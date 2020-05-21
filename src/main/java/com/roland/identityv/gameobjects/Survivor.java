package com.roland.identityv.gameobjects;

import com.roland.identityv.actions.StruggleFree;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.managers.statusmanagers.freeze.StruggleRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Survivor object (handles all events and actions involving a survivor)
 */
public class Survivor {

    public Game game;

    public Player player;
    public Player hunter;
//    public boolean isIncapacitated;
//    public boolean isChaired;
    public int state; // enum
    public int action; // enum
    public int struggleProgress;
    public IdentityV plugin;
    public int timesOnChair; // 0 is start. 1 is before half. 2 is after half (dead on chair)
    public int bleedOutTimer;
    public int chairTimer;
    public int healingProgress;
    public int rescuingProgress;

    public long lastHeartbeat;
    public int selfHeal;

    // Scoreboard
    public int line;

    public BukkitRunnable actionRunnable;

    // Maybe add character here

    public Survivor(IdentityV plugin, Player player) {
        this(plugin, player, null); // Won't be able to open gates
    }

    public Survivor(IdentityV plugin, Player player, Game game) {
        this.plugin = plugin;
        this.player = player;
        this.game = game;

        actionRunnable = null;
        state = State.NORMAL;
        action = Action.NONE;
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(4);
        player.setHealthScale(4);
        player.setFoodLevel(2);
        player.setSaturation(1000);

        lastHeartbeat = 0;
        struggleProgress = 0;
        bleedOutTimer = 0; // config to set limit
        chairTimer = 0;
        selfHeal = 1;

        line = (SurvivorManager.getSurvivors().size()*2) + 2; // Line for their name
//        ScoreboardUtil.set("&a"+player.getDisplayName(), getNameLine());
//        ScoreboardUtil.setBar(1,"a",getBarLine());
    }

    public int getNameLine() {
        return line;
    }

    public int getBarLine() {
        return line-1;
    }

    public Player getPlayer() {
        return player;
    }

    public void struggle() {
        struggleProgress += 1;
        player.setExp((float) struggleProgress / (float) Config.getInt("timers.survivor","struggle"));

        if (struggleProgress % Config.getInt("timers.survivor","struggle_tilt_interval") == 0) { // Make player tilt
            Console.log("Tilting "+hunter.getDisplayName()+": "+hunter.getLocation().getYaw());


            Location newYaw = hunter.getLocation().clone();
            Random r = new Random();
            int chance = r.nextInt(2);
            int angle = Config.getInt("attributes.hunter","struggle_tilt_angle");
            if (chance == 0) newYaw.setYaw(newYaw.getYaw() + 60);
            else newYaw.setYaw(newYaw.getYaw() - 60);

            hunter.eject(); // temporarily so they can teleport
            hunter.teleport(newYaw);
        }
        if (player.getExp() == 1) {
            new StruggleFree(plugin,hunter,player);
            struggleProgress = 0;
            player.setExp(0);
            hunter = null;
        }
    }

    public void drop() {
        plugin.getServer().broadcastMessage(player.getDisplayName() + " was dropped");
//        ScoreboardUtil.set("&e"+player.getDisplayName(), getNameLine());
//        ScoreboardUtil.setBar((float)bleedOutTimer / (float) Config.getInt("timers.survivor","bleed"),"e",getBarLine());
        setState(State.INCAP);
        struggleProgress += 3;
        player.setExp(0);
        SitHandler.unsit(player);
    }

    public Player getHunter() { return hunter; }

    public void setHunter(Player hunter) { this.hunter = hunter; }

    public void incTimesOnChair() {
        timesOnChair += 1;
    }

    public int getTimesOnChair() {
        return timesOnChair;
    }

    public void incapacitate() {
//        ScoreboardUtil.set("&e"+player.getDisplayName(), getNameLine());
//        ScoreboardUtil.setBar((float)bleedOutTimer / (float) Config.getInt("timers.survivor","bleed"),"e",getBarLine());

        Animations.decreasing_ring(player.getLocation(),"animations.survivor","incap",2,40);
        player.removePotionEffect(PotionEffectType.SPEED); // remove any speed boost
        plugin.getServer().broadcastMessage(player.getDisplayName() + " was incapacitated!");

        //IncapacitationManager.getInstance().add(player, 200); // 10 seconds for now
        healingProgress = 0;
        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","incap"));
        player.setHealth(1);
        state = State.INCAP;

        player.getWorld().playEffect(player.getLocation(), Effect.CRIT, 1, 10);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAction() { return action; }

    public void setAction(int action) { this.action = action;}

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public void incBleedOutTimer() {
        bleedOutTimer += 1;
        Animations.random(player.getLocation(),"animations.survivor","bleed",1.5,3);
    }

    public int getBleedOutTimer() { return bleedOutTimer;}

    public int getChairTimer() { return chairTimer;}

    public void incChairTimer() {
        chairTimer += 1;
    }

    public void death() {
        if (state != State.DEAD) {
            state = State.DEAD;
            player.setGameMode(GameMode.SPECTATOR);
            Animations.one(player.getLocation(),"animations.survivor","death",12);
            plugin.getServer().broadcastMessage(player.getDisplayName() + " died");
        }
    }

    public int getStruggleProgress() {
        return struggleProgress;
    }

    public int getHealingProgress() {
        return healingProgress;
    }

    public void incHealingProgress() {
        healingProgress += 1;
        Animations.random(player.getLocation(),"animations.survivor","heal",1.5,3);
    }

    public int getRescuingProgress() { return rescuingProgress; }

    public void incRescuingProgress() {
        rescuingProgress += 1;
        Animations.ring(player.getLocation(),"animations.survivor","rescue",2);
    }

    public int getHealth() {
        return (int) player.getHealth();
    }

    public void hit(int damage) {
        if (state != State.NORMAL) return; // chair

        healingProgress = 0; // Reset healing progress

        // Incapacitate instead of vanilla dying
        if (damage >= player.getHealth()) { // If they will die
            player.damage(0.001); // for animation?
            incapacitate();
            return;
        }

        player.damage(damage);

        // Speed boost
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Config.getInt("attributes.survivor","hit_boost_length"), 1, true),true);
    }

    public void startSelfHeal() {
        Console.log("Starting self heal");
        setAction(Action.SELFHEAL);

        actionRunnable = new BukkitRunnable() {
            public void run() {
                if (getAction() != Action.SELFHEAL) { // Player cleared it
                    clearActionRunnable();
                    return;
                }

                // Check if they can self heal and past limit
                if (getState() == State.INCAP && healingProgress >= Config.getInt("timers.survivor","self_heal_limit") && selfHeal == 0) {
                    player.sendMessage("Cannot progress anymore due to self heal limit");
                    clearActionRunnable();
                    return;
                }

                incHealingProgress();


                // Heal (syringe) TODO
                if (getState() == State.NORMAL) {
                    player.setExp((float) healingProgress / (float) Config.getInt("timers.survivor","heal"));
                    if (player.getExp() == 1) { // Finished healing
                        player.setHealth(getPlayer().getHealth() + 2);
                        setHealingProgress(0);
                        player.sendMessage("You have healed yourself");
                        clearActionRunnable();
                    }
                    return;
                }
                // Self revive
                if (getState() == State.INCAP) {
                    player.setExp((float) healingProgress / (float) Config.getInt("timers.survivor","revive"));
                    if (player.getExp() == 1) { // Finished reviving
                        selfHeal -= 1;
                        player.setHealth(2);
                        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                        setHealingProgress(0);
                        setState(State.NORMAL);
                        player.sendMessage("You have revived yourself");
                        clearActionRunnable();
                    }
                    return;
                }
            }
        };
        actionRunnable.runTaskTimer(plugin, 5, 15); // slower
    }

    public void startHeal(final Survivor injured) {
        Console.log("Starting heal of "+injured.getPlayer().getDisplayName());
        setAction(Action.HEAL);
        injured.setAction(Action.GETHEAL);

        actionRunnable = new BukkitRunnable() {
            public void run() {
                if (injured.getAction() != Action.GETHEAL) { // Player cleared it
                    clearActionRunnable();
                    return;
                }
                injured.incHealingProgress();

                // Heal
                if (injured.getState() == State.NORMAL) {
                    player.setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                    injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                    if (player.getExp() == 1) { // Finished healing
                        injured.getPlayer().setHealth(injured.getPlayer().getHealth() + 2);
                        injured.setHealingProgress(0);
                        injured.getPlayer().sendMessage("You have been healed");
                        injured.clearActionRunnable();
                        clearActionRunnable();
                    }
                    return;
                }
                // Revive
                if (injured.getState() == State.INCAP) {
                    player.setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                    injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                    if (player.getExp() == 1) { // Finished reviving
                        injured.getPlayer().setHealth(2);
                        injured.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                        injured.setHealingProgress(0);
                        injured.setState(State.NORMAL);
                        injured.getPlayer().sendMessage("You have been revived");
                        injured.clearActionRunnable();
                        clearActionRunnable();
                    }
                    return;
                }
            }
        };
        actionRunnable.runTaskTimer(plugin, 5, 10);
    }

    public void startRescue(final Survivor chaired) {
        setAction(Action.RESCUE);
        chaired.setRescuingProgress(0); // Reset rescuing progress
        //injured.setAction(Action.GETHEAL);

        actionRunnable = new BukkitRunnable() {
            public void run() {
                chaired.incRescuingProgress();

                player.setExp((float) chaired.getRescuingProgress() / (float) Config.getInt("timers.survivor","rescue"));
                chaired.getPlayer().setExp((float) chaired.getRescuingProgress() / (float) Config.getInt("timers.survivor","rescue"));

                if (player.getExp() == 1) { // Finished rescuing
                    SitHandler.unsit(chaired.getPlayer());
                    chaired.getPlayer().setHealth(2);
                    chaired.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                    chaired.setRescuingProgress(0);
                    chaired.setState(State.NORMAL);
                    chaired.getPlayer().sendMessage("You have been rescued");
                    chaired.clearActionRunnable();
                    clearActionRunnable();
                }
            }
        };
        actionRunnable.runTaskTimer(plugin, 5, 5);
    }

    public void startDecode(final Cipher cipher) {
        setAction(Action.DECODE);
        Console.log("Start decode");

        actionRunnable = new BukkitRunnable() {
            public void run() {
                cipher.decodeBit(1);
                //if (cipher.getProgress() % 10 == 0) cipher.notify(player);
                player.setExp((float) cipher.getProgress() / (float) Config.getInt("timers.survivor","decode"));
                if (player.getExp() == 1) {
                    cipher.pop();
                    player.sendMessage("You have finished this cipher");
                    clearActionRunnable();
                }
            }
        };
        actionRunnable.runTaskTimer(plugin, 5, 10);
    }

    public void startOpen(final Gate gate) {
        if (game != null && game.getCiphersDone() < 5) return; // need to do 5 ciphers

        setAction(Action.DECODE); // same as ciphers
        Console.log("Start open gate");

        actionRunnable = new BukkitRunnable() {
            public void run() {
                gate.openBit(1);
                player.setExp((float) gate.getProgress() / (float) Config.getInt("timers.survivor","open_gate"));
                if (player.getExp() == 1) {
                    gate.open();
                    player.sendMessage("You have opened the gate");
                    clearActionRunnable();
                }
            }
        };
        actionRunnable.runTaskTimer(plugin, 5, 10);
    }

    private void setRescuingProgress(int rescuingProgress) {
        this.rescuingProgress = rescuingProgress;
    }

    public void setHealingProgress(int healingProgress) {
        this.healingProgress = healingProgress;
    }

    public boolean clearActionRunnable() {
        player.setExp(0);
        setAction(Action.NONE);
        if (actionRunnable != null) {
            actionRunnable.cancel();
            actionRunnable = null;
            return true;
        }
        return false;
    }

    public int getSelfHeal() {
        return selfHeal;
    }
}

