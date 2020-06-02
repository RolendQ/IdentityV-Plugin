package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Adjustments;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Manages all heartbeats for survivors
 */
public class HeartbeatManager {
    public static IdentityV plugin;

    public HeartbeatManager(final IdentityV plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {

            public void run() {
                ConsoleCommandSender console = plugin.getServer().getConsoleSender();

                // Shows only for the respective survivor
                for (Survivor s : SurvivorManager.getSurvivors()) {
                    for (Hunter h : HunterManager.getHunters()) {
                        double distance = s.getPlayer().getLocation().distance(h.getPlayer().getLocation());

                        Location loc = s.getPlayer().getLocation().add(0, 2, 0);

                        long currentTime = h.getPlayer().getWorld().getTime();
                        if (currentTime - s.getLastHeartbeat() > Adjustments.getHeartRate(distance)) {
                            try {
                                s.getPlayer().playSound(loc, Sound.BLAZE_HIT, (float) ((100 - (4 * distance)) / 100), 1);
                                s.setLastHeartbeat(currentTime);
                                String effect = plugin.getConfig().getConfigurationSection("animations").getConfigurationSection("survivor").getString("heartbeat").toUpperCase();
                                s.getPlayer().playEffect(loc, Effect.valueOf(effect), 1);
                            } catch (Exception e) {

                            }
                        }
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 2);
    }
}
