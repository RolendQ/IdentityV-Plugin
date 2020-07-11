package com.roland.identityv.gameobjects.items;

import com.roland.identityv.actions.progress.Decode;
import com.roland.identityv.actions.progress.Heal;
import com.roland.identityv.actions.progress.OpenGate;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Gate;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.utils.*;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Controller extends Item {
    public Location entityLoc;
    public boolean isRobot;
    public BukkitRunnable robotsTask;
    public int robotsAction;
    public Integer villagerID;
    public Cipher robotCipher;
    public double cipherProgressAtStart;
    public Survivor injured;
    public Gate robotGate;
    public Survivor robotPlaceholder;

    public double survivorHealth;

    public static HashMap<Integer,Integer> clones = new HashMap<Integer,Integer>();

    public static ItemStack[] botArmor = {new ItemStack(Material.CHAINMAIL_BOOTS, 1),
            new ItemStack(Material.CHAINMAIL_LEGGINGS, 1),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1),
            new ItemStack(Material.CHAINMAIL_HELMET, 1)};

    public Controller(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
        this.task = null; // Durability task
        this.entityLoc = s.getPlayer().getLocation().clone(); // Entity/Clone's location (Not necessarily the robot)
        this.isRobot = false;
        this.robotsTask = null;
        this.injured = null;
        this.robotGate = null;
        this.robotPlaceholder = new Survivor(s, entityLoc, s.game);
        this.survivorHealth = p.getHealth();
    }


    public boolean use() {
        if (entityLoc == null) { // No robot in game
            // Prepare to summon a robot
            entityLoc = s.getPlayer().getLocation().clone();
        }
        // Switch to either robot or player
        if (isRobot) {
            isRobot = false;

            // Cancel durability decrease
            task.cancel();

            // Update healing progress
            s.setHealingProgress(robotPlaceholder.getHealingProgress());

            // Set health
//            p = s.getPlayer();
//            p.setMaxHealth(4);
//            p.setHealth(survivorHealth);
            p.removePotionEffect(PotionEffectType.WITHER);

            // Check if player (in robot) was decoding, healing, or opening gate
            if (s.getAction() == Action.DECODE) {
                Console.log("Set robot's task");
                robotCipher = CipherManager.getCipherFromSurvivor(s);
                cipherProgressAtStart = s.getCipherProgressAtStart(); // decode with surv's start progress
                robotsAction = Action.DECODE;
                robotCipher.addSurvivor(robotPlaceholder); // So it tracks the right number of decoders

                // Transfer any existing calibration
                if (CalibrationManager.hasCalibration(s) && !CalibrationManager.get(s).getBeingRemoved()) {
                    Console.log("Transferred calib from survivor to robot");
                    CalibrationManager.get(s).setSurvivor(robotPlaceholder);
                }


                robotsTask = new BukkitRunnable() {
                    public void run() {
                        // If 5 ciphers are done
                        if (s.game.getCiphersDone() == 5) {
                            clearRobotTask();
                            if (CalibrationManager.hasCalibration(robotPlaceholder)) {
                                CalibrationManager.get(robotPlaceholder).finish();
                            }
                        }

                        // Progress depends on number of decoders
                        robotCipher.incProgress(Adjustments.getDecodeRate(robotCipher.getSurvivorsDecoding().size()));

                        // TODO could be more like the game but requires getProgressAtStart() for Survivor
                        if (robotCipher.getProgress() - cipherProgressAtStart > 40) { // Window for calibration
                            Random r = new Random();
                            if (r.nextInt(6) == 0) {

                                if (!CalibrationManager.hasCalibration(robotPlaceholder)) CalibrationManager.give(robotPlaceholder, Action.DECODE);

                            }
                        }
                        float progress = (float) robotCipher.getProgress() / (float) Config.getInt("timers.survivor","decode");
                        if (progress >= 1) {
                            robotCipher.pop();
                            s.getPlayer().sendMessage("Robot has finished a cipher");
                            if (CalibrationManager.hasCalibration(robotPlaceholder)) {
                                CalibrationManager.get(robotPlaceholder).finish();
                            }
                            clearRobotTask();
                        }
                    }
                };
                robotsTask.runTaskTimer(plugin, 5, 10);

                //s.clearActionRunnable(true); // Now clear survivor's decoding
            } else if (s.getAction() == Action.HEAL && s.getTarget() != robotPlaceholder) { // Can't heal self without using the bot
                Console.log("Bot continues healing");
                injured = s.getTarget();
                robotsAction = Action.HEAL;
                robotsTask = new BukkitRunnable() {
                    public void run() {
                        if (injured.getAction() != Action.GETHEAL) { // Player cleared it
                            Console.log("Injured player cleared it");
                            clearRobotTask();
                            return;
                        }
                        injured.incHealingProgress(10);

                        // Heal
                        if (injured.getState() == State.NORMAL) {
                            injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                            if (injured.getPlayer().getExp() >= 1) { // Finished healing
                                injured.getPlayer().setHealth(injured.getPlayer().getHealth() + 2);
                                injured.setHealingProgress(0);
                                injured.getPlayer().sendMessage("You have been healed");
                                injured.clearActionRunnable();
                                clearRobotTask();
                            }
                            return;
                        }
                        // Revive
                        if (injured.getState() == State.INCAP) {
                            injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                            if (injured.getPlayer().getExp() >= 1) { // Finished reviving
                                injured.getPlayer().setHealth(2);
                                injured.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                                injured.setHealingProgress(0);
                                injured.setState(State.NORMAL);
                                injured.getPlayer().sendMessage("You have been revived");
                                injured.clearActionRunnable();
                                clearRobotTask();
                            }
                            return;
                        }
                    }
                };
                robotsTask.runTaskTimer(plugin, 5, 10);

                //s.clearActionRunnable(true); // Now clear survivor's healing

            } else if (s.getAction() == Action.OPEN) {
                robotsAction = Action.OPEN;
                robotGate = GateManager.getGateFromSurvivor(s);
                robotsTask = new BukkitRunnable() {
                    public void run() {
                        robotGate.incProgress(10);
                        double progress = (float) robotGate.getProgress() / (float) Config.getInt("timers.survivor","open_gate");
                        if (progress >= 1) {
                            robotGate.open();
                            p.sendMessage("Robot opened the gate");
                            clearRobotTask();
                        }
                    }
                };
                robotsTask.runTaskTimer(plugin, 5, 10);

                //s.clearActionRunnable(true);

                robotGate.setOpener(robotPlaceholder); // So others can't open
            }

            s.clearActionRunnable(true); // Now clear survivor's actions

            // If body was being healed
            if (robotPlaceholder.getAction() == Action.GETHEAL) {
                robotPlaceholder.clearActionRunnable(true);
                if (robotPlaceholder.getHealer() == null) {
                    Console.log("Healer is null");
                } else if (robotPlaceholder.getHealer() != s) {// Stop healing yourself
                    Heal heal = new Heal(robotPlaceholder.getHealer());
                    heal.startHeal(s); // Start healing again
                }
            }


            swap();

            // Return armor
            p.getInventory().setArmorContents(s.getArmor());
        } else {
            isRobot = true;

            // Update robotPlaceholder
            robotPlaceholder.setHealingProgress(s.getHealingProgress());

            // Set health
            p = s.getPlayer();
            //survivorHealth = p.getHealth();
            //Console.log("Set survivor health: "+survivorHealth);
            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 9999, 0, true, false),true);

            // Clear action here
            s.clearActionRunnable();

            if (task != null) task.cancel();

            // Durability task
            task = new BukkitRunnable() {
                public void run() {
                    ItemStack itemStack = p.getItemInHand(); // TODO if not controller, cancel

                    if (itemStack == null) return;

                    int newDur = itemStack.getDurability() + Config.getInt("attributes.item", "controller_durability");
                    itemStack.setDurability((short) newDur);
                    p.updateInventory();
                    if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                        p.sendMessage("Your controller ran out");
                        reduceItem();
                        killBot();
                    }
                    // Can also add particles to clone

                    if (entityLoc != null) {
                        Animations.ring(entityLoc.clone().add(0, 1, 0), "animations.item", "controller_use", 1);
                    }
                }
            };
            task.runTaskTimer(plugin,0,5);

            // Check if robot was decoding, healing, or opening gate
            if (robotsTask != null) {
                Console.log("Taking robot's task");
                if (robotsAction == Action.DECODE) {
                    new Decode(s,robotCipher, cipherProgressAtStart); // decode with robot's start progress

                    // Transfer any existing calibration
                    if (CalibrationManager.hasCalibration(robotPlaceholder) && !CalibrationManager.get(robotPlaceholder).getBeingRemoved()) {
                        Console.log("Transferred calib from robot to surv");
                        CalibrationManager.get(robotPlaceholder).setSurvivor(s);
                    }
                } else if (robotsAction == Action.HEAL) {
                    Heal heal = new Heal(s);
                    heal.startHeal(injured); // continue healing
                } else if (robotsAction == Action.OPEN) {
                    new OpenGate(s,robotGate);
                }
                clearRobotTask();
            }

            swap();

            // Set armor of player
            p.getInventory().setArmorContents(botArmor);
        }


        return true;
    }

    public void clearRobotTask() {
        if (robotsTask != null) {
            Console.log("Cleared robot's task");
            robotsTask.cancel();
            robotsAction = Action.NONE;
            robotsTask = null;
            // Clear robot from cipher or gate
            if (robotCipher != null) {
                CipherManager.removeDecodingSurvivor(robotPlaceholder);
                robotCipher = null;
            }
            if (robotGate != null) {
                robotGate.setOpener(null);
                robotGate = null;
            }
            injured = null;
        }
    }

    public void swap() {
        if (villagerID != null) {
            // Remove existing entity
            Controller.removeClone(villagerID, entityLoc);
        }

        // Create new entity at player's location
        final Location location = p.getLocation().clone();
        //p.getWorld().getBlockAt(p.getLocation()).setType(Material.BARRIER);
        final LivingEntity v = (LivingEntity) p.getWorld().spawnEntity(location, EntityType.VILLAGER);
        v.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 0, true, false),true);

        new BukkitRunnable() {
            public void run() {
                if (v.isValid()) {
                    v.teleport(location);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0,5);

        EntityPlayer npc;
        if (!isRobot) {
            npc = NPCs.spawnNPC(p, location);
        } else npc = NPCs.spawnNPC(p, location.clone().add(0,-0.3,0)); // a little lower

        villagerID = v.getEntityId();
        clones.put(villagerID,npc.getId());

        // Swap
        p.teleport(entityLoc);
        entityLoc = v.getLocation();
    }

    public void killBot() {
        if (p == null || entityLoc == null || villagerID == null || task == null) return; // offline or something?
        Console.log("Killing bot");

        clearRobotTask();
        if (isRobot) {
            isRobot = false;
            task.cancel(); // durability

            s.setHealingProgress(robotPlaceholder.getHealingProgress());

            p.removePotionEffect(PotionEffectType.WITHER);

            p.teleport(entityLoc);
            p.getInventory().setArmorContents(s.getArmor());

            CalibrationManager.removeAfterDelay(s);

        } else {
            // Remove any calibrations because bot died
            CalibrationManager.removeAfterDelay(robotPlaceholder); // TODO doesn't seem to work effectivel?
        }

        Controller.removeClone(villagerID, entityLoc);
        entityLoc = null;

        // Durability
        ItemStack itemStack = p.getItemInHand(); // TODO make sure is controller

        if (itemStack != null && itemStack.getDurability() < itemStack.getType().getMaxDurability()) {
            int newDur = itemStack.getDurability() + 20 * Config.getInt("attributes.item", "controller_durability");
            itemStack.setDurability((short) newDur);
            p.updateInventory();
            if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                p.sendMessage("Your controller ran out");
                reduceItem();
            }
        }
    }

    // Removes any clone
    public static void removeClone(Integer villagerID, Location loc) {
        if (!clones.containsKey(villagerID)) return;
        // Delete villager
        for (Entity en : loc.getWorld().getNearbyEntities(loc,3,3,3)) {
            if (en.getEntityId() == villagerID) {
                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                en.remove();
                for (Player p : IdentityV.plugin.getServer().getOnlinePlayers()) {
                    final PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                    PacketPlayOutEntityDestroy pa = new PacketPlayOutEntityDestroy(clones.get(villagerID));
                    connection.sendPacket(pa);
                }
                clones.remove(villagerID);
                return;
            }
        }
    }

    public Integer getVillagerID() {
        return villagerID;
    }

    public Location getEntityLoc() {
        return entityLoc;
    }

    public static ArrayList<Location> getEntityLocs() {
        ArrayList<Location> entityLocs = new ArrayList<Location>();
        for (Survivor s : SurvivorManager.getSurvivors()) {
            Item item = s.getItem();
            if (item != null && item instanceof Controller) {
                Controller controller = (Controller) item;
                if (controller.getEntityLoc() != null) {
                    entityLocs.add(controller.getEntityLoc());
                }
            }
        }
        return entityLocs;
    }

    public static Controller getController(Integer villagerID) {
        for (Survivor s : SurvivorManager.getSurvivors()) {
            Item item = s.getItem();
            if (item != null && item instanceof Controller) {
                Controller controller = (Controller) item;
                if (controller.getVillagerID().equals(villagerID)) {
                    Console.log("Found controller");
                    return controller;
                }
            }
        }
        return null;
    }

    public static Survivor getOwner(Integer villagerID) {
        for (Survivor s : SurvivorManager.getSurvivors()) {
            Item item = s.getItem();
            if (item != null && item instanceof Controller) {
                Controller controller = (Controller) item;
                if (controller.getVillagerID().equals(villagerID)) {
                    return s;
                }
            }
        }
        return null;
    }

    public void hit(Hunter h) {
        Console.log("Clone was hit!");
        if (isRobot) {
            use(); // Forces you to go back
            s.hit(h,2); // Cannot terror shock
        }
        else {
            killBot();

            // Alert hunters after small delay
            new BukkitRunnable() {
                public void run() {
                    for (Hunter h : HunterManager.getHunters()) { // Alerts all
                        Holograms.alert(h.getPlayer(), s.getPlayer().getLocation(), 20);
                    }
                }
            }.runTaskLater(plugin, 40);
        }
    }

    @Override
    public int getCD() {
        return 1;
    }

    public Survivor getRobotPlaceholder() {
        return robotPlaceholder;
    }

    public void setSurvivorHealth(double survivorHealth) {
        this.survivorHealth = survivorHealth;
    }

    public double getSurvivorHealth() {
        return survivorHealth;
    }

    @Override
    public Material getMat() {
        return Material.IRON_PICKAXE;
    }
}
