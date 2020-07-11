package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Chest {
    public Location loc;
    public Location particlesLoc;
    public double progress;
    //public boolean isEmpty;
    public Survivor opener;

    public Chest(Location loc) {
        this.loc = loc;
        this.particlesLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.1, loc.getZ());
        this.opener = null;
    }

    public Location getLocation() {
        return loc;
    }

    public double getProgress() {
        return progress;
    }

    public void incProgress(int bit) {
        Animations.random(particlesLoc,"animations.survivor","open_chest",0.5,8);
        progress += bit;
    }

    public boolean isEmpty() {
        return progress >= Config.getInt("timers.survivor","open_chest");
    }

    public void open() {
        Animations.random(particlesLoc,"animations.survivor","finish_chest",1, 5);

        // Set item frames
        BlockFace[] faces = {BlockFace.NORTH,BlockFace.SOUTH,BlockFace.EAST, BlockFace.WEST};
        Block b = loc.getBlock();
        for (BlockFace face : faces) {
            if (b.getRelative(face).getType() == Material.AIR) {
                //b.getRelative(face).setType(Material.ITEM_FRAME);
                try {
                    ItemFrame i = loc.getWorld().spawn(b.getRelative(face).getLocation(), ItemFrame.class);
                    i.setFacingDirection(face);
                } catch (Exception e) {
                    continue;
                }
            }
        }

        // Spawn item
        Random r = new Random();
        int n = r.nextInt(100); // 0 - 99
        final Entity item = loc.getWorld().dropItem(particlesLoc.clone().add(0.5,0,0.5),generateItem(n));
        item.setVelocity(new Vector(0,0,0));
        new BukkitRunnable() {
            public void run() {
                item.teleport(particlesLoc.clone().add(0.5,0,0.5));
            }
        }.runTaskLater(IdentityV.plugin, 1);
    }

    public static ItemStack generateItem(int n) {
        if (n < Config.getInt("attributes.chest","flaregun_chance")) return ItemUtil.create(Material.FIREWORK,1);
        if (n < Config.getInt("attributes.chest","controller_chance")) return ItemUtil.create(Material.IRON_PICKAXE,1);
        if (n < Config.getInt("attributes.chest","football_chance")) return ItemUtil.create(Material.LEATHER_HELMET,1);
        if (n < Config.getInt("attributes.chest","elbowpad_chance")) return ItemUtil.create(Material.IRON_CHESTPLATE,2);
        if (n < Config.getInt("attributes.chest","perfume_chance")) return ItemUtil.create(Material.GOLD_CHESTPLATE,2);
        if (n < Config.getInt("attributes.chest","wand_chance")) return ItemUtil.create(Material.BLAZE_ROD,1);
        if (n < Config.getInt("attributes.chest","syringe_chance")) return ItemUtil.create(Material.SHEARS,1);
        return new ItemStack(Material.BEDROCK);
    }

    public void animateOpenAndClose() {
        if (opener != null) Animations.falling_rings(opener.getPlayer().getLocation(),"animations.survivor","open_and_close_chest",Config.getInt("timers.survivor","open_and_close_chest_duration"));
    }

    public Survivor getOpener() {
        return opener;
    }

    public void setOpener(Survivor s) {
        this.opener = s;
    }
}
