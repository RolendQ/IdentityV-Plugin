package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Chest;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;

import java.util.HashSet;

public class ChestManager {
    public static IdentityV plugin;

    public ChestManager(final IdentityV plugin) {
        this.plugin = plugin;
        chests = new HashSet<Chest>();
    }

    public static HashSet<Chest> chests;

    public static void add(Location loc) {
        chests.add(new Chest(plugin, loc));
    }

    public static void remove(Location loc) {

    }

    public static Chest getChest(Location loc) {
        for (Chest c : chests) {
            if (c.getLocation().getX() == loc.getX() &&
                    c.getLocation().getY() == loc.getY() &&
                    c.getLocation().getZ() == loc.getZ()) {
                return c;
            }
        }
        //return null;
        Console.log("Created new chest");
        Chest newChest = new Chest(plugin,loc);
        chests.add(newChest);
        return newChest;
    }
}
