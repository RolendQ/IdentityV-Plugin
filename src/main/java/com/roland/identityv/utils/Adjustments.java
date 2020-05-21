package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import org.bukkit.entity.Player;

import java.util.Set;

public class Adjustments {
    public static IdentityV plugin;

    /**
     * Converts distance into a heart rate for survivor
     * @param distance
     * @return
     */
    public static int getHeartRate(double distance) {
        if (distance > 20) return 9999999;
        if (distance > 15) return 30;
        if (distance > 10) return 20;
        if (distance > 7) return 15;
        return 10;
    }

    public Adjustments(IdentityV plugin) {
        this.plugin = plugin;
    }
}
