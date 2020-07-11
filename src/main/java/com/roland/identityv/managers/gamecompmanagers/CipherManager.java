package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.gameobjects.Survivor;
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
    }

    public static HashSet<Cipher> ciphers;

    public static void add(Location loc) {
        ciphers.add(new Cipher(loc, plugin.getGame()));
    }

    public static void remove(Location loc) {

    }

    public static void addBlackGlass() {
        for (Cipher c : ciphers) {
            c.addBlackGlass();
        }
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
        Cipher newCipher = new Cipher(loc, plugin.getGame());
        ciphers.add(newCipher);
        return newCipher;
    }

    public static void removeDecodingSurvivor(Survivor survivor) {
        for (Cipher c : ciphers) {
            if (c.getSurvivorsDecoding().contains(survivor)) {
                c.getSurvivorsDecoding().remove(survivor);
            }
        }
    }

    public static Cipher getCipherFromSurvivor(Survivor survivor) {
        for (Cipher c : ciphers) {
            if (c.getSurvivorsDecoding().contains(survivor)) {
                return c;
            }
        }
        return null;
    }
}
