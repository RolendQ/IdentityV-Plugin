package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all cipher objects
 */
public class CipherManager {
    public static IdentityV plugin;

    public CipherManager(final IdentityV plugin) {
        this.plugin = plugin;
        ciphers = new HashSet<Cipher>();

//        new BukkitRunnable() {
//
//            public void run() {
//                ConsoleCommandSender console = plugin.getServer().getConsoleSender();
//
//                for (Cipher c : ciphers) {
//                    if (c.isDone()) return;
//
//                    for (Entity entity : c.getLocation().getWorld().getNearbyEntities(c.getLocation(),2,2,2)) {
//                        if (entity.getType() == EntityType.PLAYER) {
//                            Player p = (Player) entity;
//                            // Must be survivor and NORMAL state
//                            if (SurvivorManager.isSurvivor(p) && SurvivorManager.getSurvivor(p).getState() == State.NORMAL) {
//                                if (p.isSneaking()) {
//                                    c.decodeBit(1); // 1 for now
//                                    if (c.getProgress() % 10 == 0) c.notify(p);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }.runTaskTimer(plugin, 0, 10);
    }

    public static HashSet<Cipher> ciphers;

    public static void add(Location loc) {
        ciphers.add(new Cipher(plugin, loc, plugin.getGame()));
    }

    public static void remove(Location loc) {

    }

    public static Cipher getCipher(Location loc) {
        for (Cipher c : ciphers) {
            Console.log(c.getLocation().toString() + ": " + c.getProgress());
            if (c.getLocation().getX() == loc.getX() &&
                    c.getLocation().getY() == loc.getY() &&
                    c.getLocation().getZ() == loc.getZ()) {
                return c;
            }
        }
        //return null;
        Console.log("Created new cipher");
        Cipher newCipher = new Cipher(plugin,loc);
        ciphers.add(newCipher);
        return newCipher;
    }

}
