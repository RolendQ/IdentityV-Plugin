package com.roland.identityv.handlers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Console;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * Handles sitting and unsitting players
 */
public class SitHandler {
    public static HashMap<String,Integer> chickens = new HashMap<String,Integer>(); // Key is UUID of player, value is chicken id
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

    /**
     * Fake sitting using packets because real sitting creates bugs with "Invalid move packet" kicking
     * @param player
     * @param loc
     */
    public static void fakeSit(final Player player, Location loc) {
        if (player.getVehicle() != null) {
            forciblyEjected = true;
            player.getVehicle().eject();
            forciblyEjected = false;
        }
        final EntityChicken en = new EntityChicken(((CraftWorld) player.getWorld()).getHandle());
        en.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0); //Set chicken's location
        en.setInvisible(true);
        // store chicken
        chickens.put(player.getUniqueId().toString(),en.getId());
        for (Player p : player.getServer().getOnlinePlayers()) {
            Console.log("Sending chicken packet to "+p.getDisplayName());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(en));
        }
        new BukkitRunnable() {
            public void run() {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    Console.log("Sending sit packet to "+p.getDisplayName());
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), en));
                }
            }
        }.runTaskLater(IdentityV.plugin, 5);
        Console.log("Sent packet!");
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

    public static void fakeUnsit(Player player) {
        if (!chickens.containsKey(player.getUniqueId().toString())) return;
        Integer chickenID = chickens.get(player.getUniqueId().toString());
        for (Player p : player.getServer().getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(chickenID));
        }
        Console.log("Sent packet to destroy chicken");
    }

    public static boolean wasForcibyEjected() {
        return forciblyEjected;
    }
}
