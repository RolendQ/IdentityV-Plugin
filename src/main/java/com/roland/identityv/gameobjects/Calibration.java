package com.roland.identityv.gameobjects;

import com.roland.identityv.actions.MissCalibration;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Calibration {
    public int progress; // exp level
    public int goal;
    public Player player;
    public BukkitRunnable task;
    public int type;

    public IdentityV plugin;

    public Calibration(final IdentityV plugin, final Player player, final int type) {
        this.player = player;
        this.plugin = plugin;
        this.type = type;
        progress = 0;
        Random r = new Random();
        goal = (4 + r.nextInt(5)) * 10; // 40 to 80
        task = new BukkitRunnable() {
            public void run() {
                // If no longer normal state or no longer decoding
                if (SurvivorManager.isSurvivor(player)) {
                    if (SurvivorManager.getSurvivor(player).getState() != State.NORMAL) {
                        finish();
                        return;
                    }
                    if (SurvivorManager.getSurvivor(player).getAction() != type) {
                        finish();
                        new MissCalibration(plugin, player);
                        return;
                    }
                }

                progress += 2;
                if (progress > 100) {
                    finish();
                    new MissCalibration(plugin, player);
                } else {
                    player.setLevel(progress);
                }
            }
        };
        task.runTaskTimer(plugin, 0, 2); // 10 times per second
    }

    public void finish() {
        player.setLevel(0);
        task.cancel();
        CalibrationManager.removeAfterDelay(player);
    }

    public void hit() {
        //player.sendMessage("Hit calibration at: "+progress);
        finish();
        if (progress / 10 == goal / 10) {
            player.sendMessage("Success!");
        } else {
            new MissCalibration(plugin, player);
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
}
