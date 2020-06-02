package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.CancelProtectionManager;
import com.roland.identityv.managers.statusmanagers.SwingManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

        if (p.getGameMode() == GameMode.SURVIVAL) p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,99999,250),true);
        else p.removePotionEffect(PotionEffectType.JUMP);

        if (SurvivorManager.isSurvivor(p)) {
            // Survivor
            Survivor s = SurvivorManager.getSurvivor(p);
            if (s.getState() != State.NORMAL && s.getState() != State.INCAP) return; // make sure not balloon or chair, dead or free

            // Check if escaped
            if (blockBelow.getType() == Material.SEA_LANTERN) {
                s.escape();
            }

            if (!CancelProtectionManager.getInstance().isDoingTask(p)) { // Brief period where they can't cancel
                s.setAction(Action.NONE); // Resets any healing/rescuing
                if (s.clearActionRunnable()) {
                    p.sendMessage("Cancelled");
                }
            }
        } else if (HunterManager.isHunter(p)) {
            // Hunter Falling
            if (p.getFallDistance() > Config.getDouble("attributes.hunter","fall_distance_reset")) {
                //p.sendMessage("Detected fall");
                if (SwingManager.getInstance().isDoingTask(p)) {
                    AttackRecoveryManager.getInstance().remove(p);
                    SwingManager.getInstance().remove(p);
                    // TODO make animation stop too
                }
            }
        }
    }
}
