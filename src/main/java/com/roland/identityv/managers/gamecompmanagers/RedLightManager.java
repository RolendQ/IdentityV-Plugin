package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.items.Controller;
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

                for (Hunter h : HunterManager.getHunters()) {
                    Player hunterP = h.getPlayer();
                    Vector eyeD = hunterP.getEyeLocation().getDirection();

                    boolean hasTinnitus = false;
                    for (Player p2 : plugin.getServer().getOnlinePlayers()) { // Show for all players (spectators)
                        if (!HunterManager.isHunter(p2)) {
                            // Light
                            for (int i = 0; i < 4; i++) {
                                Location loc = new Location(hunterP.getWorld(),hunterP.getLocation().getX()+eyeD.getX(), hunterP.getLocation().getY()-0.8, hunterP.getLocation().getZ()+eyeD.getZ());
                                p2.playEffect(loc, Effect.valueOf(plugin.getConfig().getString("light_effect").toUpperCase()), plugin.getConfig().getInt("light_data"));
                                eyeD.setX(eyeD.getX() * 1.5);
                                eyeD.setZ(eyeD.getZ() * 1.5);
                            }

                            // Tinnitus for survivors
                            if (SurvivorManager.isSurvivor(p2)) {
                                double distance = hunterP.getLocation().distance(p2.getLocation());
                                if (!hasTinnitus && distance < 10) {
                                    hasTinnitus = true;
                                }
                            }
                        }
                    }

                    // Tinnitus for clones
                    if (!hasTinnitus) {
                        for (Location loc : Controller.getEntityLocs()) {
                            if (hunterP.getLocation().distance(loc) < 10) hasTinnitus = true;
                        }
                    }

                    // Air and bubbles?
                    if (hasTinnitus) hunterP.setLevel(1);
                    else hunterP.setLevel(0);

                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }
}
