package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.listeners.playerlisteners.PlayerSneakListener;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class Dungeon {
    public Location loc;
    public ArrayList<Location> blockLocs;
    public Game game;

    // Create this objects in game setup TODO make sure they don't overlap
    public Dungeon(Location loc, Game game) {
        this.game = game;
        this.loc = loc;
        blockLocs = new ArrayList<Location>();

        // Add all dungeon blocks
        blockLocs.add(loc);
        for (BlockFace face : PlayerSneakListener.faces) {
            Block b = loc.getBlock().getRelative(face);
            if (b.getType() == Material.DAYLIGHT_DETECTOR_INVERTED) {
                blockLocs.add(b.getLocation());
            }
        }
    }

    public void spawn() {
        Console.log("blockLocs: "+blockLocs.size());
        for (Location blockLoc : blockLocs) {
            loc.getWorld().getBlockAt(blockLoc).setType(Material.DAYLIGHT_DETECTOR_INVERTED);
        }
    }

    public void open() {
        for (Location blockLoc : blockLocs) {
            loc.getWorld().getBlockAt(blockLoc).setType(Material.DAYLIGHT_DETECTOR);
        }
    }

    public ArrayList<Location> getBlockLocs() {
        return blockLocs;
    }


}
