package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Manages all rocket chair objects
 */
public class RocketChairManager {
    public static IdentityV plugin;

    public static HashSet<RocketChair> chairs;

    public RocketChairManager(final IdentityV plugin) {
        chairs = new HashSet<RocketChair>();
    }

    public static RocketChair getChair(Location loc) {
        for (RocketChair chair : chairs) {
            if (chair.getLocation().equals(loc)) { // TODO might need to change
                return chair;
            }
        }
        // If not found, setup new chair
        Console.log("Created new rocket chair");
        RocketChair newChair = new RocketChair(loc,plugin);
        chairs.add(newChair);
        return newChair;
    }

    public static RocketChair getChair(Player p) {
        for (RocketChair chair : chairs) {
            if (chair.getSurvivor() == p) {
                return chair;
            }
        }
        return null;
    }
}
