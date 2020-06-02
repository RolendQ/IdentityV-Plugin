package com.roland.identityv.utils;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {
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
}
