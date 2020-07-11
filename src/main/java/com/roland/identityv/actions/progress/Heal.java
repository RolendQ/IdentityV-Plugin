package com.roland.identityv.actions.progress;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Heal {
    public Survivor s;
    public Player player;
    public Heal(Survivor s) {
        this.s = s;
        this.player = s.getPlayer();
    }

    public void startSelfHeal() {
        // NOTE: No calibrations
        Console.log("Starting self heal");
        s.setAction(Action.SELFHEAL);

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                if (s.getAction() != Action.SELFHEAL) { // Player cleared it
                    s.clearActionRunnable();
                    return;
                }

                // [INCAP] Check if they can self heal and past limit
                if (s.getState() == State.INCAP && s.getHealingProgress() >= Config.getInt("timers.survivor","self_heal_limit") && s.getSelfHeal() == 0) {
                    player.sendMessage("Cannot progress anymore due to self heal limit");
                    s.clearActionRunnable();
                    return;
                }

                s.incHealingProgress(10);


                // Self revive
                if (s.getState() == State.INCAP) {
                    player.setExp((float) s.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                    if (player.getExp() >= 1) { // Finished reviving
                        s.selfHeal -= 1;
                        player.setHealth(2);
                        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                        s.setHealingProgress(0);
                        s.setState(State.NORMAL);
                        player.sendMessage("You have revived yourself");
                        s.clearActionRunnable();
                    }
                    return;
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 15); // slower
    }

    public void startCloneHeal(final Survivor clone, final Survivor injured) {
        Console.log("Detected clone heal");
        s.setAction(Action.HEAL);
        clone.setAction(Action.GETHEAL); // Can't be injured because they are using bot
        clone.setHealer(s);

        injured.getPlayer().sendMessage("Your body is being healed");

        s.target = clone; // ?

        final double progressAtStart = clone.getHealingProgress(); // May need to adjust this later

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {

                clone.incHealingProgress(10, false); // Cancel particle effects
                Animations.random(clone.getLocation(),"animations.survivor","heal",1.5,3);

                // Calib
                if (clone.getHealingProgress() - progressAtStart > 40) { // Window for calibration
                    Random r = new Random();
                    if (r.nextInt(4) == 0) {
                        if (!CalibrationManager.hasCalibration(s)) CalibrationManager.give(s,Action.HEAL);
                    }
                }

                // Heal
                if (clone.getState() == State.NORMAL) {
                    player.setExp((float) clone.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));

                    // Don't change exp of player
                    //injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                    if (player.getExp() >= 1) { // Finished healing
                        if (CalibrationManager.hasCalibration(s)) {
                            CalibrationManager.get(s).finish();
                        }

                        injured.getPlayer().setHealth(injured.getPlayer().getHealth() + 2); // Increases health to be updated later
                        clone.setHealingProgress(0);
                        injured.getPlayer().sendMessage("You have been healed");
                        Console.log("Clone was healed!");
                        //injured.getPlayer().sendMessage("You have been healed");
                        clone.clearActionRunnable();
                        s.clearActionRunnable();
                    }
                    return;
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 10);
    }

    public void startHeal(final Survivor injured) {
        Console.log("Starting heal of "+injured.getPlayer().getDisplayName());
        s.setAction(Action.HEAL);
        injured.setAction(Action.GETHEAL);

        s.target = injured;

        final double progressAtStart = injured.getHealingProgress();

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                if (injured.getAction() != Action.GETHEAL) { // Player cleared it
                    if (CalibrationManager.hasCalibration(s)) {
                        CalibrationManager.get(s).finish();
                    }
                    s.clearActionRunnable();
                    return;
                }
                injured.incHealingProgress(10);

                // Calib
                if (injured.getHealingProgress() - progressAtStart > 40) { // Window for calibration
                    Random r = new Random();
                    if (r.nextInt(4) == 0) {
                        if (!CalibrationManager.hasCalibration(s)) CalibrationManager.give(s,Action.HEAL);
                    }
                }

                // Heal
                if (injured.getState() == State.NORMAL) {
                    player.setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                    injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                    if (player.getExp() >= 1) { // Finished healing
                        if (CalibrationManager.hasCalibration(s)) {
                            CalibrationManager.get(s).finish();
                        }

                        injured.getPlayer().setHealth(injured.getPlayer().getHealth() + 2);
                        injured.setHealingProgress(0);
                        injured.getPlayer().sendMessage("You have been healed");
                        injured.clearActionRunnable();
                        s.clearActionRunnable();
                    }
                    return;
                }
                // Revive
                if (injured.getState() == State.INCAP) {
                    player.setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                    injured.getPlayer().setExp((float) injured.getHealingProgress() / (float) Config.getInt("timers.survivor","revive"));
                    if (player.getExp() >= 1) { // Finished reviving
                        if (CalibrationManager.hasCalibration(s)) {
                            CalibrationManager.get(s).finish();
                        }

                        injured.getPlayer().setHealth(2);
                        injured.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                        injured.setHealingProgress(0);
                        injured.setState(State.NORMAL);
                        injured.getPlayer().sendMessage("You have been revived");
                        injured.clearActionRunnable();
                        s.clearActionRunnable();
                    }
                    return;
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 10);
    }
}
