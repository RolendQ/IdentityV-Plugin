package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Creating and deleting holograms
 */
public class Holograms {
    public static IdentityV plugin;

    public Holograms(IdentityV plugin) {
        Holograms.plugin = plugin;
    }

    public static void alert(Player hunter, final Location loc) {
        hunter.playSound(hunter.getLocation(), Sound.ANVIL_LAND,1,0);
        loc.getWorld().strikeLightningEffect(loc);
        box(loc);
        new BukkitRunnable() {
            public void run() {
                delete(loc);
            }
        }.runTaskLater(plugin, 40);
    }

    public static void create(Location loc, String name) {
        name = ChatColor.translateAlternateColorCodes('&',name);
        ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        am.setArms(false);
        am.setGravity(false);
        am.setVisible(false);
        am.setCustomName(name);
        am.setCustomNameVisible(true);
    }

    public static void delete(Location loc) {
        for (Entity en : loc.getWorld().getNearbyEntities(loc,3,3,3)) {
            if (en.getType() == EntityType.ARMOR_STAND) {
                en.remove();
            }
        }
    }

    public static void box(Location location) {
        for (double y = -1.5; y <= 1.5; y += 0.25) {
            create(location.add(0,y,0), "&c███████████████");
            location.subtract(0,y,0);
        }
    }
}
