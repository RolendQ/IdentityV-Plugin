package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Calibration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CalibrationManager {
    public static IdentityV plugin;

    public static HashMap<Player, Calibration> calibrations;

    public CalibrationManager(final IdentityV plugin) {
        this.plugin = plugin;
        calibrations = new HashMap<Player, Calibration>();
    }

    public static void give(Player p, int type) {
        Calibration c = new Calibration(plugin, p, type);
        p.sendTitle("Calibration",""+c.getGoal()); // sometimes players don't see it?
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Calibration: " + ChatColor.YELLOW + c.getGoal());
        calibrations.put(p,c);
    }

    public static boolean hasCalibration(Player p) {
        return calibrations.containsKey(p);
    }

//    public static void remove(Player p) {
//        calibrations.remove(p);
//    }

    public static void removeAfterDelay(final Player p) {
        // Can adjust delay
        new BukkitRunnable() {
            public void run() {
                calibrations.remove(p);
            }
        }.runTaskLater(plugin, 40);
    }

    public static Calibration get(Player p) {
        return calibrations.get(p);
    }
}
