package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.gameobjects.items.Wand;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.SwingManager;
import com.roland.identityv.managers.statusmanagers.VaultManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HunterSwing {
    public IdentityV plugin;

    /**
     * Initiates the action when the hunter swings
     * @param plugin
     * @param hunter
     */
    public HunterSwing(final IdentityV plugin, final Hunter hunter) {
        this.plugin = plugin;
        final Player hunterP = hunter.getPlayer();

        // TODO There may be an issue with getting instance of swing and vault manager
        if (FreezeHandler.isFrozen(hunterP) || SwingManager.getInstance().isDoingTask(hunterP) || VaultManager.getInstance().isDoingTask(hunterP)) {
            return;
        }

        Console.log("Hunter is swinging");

        // Preswing animation
        Animations.one(hunterP.getLocation().add(0,2,0), "animations.hunter", "preswing");

        SwingManager.getInstance().add(hunterP, 40); // Delay before you can swing again or do anything

        // These attack calculations are delayed slightly
        new BukkitRunnable() {

            public void run() {
                if (FreezeHandler.isFrozen(hunterP)) { // Can attack while in air
                    return;
                }

                // If sword has enchantment
                if (hunterP.getItemInHand() != null && hunterP.getItemInHand().getEnchantments().size() > 0) {
                    foggyBlade(hunterP);
                }

                hunter.resetInvisTimer();

                double lowestDistance = 100;
                Entity closest = null;

                // Get unit eye direction
                Vector eyeD = hunterP.getEyeLocation().getDirection();
                Double multiplier = Config.getDouble("attributes.hunter","swingbox_multiplier");
                eyeD.setX(eyeD.getX() * multiplier);
                eyeD.setZ(eyeD.getZ() * multiplier);
                for (Entity en : hunterP.getNearbyEntities(eyeD.getX(),3,eyeD.getZ())) { // Will have to adjust box
                    double distance;
                    if ((en.getType() == EntityType.PLAYER || en.getType() == EntityType.VILLAGER) && hunterP.hasLineOfSight(en)) { // line of sight for now
                        distance = en.getLocation().distance(hunterP.getLocation());
                        if (distance < lowestDistance && isValidTarget(en)) {
                            lowestDistance = distance;
                            closest = en;
                        }
                    } else {
                        // Debug
                        Console.log("Not a player in line of sight");
                    }
                }

                // Swing
                Animations.one(hunterP.getLocation().add(hunterP.getEyeLocation().getDirection()).add(0, 0.5, 0),"animations.hunter","swing",3);

                if (closest != null) { // Hit someone
                    Animations.multiple(hunterP.getLocation().add(hunterP.getEyeLocation().getDirection()).add(0, 0.5, 0),"animations.hunter","hit",5);

                    // Lightning for other survivors
                    hunterP.getWorld().strikeLightningEffect(hunterP.getLocation());

                    // Recover if not carrying a survivor TODO maybe make balloon hits different
                    boolean isWand = false;

                    // Actual player
                    if (closest.getType() == EntityType.PLAYER) {
                        hunterP.sendMessage("Hit!");
                        Survivor s = SurvivorManager.getSurvivor((Player) closest);

                        // Check if player was acting as robot
                        if (s.getItem() != null && s.getItem() instanceof Controller) {
                            Controller cont = (Controller) s.getItem();
                            if (cont.isRobot) {
                                cont.killBot();
                                return; // don't take damage
                            }
                        }

                        if (hunter.hasDetention()) s.hit(hunter, 4);
                        else s.hit(hunter,2);

                        if (s.getAction() != Action.NONE) { // Terror shock
                            s.clearActionRunnable(); // TODO maybe move this to survivor hit method for abilities?
                            s.getPlayer().getServer().broadcastMessage("Terror shock!");
                            s.getPlayer().sendTitle(ChatColor.RED + "Terror shock!", "");
                            hunter.getPlayer().sendTitle(ChatColor.RED + "Terror shock!", "");
                            s.hit(hunter,2);
                        }
                    } else {
                        // Villager
                        hunterP.sendMessage("Hit a clone!");
                        // If wand
                        if (Wand.removeClone(closest.getEntityId(),closest.getLocation())) {
                            isWand = true;
                        } else {
                            Controller.getController(closest.getEntityId()).hit(hunter);

                        }
                            //Controller.removeClone(closest.getEntityId(),closest.getLocation());
                    }

                    if (hunterP.getPassenger() == null && !isWand) {
                        Animations.falling_rings(hunterP.getLocation(), "animations.hunter", "hit_recovery", Config.getInt("timers.hunter", "hit_recovery"));
                        AttackRecoveryManager.getInstance().add(hunterP, Config.getInt("timers.hunter", "hit_recovery"));
                    } else {
                        AttackRecoveryManager.getInstance().add(hunterP, Config.getInt("timers.hunter", "miss_recovery"));
                    }
                } else { // Add delay even if you didn't hit someone
                    Console.log("No hit.");
                    AttackRecoveryManager.getInstance().add(hunterP,Config.getInt("timers.hunter","miss_recovery"));
                }



            }
        }.runTaskLater(plugin, Config.getInt("timers.hunter","swing_delay"));
    }

    private void foggyBlade(final Player p) {
        Console.log("Summoned fireball");
        //final SmallFireball proj = (SmallFireball) p.getWorld().spawnEntity(p.getLocation().add(p.getLocation().getDirection()), EntityType.SMALL_FIREBALL);
        final SmallFireball proj = p.launchProjectile(SmallFireball.class);
        //proj.teleport(p.getLocation().clone().add(0,1,0));
        proj.setIsIncendiary(false);
        proj.setBounce(false);
        proj.setYield(0);

        //proj.setVelocity(new Vector(0,0,0));

        Vector dir = p.getLocation().getDirection();
        //dir.setY(0);

        proj.setVelocity(dir.multiply(0.6));
        //proj.setDirection(eyeD.normalize().multiply(0.5));

//        proj.teleport(new Location(proj.getWorld(), proj.getLocation().getX(), p.getLocation().getY() + 1.5, proj.getLocation().getZ()));
//        //proj.setVelocity(p.getLocation().getDirection().setY(0).multiply(0.3));
//        Vector dir = p.getLocation().getDirection().setY(0).multiply(0.3);
//        proj.setDirection(dir);
//        p.sendMessage("Sending at dir: "+dir);

//        new BukkitRunnable() {
//
//            public void run() {
//                if (proj.isDead()) {
//                    cancel();
//                    return;
//                }
//                proj.teleport(new Location(proj.getWorld(), proj.getLocation().getX(), p.getLocation().getY() + 1.5, proj.getLocation().getZ()));
//            }
//        }.runTaskTimer(plugin,0,1);
        proj.setShooter(p);
        HunterManager.getHunter(p).resetFoggyTimer();
    }

    private boolean isValidTarget(Entity en) {
        if (en.getType() == EntityType.VILLAGER) return true;
        Player p = (Player) en;
        Survivor s = SurvivorManager.getSurvivor(p);
        return s != null && s.getState() != State.INCAP &&
               s.getState() != State.BALLOON && s.getAction() != Action.GETHEAL;
    }
}
