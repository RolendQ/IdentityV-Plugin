package com.roland.identityv.gameobjects;

import com.roland.identityv.actions.MissCalibration;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
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

    public IdentityV plugin;

    public Calibration(final IdentityV plugin, Survivor survivor, final int type) {
        this.player = survivor.getPlayer();
        this.survivor = survivor;
        this.plugin = plugin;
        this.type = type;
        progress = 0;
        Random r = new Random();
        goal = (4 + r.nextInt(5)) * 10; // 40 to 80
        task = new BukkitRunnable() {
            public void run() {
                // If no longer normal state or no longer decoding
                player = getSurvivor().getPlayer();

                // Make sure not bot
                if (player != null && getSurvivor().getState() != State.NORMAL) {
                    finish();
                    return;
                }
                if (player != null) Console.log(progress + " Checking :"+player.getDisplayName()+" action: "+getSurvivor().getAction());
                else Console.log(progress + " Robot");

                if (player != null && getSurvivor().getAction() != type) {
                    finish();
                    Console.log(player.getDisplayName() + " missed calib because not decoding/healing anymore: "+type);
                    new MissCalibration(plugin, getSurvivor());
                    return;
                }
                progress += 4;
                if (progress > 100) {
                    finish();
                    Console.log("Missed calib because reached limit");
                    new MissCalibration(plugin, getSurvivor());
                } else {
                    if (player != null) player.setLevel(progress);
                }
            }
        };
        task.runTaskTimer(plugin, 0, 4); // 10 times per second
    }

    public void finish() {
        if (player != null) player.setLevel(0);
        task.cancel();
        CalibrationManager.removeAfterDelay(survivor);
    }

    public void hit() {
        //player.sendMessage("Hit calibration at: "+progress);
        // Must be player to hit
        finish();
        if (progress / 10 == goal / 10) {
            player.sendMessage("Success!");
        } else {
            new MissCalibration(plugin, survivor);
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
