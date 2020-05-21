package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerMoveListener implements Listener {
    private IdentityV plugin;

    public PlayerMoveListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player moves (even looking)
     * @param e
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (Math.abs(e.getTo().getY() - e.getFrom().getY()) < 0.04 && Math.abs(e.getTo().getX() - e.getFrom().getX()) < 0.04 &&
                Math.abs(e.getTo().getZ() - e.getFrom().getZ()) < 0.04) return;

        //Console.log("Actual movement");

        Block blockBelow = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (blockBelow.getType() == Material.STAINED_CLAY) {
            if (SurvivorManager.isSurvivor(p)) {
                if (SurvivorManager.getSurvivor(p).getState() == State.NORMAL) {
                    // Must be survivor and normal state to vault
                    p.removePotionEffect(PotionEffectType.JUMP);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 0), true);
                }
            } else if (blockBelow.getData() == DyeColor.BLUE.getData()) {
                p.removePotionEffect(PotionEffectType.JUMP);
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 0), true);
            }
        }
        else p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,99999,250),true);

        if (SurvivorManager.isSurvivor(p)) {
            Survivor s = SurvivorManager.getSurvivor(p);
            if (s.getState() != State.NORMAL && s.getState() != State.INCAP) return; // make sure not balloon or chair

            if (p.isOnGround() && blockBelow.getType() != Material.FENCE
                    && blockBelow.getType() != Material.COBBLE_WALL) {
                if (s.getAction() != Action.NONE) {
                    p.sendMessage("Cancelled action");
                }
                s.setAction(Action.NONE); // Resets any healing/rescuing
                if (s.clearActionRunnable()) {
                    p.sendMessage("Cancelled");
                }
            } else {
                if (s.getAction() != Action.VAULT && blockBelow.getType() == Material.STAINED_CLAY) { // must be clay
                    Console.log("Detected jump");
                    p.sendMessage("You are vaulting");
                    s.setAction(Action.VAULT);

                    // Sound
                    for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(),20,20,20)) {
                        if (entity.getType() == EntityType.PLAYER) {
                            Player p2 = (Player) entity;
                            if (!SurvivorManager.isSurvivor(p)) { // Hunter
                                Holograms.alert(p2,p.getLocation());
                            }
                        }
                    }
                }
            }
        }
    }
}