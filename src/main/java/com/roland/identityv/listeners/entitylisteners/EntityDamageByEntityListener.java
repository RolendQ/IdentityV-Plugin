package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.actions.HunterSwing;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageByEntityListener implements Listener {

    private IdentityV plugin;

    public EntityDamageByEntityListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when entities are damaged by other entities (used for hunter swings)
     * @param e
     */
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        Console.log("Damaged by " + e.getDamager().getName());

        // DAMAGER IS PLAYER
        if (e.getDamager().getType() == EntityType.PLAYER) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Player p = (Player) e.getDamager();

                e.setCancelled(true);

                if (FreezeHandler.isFrozen(p)) { // Return if they are frozen
                    return;
                }

                // Hunter swing
                if (HunterManager.isHunter(p)) {
                    if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.GOLD_SWORD) {
                        new HunterSwing(plugin, HunterManager.getHunter(p));
                        e.setCancelled(true);
                        return;
                    }
                    return;
                }

                if (SurvivorManager.isSurvivor(p)) {
                    Survivor s = SurvivorManager.getSurvivor(p);

                    // HEAL CALIBRATION
                    if (s.getAction() == com.roland.identityv.enums.Action.HEAL) {
                        if (CalibrationManager.hasCalibration(s) && CalibrationManager.get(s).getType() == com.roland.identityv.enums.Action.HEAL) {
                            CalibrationManager.get(s).hit();
                        }
                        return;
                    }
                }
            }
        }

        // DAMAGER IS ARROW
        if (e.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) e.getDamager();
            if (e.getEntityType() == EntityType.PLAYER) {
                Player p = (Player) e.getEntity();
                // STUN HUNTER
                if (HunterManager.isHunter(p)) {
                    plugin.getServer().broadcastMessage(((Player) arrow.getShooter()).getDisplayName() + " shot " + p.getDisplayName());
                    Animations.falling_rings(p.getLocation().add(0,1,0),"animations.hunter","stun_recovery", Config.getInt("timers.hunter","flare_gun"));
                    FreezeActionManager.getInstance().add(p, Config.getInt("timers.hunter","flare_gun"));
                    e.setDamage(0); // cancel damage
                }
            }
        }
    }
}
