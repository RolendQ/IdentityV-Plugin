package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Adjustments;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapManager {
    public static World world;
    public static IdentityV plugin;

    public MapManager(IdentityV plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    public static void setup(int x1, int y1, int z1, int x2, int y2, int z2) {
        Console.log("Setting map");

        Config.clearMapData();
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        int bannersCount = 0;
        int slabsCount = 0;
        int barsCount = 0;
        int dungeonsCount = 0;
        int chestsCount = 0;

        Config.addMapData("coords",minX + "," + minY + "," + minZ);
        Config.addMapData("coords",maxX + "," + maxY + "," + maxZ);

        for (int bX = minX; bX < maxX; bX++) {
            for (int bY = minY; bY < maxY; bY++) {
                for (int bZ = minZ; bZ < maxZ; bZ++) {
                    Block b = world.getBlockAt(bX,bY,bZ);
                    if (b.getType() == Material.WALL_BANNER) {
                        Banner banner = (Banner) b.getState();
                        org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                        String face = bannerData.getFacing().toString();
                        String data = bX + "," + bY + "," + bZ + "," + face;
                        Config.addMapData("banners",data);
                        bannersCount++;
                    } else if (b.getType() == Material.STEP && b.getData() == 6) {
                        String data = bX + "," + bY + "," + bZ;
                        Config.addMapData("slabs",data);
                        slabsCount++;
                    } else if (b.getType() == Material.IRON_FENCE) {
                        String data = bX + "," + bY + "," + bZ;
                        Config.addMapData("bars",data);
                        barsCount++;
                    } else if (b.getType() == Material.GOLD_BLOCK) {
                        Console.log("Found gold");
                        String data = bX + "," + bY + "," + bZ;
                        Config.addMapData("gold",data);
                    } else if (b.getType() == Material.EMERALD_BLOCK) {
                        Console.log("Found emerald");
                        String data = bX + "," + bY + "," + bZ;
                        Config.addMapData("emerald",data);
                    } else if (b.getType() == Material.DAYLIGHT_DETECTOR_INVERTED) {
                        // This stores ALL dungeon blocks (Must be inverted)
                        Console.log("Found daylight_detector");
                        String data = bX + "," + bY + "," + bZ;
                        Config.addMapData("daylight_detector",data);
                        dungeonsCount++;
                    } else if (b.getType() == Material.CHEST) {
                        chestsCount++;
                    }
                }
            }
            // TODO send to player?
            Console.log("Successfully saved map! Banners: "+bannersCount+" Slabs: "+slabsCount+" Bars: "+barsCount+ " Dungeons: "+dungeonsCount + " Chests: "+chestsCount);
        }

        plugin.saveMapConfig();
    }

    public static void refresh() {
        // Setup dungeons in manager
        DungeonManager.clear();
        for (String data : Config.getMapData("daylight_detector")) {
            String[] dataSplit = data.split(",");
            Location loc = new Location(world,Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
            loc.getBlock().setType(Material.DAYLIGHT_DETECTOR_INVERTED); // Set it so the nonadjacent detector can work
            //DungeonManager.add(loc);
        }

        // Hide the dungeons
        for (String data : Config.getMapData("daylight_detector")) {
            String[] dataSplit = data.split(",");
            Location loc = new Location(world,Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
            DungeonManager.add(loc);
            loc.getBlock().setType(Material.AIR);
        }

        Console.log("Number of dungeons loaded: "+DungeonManager.dungeons.size());

        List<String> coords = Config.getMapData("coords");
        String[] minSplit = coords.get(0).split(",");
        String[] maxSplit = coords.get(1).split(",");
        int minX = Integer.parseInt(minSplit[0]);
        int minY = Integer.parseInt(minSplit[1]);
        int minZ = Integer.parseInt(minSplit[2]);
        int maxX = Integer.parseInt(maxSplit[0]);
        int maxY = Integer.parseInt(maxSplit[1]);
        int maxZ = Integer.parseInt(maxSplit[2]);
        for (int bX = minX; bX < maxX; bX++) {
            for (int bY = minY; bY < maxY; bY++) {
                for (int bZ = minZ; bZ < maxZ; bZ++) {
                    Block b = world.getBlockAt(bX, bY, bZ);
                    // Detect any gates
                    if (b.getType() == Material.TRIPWIRE_HOOK) {
                        GateManager.add(b.getLocation());
                    }

                    // Remove green clay and black glass and frames and acacia fences
                    if (b.getType() == Material.STAINED_GLASS && b.getData() == DyeColor.BLACK.getData()) {
                        b.setType(Material.AIR);
                        continue;
                    }
                    if (b.getType() == Material.STAINED_CLAY && b.getData() == DyeColor.GREEN.getData()) {
                        b.setType(Material.AIR);
                        continue;
                    }
                    if (b.getType() == Material.ACACIA_FENCE) {
                        b.setType(Material.AIR);
                        continue;
                    }
                }
            }
        }

        // Kill all item frames
        for (Entity en : world.getNearbyEntities(new Location(world,(maxX+minX) / 2, (maxY+minY) / 2 ,(maxZ+minZ) / 2),200, 200, 200)) {
            if (en.getType() == EntityType.ITEM_FRAME) {
                en.remove();
            }
        }

        for (String data : Config.getMapData("banners")) {
            String[] dataSplit = data.split(",");
            Block b = world.getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
            if (b.getType() != Material.AIR) continue;
            b.setType(Material.WALL_BANNER);
            Banner banner = (Banner) b.getState();
            org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
            //Console.log("Setting block face: "+BlockFace.valueOf(dataSplit[3]));
            bannerData.setFacingDirection(BlockFace.valueOf(dataSplit[3]));
            banner.setBaseColor(DyeColor.GREEN);
            banner.setData(bannerData);
            banner.update(true);
        }
        for (String data : Config.getMapData("slabs")) {
            String[] dataSplit = data.split(",");
            Block b = world.getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
            if (b.getType() != Material.AIR) continue;
            b.setType(Material.STEP);
            b.setData((byte) 6);
        }
        for (String data : Config.getMapData("bars")) {
            String[] dataSplit = data.split(",");
            Block b = world.getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
            if (b.getType() != Material.AIR) continue;
            b.setType(Material.IRON_FENCE);
        }
        //p.sendMessage(ChatColor.GREEN + "Successfully refreshed map!");
        Console.log("Successfully refreshed map!");
    }

    public static void spawn() {
        if (Config.getMapData("emerald") == null) {
            IdentityV.plugin.getServer().broadcastMessage("Error: No spawns loaded");
            return;
        }

        // TODO for now, random teleport to unoccupied spawn block
        // Gather list of survivors
        List<Player> survivors = new ArrayList<Player>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (HunterManager.isHunter(player)) {
                // Hunter
                List<String> hunterSpawns = Config.getMapData("emerald");
                String[] split = hunterSpawns.get(0).split(",");
                player.teleport(Adjustments.spawnBlock(player.getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
            } else if (SurvivorManager.isSurvivor(player)) {
                // Survivor
                survivors.add(player);
            }
        }

        // Shuffle list of survivors to randomize survivor spawns
        Collections.shuffle(survivors);
        List<String> survivorSpawns = Config.getMapData("gold");
        for (int i = 0; i < survivors.size(); i++) {
            String[] split = survivorSpawns.get(i).split(",");
            survivors.get(i).teleport(Adjustments.spawnBlock(survivors.get(i).getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
        }

        plugin.getServer().broadcastMessage("Teleported everyone to spawns");
    }
}
