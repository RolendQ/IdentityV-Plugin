package com.roland.identityv.gameobjects;

import com.roland.identityv.actions.animated.MissCalibration;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.utils.Console;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Calibration {
    public int progress; // exp level
    public int goal;
    public Survivor survivor;
    public Player player;
    public BukkitRunnable task;
    public int type;

    public boolean beingRemoved = false;

    public Calibration(Survivor survivor, final int type) {
        this.player = survivor.getPlayer();
        this.survivor = survivor;
        this.type = type;
        progress = 0;
        Random r = new Random();
        goal = (4 + r.nextInt(5)) * 10; // 40 to 80

        // Timer: increase calibration progress
        task = new BukkitRunnable() {
            public void run() {
                // If no longer normal state or no longer decoding
                player = getSurvivor().getPlayer();

                // Make sure not bot
                if (player != null && getSurvivor().getState() != State.NORMAL) {
                    finish();
                    return;
                }
                //if (player != null) Console.log(progress + " Checking :"+player.getDisplayName()+" action: "+getSurvivor().getAction());
                //else Console.log(progress + " Robot");

                if (player != null && getSurvivor().getAction() != type) {
                    finish();
                    Console.log(player.getDisplayName() + " missed calib because not decoding/healing anymore: "+type);
                    new MissCalibration(getSurvivor());
                    return;
                }
                progress += 4;
                if (progress > 100) {
                    finish();
                    Console.log("Missed calib because reached limit");
                    new MissCalibration(getSurvivor());
                } else {
                    if (player != null) player.setLevel(progress);
                }
            }
        };
        task.runTaskTimer(IdentityV.plugin, 0, 4); // 10 times per second
    }

    public void finish() {
        if (player != null) player.setLevel(0);
        task.cancel();
        CalibrationManager.removeAfterDelay(survivor);
    }

    public void hit() {
        // Must be player to hit
        finish();
        if (progress / 10 == goal / 10 || ((progress - 1) / 10 == goal / 10)) { // Increase window for high ping and less buggy feel
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 2);
            player.sendMessage("Success!");
        } else {
            new MissCalibration(survivor);
            Console.log("Hit at wrong time");
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getGoal() {
        return goal;
    }

    public BukkitRunnable getTask() {
        return task;
    }

    public int getType() {
        return type;
    }

    public Survivor getSurvivor() {
        return survivor;
    }

    public void setSurvivor(Survivor s) {
        CalibrationManager.replace(this.survivor, s);
        Console.log("Changed survivor/player");
        this.survivor = s;
        this.player = s.getPlayer();
    }

    public void setBeingRemoved(boolean beingRemoved) {
        this.beingRemoved = beingRemoved;
    }

    public boolean getBeingRemoved() {
        return beingRemoved;
    }
}
