package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Plays the particle effects in a given pattern
 */
public class Animations {
    public static IdentityV plugin;

    public Animations(IdentityV plugin) {
        Animations.plugin = plugin;
    }

    public static void one(Location loc, String path, String key) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            loc.getWorld().playEffect(loc, Effect.valueOf(effect), 1);
        } catch (Exception e) {

        }
    }

    public static void one(Location loc, String path, String key, int data) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            loc.getWorld().playEffect(loc, Effect.valueOf(effect), data);
        } catch (Exception e) {

        }
    }

    public static void multiple(Location loc, String path, String key, int number) {
        try {
            for (int i = 0; i < number; i++) {
                String effect = Config.getStr(path, key).toUpperCase();
                loc.getWorld().playEffect(loc, Effect.valueOf(effect), 1);
            }
        } catch (Exception e) {

        }
    }

    public static void random(Location loc, String path, String key, double range, int number) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            for (int i = 0; i < number; i++) {
                Random r = new Random();
                Location newLoc = new Location(loc.getWorld(), loc.getX() + ((-1*range) + 2 * range * r.nextDouble()), loc.getY() + ((-1*range) + 2 * range * r.nextDouble()),
                        loc.getZ() + ((-1*range) + 2 * range * r.nextDouble()));
                loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
            }
        } catch (Exception e) {

        }
    }

    public static void cube(Location loc, String path, String key, int size) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            for (int x = -size; x <= size; x++) {
                for (int y = -size; y <= size; y++) {
                    for (int z = -size; z <= size; z++) {
                        Location newLoc = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                        loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static void helix(Location loc, String path, String key, int radius) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            for (double y = 0; y <= 10; y += 0.1) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);
                Location newLoc = new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
                loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
            }
        } catch (Exception e) {

        }
    }

    public static void sphere(Location loc, String path, String key, double rad) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            for (double i = 0; i <= Math.PI; i += Math.PI / 5) {
                double radius = rad * Math.sin(i);
                double y = Math.cos(i);
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 5) {
                    double x = Math.cos(a) * radius;
                    double z = Math.sin(a) * radius;
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
                    loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
                }
            }
        } catch (Exception e) {

        }
    }

    public static void ring(Location loc, String path, String key, double radius) {
        try {
            String effect = Config.getStr(path,key).toUpperCase();
            for (double theta = 0; theta <= 2*Math.PI; theta += 0.5) {
                double x = radius * Math.cos(theta);
                double z = radius * Math.sin(theta);
                Location newLoc = new Location(loc.getWorld(), loc.getX()+x, loc.getY(), loc.getZ()+z);
                loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
            }
        } catch (Exception e) {

        }
    }

    public static void decreasing_ring(final Location loc, String path, String key, final double initialRadius, double time) {
        try {
            final String effect = Config.getStr(path,key).toUpperCase();
            final double[] timer = {time};
            final double initialTime = time;
            new BukkitRunnable() {

                public void run() {
                    double radius = initialRadius * (timer[0]/initialTime);
                    if (radius <= 0) {
                        cancel();
                        return;
                    }
                    for (double theta = 0; theta <= 2*Math.PI; theta += 0.5) {
                        double x = radius * Math.cos(theta);
                        double z = radius * Math.sin(theta);
                        Location newLoc = new Location(loc.getWorld(), loc.getX()+x, loc.getY(), loc.getZ()+z);
                        loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
                    }
                    timer[0] -= 10;
                }
            }.runTaskTimer(plugin, 0, 10);
        } catch (Exception e) {

        }
    }

    public static void falling_rings(final Location loc, String path, String key, double time) {
        try {
            final String effect = Config.getStr(path,key).toUpperCase();
            final double[] timer = {time};
            final double initialTime = time;
            new BukkitRunnable() {

                public void run() {

                    double i = (Math.PI) * (1 - (timer[0]/initialTime));

                    if (i > Math.PI) {
                        cancel();
                        return;
                    }
                    double radius = 2 * Math.sin(i);
                    double y = Math.cos(i);
                    for (double a = 0; a < Math.PI * 2; a += Math.PI / 5) {
                        double x = Math.cos(a) * radius;
                        double z = Math.sin(a) * radius;
                        Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
                        loc.getWorld().playEffect(newLoc, Effect.valueOf(effect), 1);
                    }
                    timer[0] -= 10;
                }
            }.runTaskTimer(plugin, 0, 10);
        } catch (Exception e) {

        }
    }
}
