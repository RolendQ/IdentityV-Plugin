package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.actions.animated.HunterSwing;
import com.roland.identityv.actions.progress.Heal;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.gameobjects.items.Wand;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
                        new HunterSwing(HunterManager.getHunter(p));
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
                            Console.log("Found heal calib");
                            CalibrationManager.get(s).hit();
                        }
                        return;
                    }

                    // HEALING CLONE (Survivor is using robot)
                    if (e.getEntityType() == EntityType.VILLAGER) {
                        Controller c = Controller.getController(e.getEntity().getEntityId());

                        // No controller or this is the robot
                        if (c == null || !c.isRobot) return;

                        Survivor clickedS = c.getSurvivor();
                        Survivor robotPlaceholder = c.getRobotPlaceholder();

                        if (clickedS != null) {
                            Console.log("Found owner of robot: "+clickedS.getPlayer().getDisplayName());

                            // HEAL
                            if (clickedS.getState() == State.NORMAL ||
                                    clickedS.getState() == State.INCAP) {
                                if (clickedS.getPlayer().getHealth() < 4) {
                                    Heal heal = new Heal(s);
                                    heal.startCloneHeal(robotPlaceholder,clickedS); // Stops self healing automatically
                                    return;
                                }
                            }
                        }
                        return;
                    }

                    if (e.getEntityType() != EntityType.PLAYER || !SurvivorManager.isSurvivor((Player) e.getEntity())) return;

                    Survivor clickedS = SurvivorManager.getSurvivor((Player) e.getEntity());

                    if (clickedS.isControllingRobot()) return; // TODO not sure if intended. This should prevent healing robots?

                    // HEAL
                    if (clickedS.getState() == State.NORMAL ||
                            clickedS.getState() == State.INCAP) {
                        if (clickedS.getHealth() < 4) {
                            Heal heal = new Heal(s);
                            heal.startHeal(clickedS); // Stops self healing automatically
                            return;
                        }
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
                    Animations.random(p.getLocation(),"animations.item","flaregun_hit",1, 6);

                    plugin.getServer().broadcastMessage(((Player) arrow.getShooter()).getDisplayName() + " shot " + p.getDisplayName());
                    HunterManager.getHunter(p).stun(Config.getInt("timers.hunter","flare_gun"));
                    e.setDamage(0); // cancel damage
                }
            }
        }

        // DAMAGER IS FIREBALL
        if (e.getDamager().getType() == EntityType.SMALL_FIREBALL) {
            SmallFireball smallFireball = (SmallFireball) e.getDamager();
            if (e.getEntityType() == EntityType.PLAYER) {
                final Player p = (Player) e.getEntity();
                // HIT SURVIVOR

                e.setDamage(0); // cancel damage

                new BukkitRunnable() {
                    public void run() {
                        p.setFireTicks(0);
                    }
                }.runTaskLater(plugin, 2);

                if (SurvivorManager.isSurvivor(p) && !SurvivorManager.getSurvivor(p).wasJustHit()) {
                    //Animations.random(p.getLocation(),"animations.item","flaregun_hit",1, 6);

                    Player hunterP = (Player) smallFireball.getShooter();

                    Survivor s = SurvivorManager.getSurvivor(p);

                    // Check if player was acting as robot
                    if (s.getItem() != null && s.getItem() instanceof Controller) {
                        Controller cont = (Controller) s.getItem();
                        if (cont.isRobot) {
                            cont.killBot();
                            return; // don't take damage
                        }
                    }

                    // Not robot
                    s.hit(HunterManager.getHunter(hunterP),2);

                    hunterP.sendMessage("Hit with fireball!");
                }
            } else if (e.getEntityType() == EntityType.VILLAGER) {
                // Villager
                Player hunterP = (Player) smallFireball.getShooter();

                //hunterP.sendMessage("Hit a clone with fireball!");
                Entity en = e.getEntity();
                // If wand
                if (Wand.removeClone(en.getEntityId(),en.getLocation())) {

                } else {
                    Controller.getController(en.getEntityId()).hit(HunterManager.getHunter(hunterP));
                }
            }
        }
    }
}
