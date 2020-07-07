package com.roland.identityv.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUtil {

    // Positive X: EAST
    // Positive Z: SOUTH
    public static BlockFace[][] faces = {
            {BlockFace.EAST,BlockFace.SOUTH,BlockFace.SOUTH_EAST}, // up right
            {BlockFace.WEST,BlockFace.SOUTH,BlockFace.SOUTH_WEST}, // up left
            {BlockFace.EAST,BlockFace.NORTH,BlockFace.NORTH_EAST}, // down right
            {BlockFace.WEST,BlockFace.NORTH,BlockFace.NORTH_WEST}, // down left


//            {BlockFace.EAST,BlockFace.NORTH,BlockFace.NORTH_EAST}, // down right
//            {BlockFace.WEST,BlockFace.NORTH,BlockFace.NORTH_WEST}, // down left
//            {BlockFace.EAST,BlockFace.SOUTH,BlockFace.SOUTH_EAST}, // up right
//            {BlockFace.WEST,BlockFace.SOUTH,BlockFace.SOUTH_WEST}, // up left
    };

    public static boolean isTouchingWall(Player p) {
        double x = p.getLocation().getX();
        double z = p.getLocation().getZ();
        Block b = p.getLocation().getBlock();
        double decX = x - ((int) x);
        double decZ = z - ((int) z);
        if (decX > 0) {
            if (decX > 0.69 && b.getRelative(BlockFace.EAST).getType().isSolid()) return true;
            if (decX < 0.31 && b.getRelative(BlockFace.WEST).getType().isSolid()) return true;
        } else {
            if (decX > -0.31 && b.getRelative(BlockFace.EAST).getType().isSolid()) return true;
            if (decX < -0.69 && b.getRelative(BlockFace.WEST).getType().isSolid()) return true;
        }
        if (decZ > 0) {
            if (decZ > 0.69 && b.getRelative(BlockFace.SOUTH).getType().isSolid()) return true;
            if (decZ < 0.31 && b.getRelative(BlockFace.NORTH).getType().isSolid()) return true;
        } else {
            if (decZ > -0.31 && b.getRelative(BlockFace.SOUTH).getType().isSolid()) return true;
            if (decZ < -0.69 && b.getRelative(BlockFace.NORTH).getType().isSolid()) return true;
        }
        return false;
    }

    public static boolean hasInvisEffect(Player p) {
        for (PotionEffect pot : p.getActivePotionEffects()) {
            if (pot.getType() == PotionEffectType.INVISIBILITY) return true;
        }
        return false;
    }

    public static BlockFace[] getTwoByTwo(Location loc) {
        double x = loc.getX() - ((int) loc.getX());
        double z = loc.getZ() - ((int) loc.getZ());
        if (x > 0) {
            if (x < 0.5) {
                if (z > 0) {
                    if (z > 0.5) {
                        return PlayerUtil.faces[0];
                    } else {
                        return PlayerUtil.faces[2];
                    }
                } else {
                    if (z < -0.5) {
                        return PlayerUtil.faces[0];
                    } else {
                        return PlayerUtil.faces[2];
                    }
                }
            } else {
                if (z > 0) {
                    if (z > 0.5) {
                        return PlayerUtil.faces[1];
                    } else {
                        return PlayerUtil.faces[3];
                    }
                } else {
                    if (z < -0.5) {
                        return PlayerUtil.faces[1];
                    } else {
                        return PlayerUtil.faces[3];
                    }
                }
            }

        } else {
            if (x > -0.5) {
                if (z > 0) {
                    if (z > 0.5) {
                        return PlayerUtil.faces[0];
                    } else {
                        return PlayerUtil.faces[2];
                    }
                } else {
                    if (z < -0.5) {
                        return PlayerUtil.faces[0];
                    } else {
                        return PlayerUtil.faces[2];
                    }
                }
            } else {
                if (z > 0) {
                    if (z > 0.5) {
                        return PlayerUtil.faces[1];
                    } else {
                        return PlayerUtil.faces[3];
                    }
                } else {
                    if (z < -0.5) {
                        return PlayerUtil.faces[1];
                    } else {
                        return PlayerUtil.faces[3];
                    }
                }
            }
        }
    }
}
