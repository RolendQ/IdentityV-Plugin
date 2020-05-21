package com.roland.identityv.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Handles sitting and unsitting players
 */
public class SitHandler {
    public static boolean forciblyEjected = false;

    public static void sit(Player player, Location loc) {
        if (player.getVehicle() != null) {
            forciblyEjected = true;
            player.getVehicle().eject();
            forciblyEjected = false;
        }
        Arrow arrow = player.getWorld().spawnArrow(loc, new Vector(0,0,0), 0, 0);
        arrow.setPassenger(player);
    }

    public static void unsit(Player player) {
        if (player.getVehicle() != null) {
            if (player.getVehicle().getType() == EntityType.PLAYER) {
                forciblyEjected = true;
                player.getVehicle().eject();
                forciblyEjected = false;
            } else {
                player.getVehicle().remove();
            }
        }
    }

    public static boolean wasForcibyEjected() {
        return forciblyEjected;
    }
}
