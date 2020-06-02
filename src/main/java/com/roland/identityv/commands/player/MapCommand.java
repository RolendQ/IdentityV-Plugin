package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.DungeonManager;
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
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import java.util.List;

public class MapCommand extends PlayerCommand {
    public MapCommand(IdentityV plugin) {
        super(plugin);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("map")) {
            if (args.length == 0) return true;

            if (args[0].equalsIgnoreCase("set")) {
                Console.log("Setting map");

                Config.clearMapData();

                if (args.length == 4) { // 1 set of coords and standing on block
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);

                    Location loc = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                    int minX = Math.min(x,(int) loc.getX());
                    int minY = Math.min(y,(int) loc.getY());
                    int minZ = Math.min(z,(int) loc.getZ());
                    int maxX = Math.max(x,(int) loc.getX());
                    int maxY = Math.max(y,(int) loc.getY());
                    int maxZ = Math.max(z,(int) loc.getZ());

                    int bannersCount = 0;
                    int slabsCount = 0;
                    int barsCount = 0;
                    int dungeonsCount = 0;

                    Config.addMapData("coords",minX + "," + minY + "," + minZ);
                    Config.addMapData("coords",maxX + "," + maxY + "," + maxZ);

                    for (int bX = minX; bX < maxX; bX++) {
                        for (int bY = minY; bY < maxY; bY++) {
                            for (int bZ = minZ; bZ < maxZ; bZ++) {
                                Block b = p.getWorld().getBlockAt(bX,bY,bZ);
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
                                    // This stores ALL dungeon blocks
                                    Console.log("Found daylight_detector");
                                    String data = bX + "," + bY + "," + bZ;
                                    Config.addMapData("daylight_detector",data);
                                    dungeonsCount++;
                                }
                            }
                        }
                    }
                    p.sendMessage(ChatColor.GREEN + "Successfully saved map! Banners: "+bannersCount+" Slabs: "+slabsCount+" Bars: "+barsCount+ "Dungeons: "+dungeonsCount);
                }


                plugin.saveMapConfig();

                return true;
            }

            if (args[0].equalsIgnoreCase("refresh")) {
                // Setup dungeons in manager
                DungeonManager.clear();
                for (String data : Config.getMapData("daylight_detector")) {
                    String[] dataSplit = data.split(",");
                    Location loc = new Location(p.getWorld(),Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
                    loc.getBlock().setType(Material.DAYLIGHT_DETECTOR_INVERTED); // Set it so the nonadjacent detector can work
                    DungeonManager.add(loc);
                }

                // Hide the dungeons
                for (String data : Config.getMapData("daylight_detector")) {
                    String[] dataSplit = data.split(",");
                    Location loc = new Location(p.getWorld(),Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
                    loc.getBlock().setType(Material.AIR);
                }

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
                            Block b = p.getWorld().getBlockAt(bX, bY, bZ);
                            // Remove green clay and black glass
                            if (b.getType() == Material.STAINED_GLASS && b.getData() == DyeColor.BLACK.getData()) {
                                b.setType(Material.AIR);
                                continue;
                            }
                            if (b.getType() == Material.STAINED_CLAY && b.getData() == DyeColor.GREEN.getData()) {
                                b.setType(Material.AIR);
                                continue;
                            }
                        }
                    }
                }

                for (String data : Config.getMapData("banners")) {
                    String[] dataSplit = data.split(",");
                    Block b = p.getWorld().getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
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
                    Block b = p.getWorld().getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
                    if (b.getType() != Material.AIR) continue;
                    b.setType(Material.STEP);
                    b.setData((byte) 6);
                }
                for (String data : Config.getMapData("bars")) {
                    String[] dataSplit = data.split(",");
                    Block b = p.getWorld().getBlockAt(Integer.parseInt(dataSplit[0]),Integer.parseInt(dataSplit[1]),Integer.parseInt(dataSplit[2]));
                    if (b.getType() != Material.AIR) continue;
                    b.setType(Material.IRON_FENCE);
                }
                p.sendMessage(ChatColor.GREEN + "Successfully refreshed map!");
                return true;
            }
        }
        return true;
    }
}