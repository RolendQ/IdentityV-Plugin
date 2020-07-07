package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.PlayerUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Manages the survivor's trails
 */
public class TrailManager {
    public static IdentityV plugin;
    //public HashMap<Player, LinkedList<Location>> locations; // Could be survivor

    public TrailManager(final IdentityV plugin) {
        this.plugin = plugin;
        //locations = new HashMap<Player, LinkedList<Location>>();

        new BukkitRunnable() {

            public void run() {
                ConsoleCommandSender console = plugin.getServer().getConsoleSender();

                for (Survivor s : SurvivorManager.getSurvivors()) {
                    Player p = s.getPlayer();

                    if (p.isSneaking() || PlayerUtil.hasInvisEffect(p)) continue; // No trails if you sneak or use wand

                    if (SurvivorManager.getSurvivor(p).getState() != State.NORMAL) continue; // Must be normal state

                    showTrail(p.getLocation().clone());
                }

                // Clones
                for (Location loc : Controller.getEntityLocs()) {
                    showTrail(loc.clone());
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    public static void showTrail(Location loc) {
        Random r = new Random();
        double roundedX = (double) Math.round(r.nextDouble() * 100) / 50; // 0 to 2
        double roundedZ = (double) Math.round(r.nextDouble() * 100) / 50;
        Location newLoc = loc.clone().add(-1 + roundedX, 0.1, -1 + roundedZ);
        for (Player p2 : plugin.getServer().getOnlinePlayers()) { // Show for ALL players (spectators) {
            if (!SurvivorManager.isSurvivor(p2)) {
                for (int i = 0; i < 8; i++) {
                    //p.getWorld().playEffect(p.getLocation().add(0, 0.1, 0), Effect.valueOf(plugin.getConfig().getString("trail_effect").toUpperCase()), plugin.getConfig().getInt("trail_data"));
                    p2.playEffect(newLoc, Effect.valueOf(Config.getStr("animations.survivor","trail").toUpperCase()), 1);
                    // Length is calculated naturally by game
                }
            }
        }
    }
}
