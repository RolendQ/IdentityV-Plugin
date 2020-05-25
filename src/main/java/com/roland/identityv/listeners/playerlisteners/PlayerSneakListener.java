package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.ChairPlayer;
import com.roland.identityv.actions.Vault;
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
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.DyeColor;
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
    public static BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

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
                // Look for adjacent cipher or fence
                for (int i = 0; i < PlayerSneakListener.faces.length; i++) {
                    Location loc = p.getLocation().getBlock().getRelative(PlayerSneakListener.faces[i]).getLocation();
                    if (loc.getBlock().getType() == Material.BEACON) {
                        Cipher c = CipherManager.getCipher(loc);
                        //if (c == null) Console.log("cipher is null");
                        if (c != null && !c.isDone() && s.getAction() != Action.DECODE) s.startDecode(c);
                        return;
                    }

                    if (loc.getBlock().getType() == Material.STAINED_CLAY && loc.getBlock().getData() != DyeColor.RED.getData()) {
                        new Vault(plugin, p, loc, i, Config.getInt("attributes.survivor","vault"));
                    }
                }

                // Look for nearby chaired player
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
            if (p.getPassenger() == null) {
                for (int i = 0; i < PlayerSneakListener.faces.length; i++) {
                    Location loc = p.getLocation().getBlock().getRelative(PlayerSneakListener.faces[i]).getLocation();
                    if (loc.getBlock().getType() == Material.STAINED_CLAY && loc.getBlock().getData() != DyeColor.RED.getData()) {
                        new Vault(plugin, p, loc, i, Config.getInt("attributes.hunter","vault"));
                    }
                }
            } else {
                Location loc = p.getLocation().getBlock().getLocation();
               // Console.log(loc.getBlock().getType().toString());
                if (loc.getBlock().getType() == Material.STEP) {
                    if (e.getPlayer().getPassenger() != null) { // TODO Change this later to be more specific
                        RocketChair chair = RocketChairManager.getChair(loc);
                        Console.log("Found rocket chair");
                        if (!chair.isUsed() && !chair.isOccupied()) { // Makes sure the chair is empty/not used
                            new ChairPlayer(plugin, p, (Player) p.getPassenger(), chair);
                        }
                        return;
                    }
                }

                // Drop TODO Move this to hunter class
                SurvivorManager.getSurvivor((Player) p.getPassenger()).drop();
            }
        }
    }
}
