package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Manages the red light for hunters
 */
public class RedLightManager {
    public static IdentityV plugin;

    public RedLightManager(final IdentityV plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {

            public void run() {

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (SurvivorManager.isSurvivor(p)) continue;
                    Vector eyeD = p.getEyeLocation().getDirection();

                    for (int i = 0; i < 4; i++) {
                        Location loc = new Location(p.getWorld(),p.getLocation().getX()+eyeD.getX(), p.getLocation().getY()-0.8, p.getLocation().getZ()+eyeD.getZ());
                        p.getWorld().playEffect(loc, Effect.valueOf(plugin.getConfig().getString("light_effect").toUpperCase()), plugin.getConfig().getInt("light_data"));
                        eyeD.setX(eyeD.getX() * 1.5);
                        eyeD.setZ(eyeD.getZ() * 1.5);
                    }

                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }
}
