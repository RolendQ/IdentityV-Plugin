package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.SwingManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HunterSwing {
    public IdentityV plugin;

    /**
     * Initiates the action when the hunter swings
     * @param plugin
     * @param hunter
     */
    public HunterSwing(final IdentityV plugin, final Player hunter) {
        this.plugin = plugin;

        // Make sure they are allowed to swing
        if (FreezeHandler.isFrozen(hunter) || SwingManager.getInstance().isDoingTask(hunter) || !hunter.isOnGround()) {
            return;
        }

        Console.log("Hunter is swinging");

        // Preswing animation
        Animations.one(hunter.getLocation().add(0,2,0), "animations.hunter", "preswing");

        SwingManager.getInstance().add(hunter, 40); // Delay before you can swing again or do anything

        // These attack calculations are delayed slightly
        new BukkitRunnable() {

            public void run() {
                if (FreezeHandler.isFrozen(hunter) || !hunter.isOnGround()) {
                    return;
                }

                double lowestDistance = 100;
                Player closest = null;

                // Get unit eye direction
                Vector eyeD = hunter.getEyeLocation().getDirection();
                Double multiplier = plugin.getConfig().getDouble("swingbox_multiplier");
                eyeD.setX(eyeD.getX() * multiplier);
                eyeD.setZ(eyeD.getZ() * multiplier);
                for (Entity en : hunter.getNearbyEntities(eyeD.getX(),3,eyeD.getZ())) { // Will have to adjust box
                    double distance;
                    if (en.getType() == EntityType.PLAYER && hunter.hasLineOfSight(en)) { // line of sight for now
                        distance = en.getLocation().distance(hunter.getLocation());
                        if (distance < lowestDistance && SurvivorManager.isSurvivor((Player) en)
                        && SurvivorManager.getSurvivor((Player) en).getState() != State.INCAP &&
                                SurvivorManager.getSurvivor((Player) en).getState() != State.BALLOON) {
                            lowestDistance = distance;
                            closest = (Player) en;
                        }
                    }
                }

                // Swing
                Animations.one(hunter.getLocation().add(hunter.getEyeLocation().getDirection()).add(0, 0.5, 0),"animations.hunter","swing",3);

                if (closest != null) { // Hit someone
                    Animations.multiple(hunter.getLocation().add(hunter.getEyeLocation().getDirection()).add(0, 0.5, 0),"animations.hunter","hit",5);

                    // Recover if not carrying a survivor TODO maybe make balloon hits different
                    if (hunter.getPassenger() == null) {
                        Animations.falling_rings(hunter.getLocation(), "animations.hunter", "hit_recovery", Config.getInt("timers.hunter", "hit_recovery"));
                        AttackRecoveryManager.getInstance().add(hunter, Config.getInt("timers.hunter", "hit_recovery"));
                    }
                    hunter.sendMessage("Hit!");
                    Survivor s = SurvivorManager.getSurvivor(closest);
                    s.hit(2);
                    if (s.getAction() != Action.NONE) { // Terror shock
                        s.clearActionRunnable(); // TODO maybe move this to survivor hit method for abilities?
                        s.getPlayer().getServer().broadcastMessage("Terror shock!");
                        s.getPlayer().sendTitle(ChatColor.RED + "Terror shock!","");
                        hunter.getPlayer().sendTitle(ChatColor.RED + "Terror shock!","");
                        s.hit(2);
                    }
                } else { // Add delay even if you didn't hit someone
                    Console.log("No hit.");
                    AttackRecoveryManager.getInstance().add(hunter,Config.getInt("timers.hunter","miss_recovery"));
                }



            }
        }.runTaskLater(plugin, Config.getInt("timers.hunter","swing_delay"));
    }
}
