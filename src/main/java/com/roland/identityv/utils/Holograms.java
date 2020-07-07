package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Creating and deleting holograms
 */
public class Holograms {
    public static IdentityV plugin;
    public static HashMap<Integer, String> entityIds;

    public Holograms(IdentityV plugin) {
        Holograms.plugin = plugin;
        entityIds = new HashMap<Integer, String>();
    }

    public static void alert(final Player player, final Location loc, int duration) {
        player.playSound(player.getLocation(), Sound.ANVIL_LAND,1,0);
        //loc.getWorld().strikeLightningEffect(loc);

        EntityLightning lightning = new EntityLightning(((CraftWorld)player.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        //Console.log("lightning: "+lightning.getChunkCoordinates().toString());
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(lightning);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        box(player,loc);
        new BukkitRunnable() {
            public void run() {
                delete(player);
            }
        }.runTaskLater(plugin, duration);
    }

    public static void create(Player client, Location loc, String name) {
        name = ChatColor.translateAlternateColorCodes('&',name);
        //ArmorStand am = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        EntityArmorStand am = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
        am.setLocation(loc.getX(),loc.getY(),loc.getZ(), 0, 0);
        am.setArms(false);
        am.setGravity(false);
        //am.setVisible(false);
        am.setInvisible(true);
        am.setCustomName(name);
        am.setCustomNameVisible(true);

        entityIds.put(am.getId(), client.getUniqueId().toString());

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(am);
        ((CraftPlayer) client).getHandle().playerConnection.sendPacket(packet);
    }

    public static void delete(Location loc) {
        for (Entity en : loc.getWorld().getNearbyEntities(loc,3,3,3)) {
            if (en.getType() == EntityType.ARMOR_STAND) {
                en.remove();
            }
        }
    }

    public static void delete(Player client) {
//        for (Integer id : entityIds.keySet()) {
//            if (entityIds.get(id).equals(client.getUniqueId().toString())) {
//                PacketPlayOutEntityDestroy pa = new PacketPlayOutEntityDestroy(id);
//                ((CraftPlayer) client).getHandle().playerConnection.sendPacket(pa);
//                entityIds.remove(id);
//            }
//        }

        Iterator<Integer> it = entityIds.keySet().iterator();
        while(it.hasNext()){
            Integer item = it.next();
            if(entityIds.get(item).equals(client.getUniqueId().toString())){
                PacketPlayOutEntityDestroy pa = new PacketPlayOutEntityDestroy(item);
                ((CraftPlayer) client).getHandle().playerConnection.sendPacket(pa);

                it.remove();

            }
        }
    }

    public static void box(Player client, Location location) {
        for (double y = -1.5; y <= 1.5; y += 0.25) {
            create(client, location.add(0,y,0), "&c███████████████");
            location.subtract(0,y,0);
        }
    }
}
