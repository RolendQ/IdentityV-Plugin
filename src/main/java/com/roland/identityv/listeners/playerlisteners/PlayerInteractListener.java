package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.DestroyPallet;
import com.roland.identityv.actions.DropPallet;
import com.roland.identityv.actions.HunterSwing;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.CipherManager;
import com.roland.identityv.managers.gamecompmanagers.RocketChairManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private IdentityV plugin;
    private long lastWorldTime = 0;

    public PlayerInteractListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player clicks on air or a block
     * @param e
     */
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();

        Player p = e.getPlayer();

        if (FreezeHandler.isFrozen(p)) { // Return if they are frozen
            return;
        }

        // ATTACK
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Hunter swing
            if (p.getItemInHand().getType() == Material.GOLD_SWORD) {
                new HunterSwing(plugin,p);
                return;
            }

            // Survivor struggle
            if (SurvivorManager.isSurvivor(p) && SurvivorManager.getSurvivor(p).getState() == State.BALLOON) {
                if ((lastWorldTime / Config.getInt("timers.survivor","struggle_limit")) != (p.getWorld().getTime() / Config.getInt("timers.survivor","struggle_limit"))) { // Limits speed
                    Console.log("Struggling: " + SurvivorManager.getSurvivor(p).getStruggleProgress());
                    SurvivorManager.getSurvivor(p).struggle();
                    lastWorldTime = p.getWorld().getTime();
                    return;
                }
            }
        }

        if (e.getClickedBlock() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        // RIGHT CLICK ACTIONS
        Material b = e.getClickedBlock().getType();

        // HUNTER DESTROY PALLET
        if (b == Material.COBBLE_WALL) {
            for (Entity entity : e.getClickedBlock().getWorld().getNearbyEntities(e.getClickedBlock().getLocation(),1,1,1)) {
                if (entity.getEntityId() == p.getEntityId() && !SurvivorManager.isSurvivor(p)) { // Hunter
                    new DestroyPallet(plugin, e.getClickedBlock(), p);
                    return;
                }
            }
            return;
        }

        // SURVIVOR DROP PALLET
        if (b == Material.WALL_BANNER) {
            Banner banner = (Banner) e.getClickedBlock().getState();
            if (banner.getBaseColor() == DyeColor.GREEN) {
                for (Entity entity : banner.getWorld().getNearbyEntities(banner.getLocation(),2,2,2)) {
                    if (entity.getEntityId() == p.getEntityId() && SurvivorManager.isSurvivor(p) && SurvivorManager.getSurvivor(p).getState() == State.NORMAL) { // Survivor
                        new DropPallet(plugin, e.getClickedBlock().getRelative(BlockFace.DOWN));
                        return;
                    }
                }
            }
            return;
        }

        // CHECK CIPHER PROGRESS
        if (b == Material.JUKEBOX) {
            Cipher cipher = CipherManager.getCipher(e.getClickedBlock().getLocation());
            if (cipher != null && SurvivorManager.isSurvivor(p) && SurvivorManager.getSurvivor(p).getState() == State.NORMAL) { // Survivor
                cipher.notify(p);
            }
        }
    }
}
