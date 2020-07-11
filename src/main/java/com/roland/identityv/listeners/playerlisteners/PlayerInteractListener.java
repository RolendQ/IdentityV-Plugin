package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.animated.DestroyPallet;
import com.roland.identityv.actions.animated.HunterSwing;
import com.roland.identityv.actions.progress.Decode;
import com.roland.identityv.actions.progress.OpenGate;
import com.roland.identityv.actions.progress.OpenChest;
import com.roland.identityv.actions.progress.Struggle;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.*;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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

        final Player p = e.getPlayer();

        if (FreezeHandler.isFrozen(p)) { // Return if they are frozen
            e.setCancelled(true);
            return;
        }

        // LEFT CLICK ACTIONS
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (p.getGameMode() == GameMode.SURVIVAL) { // Prevents breaking blocks
                e.setCancelled(true);
            }

            // HUNTER
            if (HunterManager.isHunter(p)) {
                // SWING
                if (p.getItemInHand().getType() == Material.GOLD_SWORD) {
                    new HunterSwing(HunterManager.getHunter(p));
                    return;
                }
            }

            // SURVIVOR
            if (SurvivorManager.isSurvivor(p)) {
                Survivor s = SurvivorManager.getSurvivor(p);

                // SURVIVOR HIT CALIBRATION (CIPHER)
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.BEACON) {
                    e.setCancelled(true);
                    if (CalibrationManager.hasCalibration(s) && CalibrationManager.get(s).getType() == com.roland.identityv.enums.Action.DECODE) {
                        CalibrationManager.get(s).hit();
                    // SURVIVOR START DECODING
                    } else {
                        Cipher c = CipherManager.getCipher(e.getClickedBlock().getLocation());
                        if (c != null && !c.isDone() && s.getAction() != com.roland.identityv.enums.Action.DECODE &&
                        c.getLocation().distance(p.getLocation()) < 1.5) new Decode(s,c);
                        return;
                    }
                }

                // CHEST
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST) {
                    e.setCancelled(true);
                    Chest c = ChestManager.getChest(e.getClickedBlock().getLocation());
                    if (!s.isControllingRobot() && c != null && !c.isEmpty() && c.getOpener() == null && s.getAction() != com.roland.identityv.enums.Action.OPENCHEST &&
                            c.getLocation().distance(p.getLocation()) < 1.5) new OpenChest(s,c);
                    return;
                }

                // GATE
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.TRIPWIRE_HOOK) {
                    e.setCancelled(true);
                    Gate g = GateManager.getGate(e.getClickedBlock().getLocation());
                    if (!g.isDone() && g.getOpener() == null) new OpenGate(s,g); // must have 5 ciphers done
                    return;
                }

                // STRUGGLE
                if (s.getState() == State.BALLOON) {
                    if ((lastWorldTime / Config.getInt("timers.survivor", "struggle_limit")) != (p.getWorld().getTime() / Config.getInt("timers.survivor", "struggle_limit"))) { // Limits speed
                        new Struggle(s);
                        lastWorldTime = p.getWorld().getTime();
                        return;
                    }
                }
            }
        }

        // RIGHT CLICK ACTIONS
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            // AIR OR BLOCK
            if (SurvivorManager.isSurvivor(p)) {
                Survivor s = SurvivorManager.getSurvivor(p);
                // ITEMS
                if (p.getItemInHand() != null && ItemManager.isItem(p.getItemInHand().getType())) {
                    ItemManager.useItem(p.getItemInHand().getType(),s);
                }
            }

            // BLOCK ONLY
            if (e.getClickedBlock() == null) return;

            // RIGHT CLICK ACTIONS
            Material b = e.getClickedBlock().getType();

            if (b == Material.BEACON || b == Material.CHEST || b == Material.DAYLIGHT_DETECTOR || b == Material.DAYLIGHT_DETECTOR_INVERTED) e.setCancelled(true);

            // HUNTER DESTROY PALLET
            if (HunterManager.isHunter(p)) {
                if (b == Material.STAINED_CLAY && e.getClickedBlock().getData() == DyeColor.GREEN.getData()) {
                    for (Entity entity : e.getClickedBlock().getWorld().getNearbyEntities(e.getClickedBlock().getLocation(), 1.75, 1.75, 1.75)) {
                        if (entity.getEntityId() == p.getEntityId() && HunterManager.isHunter(p)) { // Hunter
                            Console.log("Destroying pallet");
                            new DestroyPallet(e.getClickedBlock(), HunterManager.getHunter(p));
                            return;
                        }
                    }
                    return;
                }
            }
            if (SurvivorManager.isSurvivor(p)) {

            }
        }

    }
}
