package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Dungeon;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class DungeonManager {
    public static IdentityV plugin;
    public static Dungeon activeDungeon;

    public DungeonManager(final IdentityV plugin) {
        this.plugin = plugin;
        activeDungeon = null;
        dungeons = new ArrayList<Dungeon>();
    }

    public static ArrayList<Dungeon> dungeons;

    public static boolean add(Location loc) {
        // Make sure it's not overlapping
        for (Dungeon d : dungeons) {
            if (d.getBlockLocs().contains(loc)) {
                Console.log("Found dup dungeon");
                return false;
            }
        }
        Console.log("Added new dungeon");
        dungeons.add(new Dungeon(plugin, loc, plugin.getGame()));
        return true;
    }

    public static void clear() {
        activeDungeon = null;
        dungeons.clear();
    }

    public static void spawnRandom() {
        Random r = new Random();
        Console.log("Dungeons size: "+dungeons.size());
        int index = r.nextInt(dungeons.size());
        activeDungeon = dungeons.get(index); // Set active dungeon
        activeDungeon.spawn();
    }

    public static Dungeon getActiveDungeon() {
        return activeDungeon;
    }
}
