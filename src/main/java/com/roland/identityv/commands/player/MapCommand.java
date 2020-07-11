package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.utils.Adjustments;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapCommand extends PlayerCommand {
    public MapCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * Map set (1 set of coords and standing on block), Map refresh, and Map spawn
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("map")) {
            if (args.length == 0) return true;

            if (args[0].equalsIgnoreCase("set")) {
                Location loc = p.getLocation();
                MapManager.setup(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }

            if (args[0].equalsIgnoreCase("refresh")) {
                MapManager.refresh();
            }

            if (args[0].equalsIgnoreCase("spawn")) {
                MapManager.spawn();
            }
        }
        return true;
    }
}