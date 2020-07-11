package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Calibration;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Console;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CalibrationManager {
    public static IdentityV plugin;

    public static HashMap<Survivor, Calibration> calibrations;

    public CalibrationManager(final IdentityV plugin) {
        this.plugin = plugin;
        calibrations = new HashMap<Survivor, Calibration>();
    }

    public static void give(Survivor survivor, int type) {
        Player p = survivor.getPlayer();
        Calibration c = new Calibration(survivor, type);
        calibrations.put(survivor, c);
        // Only for survivors
        if (survivor.getPlayer() != null) {
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 2);
            p.sendTitle("Calibration", "" + c.getGoal()); // sometimes players don't see it? TODO
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Calibration: " + ChatColor.YELLOW + c.getGoal());
        } else {
            Console.log("Gave robot a calibration");
        }
    }

    public static boolean hasCalibration(Survivor survivor) {
        return calibrations.containsKey(survivor);
    }

//    public static void remove(Player p) {
//        calibrations.remove(p);
//    }

    public static void removeAfterDelay(final Survivor survivor) {
        // Can adjust delay
        if (calibrations.containsKey(survivor)) {
            calibrations.get(survivor).setBeingRemoved(true);
            new BukkitRunnable() {
                public void run() {
                    calibrations.remove(survivor);
                }
            }.runTaskLater(plugin, 40);
        }
    }

    public static void replace(Survivor s1, Survivor s2) {
        Calibration c = calibrations.get(s1);
        calibrations.remove(s1);
        calibrations.put(s2,c);
        // Send the messages again
        if (s2.getPlayer() != null) {
            s2.getPlayer().sendTitle("Calibration", "" + c.getGoal()); // sometimes players don't see it?
            s2.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Calibration: " + ChatColor.YELLOW + c.getGoal());
        }
    }

    public static Calibration get(Survivor survivor) {
        return calibrations.get(survivor);
    }
}
