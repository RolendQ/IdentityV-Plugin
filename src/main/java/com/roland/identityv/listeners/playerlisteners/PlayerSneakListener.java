package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.ChairPlayer;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Gate;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.CipherManager;
import com.roland.identityv.managers.gamecompmanagers.GateManager;
import com.roland.identityv.managers.gamecompmanagers.RocketChairManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerSneakListener implements Listener {

    public IdentityV plugin;

    public PlayerSneakListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player sneaks or unsneaks
     * @param e
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        if (!p.isSneaking()) return;

        // Survivor
        if (SurvivorManager.isSurvivor(p)) {
            // NORMAL state
            Survivor s = SurvivorManager.getSurvivor(p);
            if (s.getState() == State.NORMAL) {
                // Look for adjacent cipher
                BlockFace[] faces = {BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH};
                for (BlockFace face : faces) {
                    Location loc = p.getLocation().getBlock().getRelative(face).getLocation();
                    if (loc.getBlock().getType() == Material.JUKEBOX) {
                        Cipher c = CipherManager.getCipher(loc);
                        //if (c == null) Console.log("cipher is null");
                        if (c != null && !c.isDone() && s.getAction() != Action.DECODE) s.startDecode(c);
                        return;
                    }
                }

                // Look for adjacent chaired player
                for (Entity entity : p.getLocation().getWorld().getNearbyEntities(p.getLocation(),2,2,2)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Survivor s2 = SurvivorManager.getSurvivor((Player) entity);
                        if (s2 != null && s2.getState() == State.CHAIR) {
                            s.startRescue(s2);
                            return;
                        }
                    }
                }

                // Look for touching gate pad
                // TODO make sure 5 ciphers popped
                Location head = p.getLocation().getBlock().getRelative(BlockFace.UP).getLocation();
                if (p.getWorld().getBlockAt(head).getType() == Material.TRIPWIRE_HOOK) {
                    Console.log("Found gate");
                    Gate g = GateManager.getGate(head);
                    if (!g.isDone()) s.startOpen(g);
                    return;
                }
            }
            // INCAP
            else if (s.getState() == State.INCAP) {
                if (s.getAction() != Action.SELFHEAL) { // Self heal
                    s.startSelfHeal();
                }
            }
        } else {
            // Hunter
            if (p.isSneaking() && p.getPassenger() != null) {
                Location loc = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                if (loc.getBlock().getType() == Material.IRON_BLOCK) {
                    if (e.getPlayer().getPassenger() != null) { // TODO Change this later to be more specific
                        RocketChair chair = RocketChairManager.getChair(p.getLocation());
                        Console.log("Found rocket chair");

                        new ChairPlayer(plugin, p, (Player) p.getPassenger(), chair);
                        return;
                    }
                }

                // Drop TODO Move this to hunter class
                SurvivorManager.getSurvivor((Player) p.getPassenger()).drop();
            }
        }
    }
}
