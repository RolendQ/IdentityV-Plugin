package com.roland.identityv.actions.progress;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.utils.Adjustments;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Decode {
    // Non bot
    public Decode(Survivor s, final Cipher cipher) {
        this(s, cipher, cipher.getProgress());
    }

    // Bot
    public Decode(final Survivor s, final Cipher cipher, double progressAtStart) {
        s.setAction(Action.DECODE);
        Console.log("Start decode");
        //final double progressAtStart = cipher.getProgress();
        s.setCipherProgressAtStart(progressAtStart);
        if (!cipher.getSurvivorsDecoding().contains(s)) { // Register as a decoding survivor
            cipher.addSurvivor(s);
        }

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                // If 5 ciphers are done
                if (s.game.getCiphersDone() == 5) {
                    s.clearActionRunnable();
                    if (CalibrationManager.hasCalibration(s)) {
                        CalibrationManager.get(s).finish();
                    }
                }

                // Progress depends on number of decoders
                cipher.incProgress(Adjustments.getDecodeRate(cipher.getSurvivorsDecoding().size()));

                if (cipher.getProgress() - s.getCipherProgressAtStart() > 40) { // Window for calibration
                    Random r = new Random();
                    if (r.nextInt(6) == 0) {
                        if (!CalibrationManager.hasCalibration(s)) CalibrationManager.give(s, Action.DECODE);
                    }
                }
                Player player = s.getPlayer();
                player.setExp((float) cipher.getProgress() / (float) Config.getInt("timers.survivor","decode"));
                if (player.getExp() >= 1) {
                    cipher.pop();
                    player.sendMessage("You have finished this cipher");
                    if (CalibrationManager.hasCalibration(s)) {
                        CalibrationManager.get(s).finish();
                    }
                    s.clearActionRunnable();
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 10);
    }
}
