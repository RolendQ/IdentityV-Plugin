package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Survivor;
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

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!SurvivorManager.isSurvivor(p)) continue; // Must be survivor

                    if (p.isSneaking()) continue; // No trails if you sneak

                    if (SurvivorManager.getSurvivor(p).getState() != State.NORMAL) continue; // Must be normal state

                    // Setup list of trails
//                    LinkedList<Location> list = locations.get(p);
//                    if (list == null) {
//                        list = new LinkedList<Location>();
//                        for (int i = 0; i < 10; i++) {
//                            Location newLoc = new Location(p.getWorld(), p.getLocation().getX(),
//                                    p.getLocation().getY()+0.25, p.getLocation().getZ());
//                            list.add(newLoc);
//                        }
//                        locations.put(p, list);
//                    } else {
//                        if (SurvivorManager.getSurvivor(p).getState() == State.NORMAL) {
//                            Location newLoc = new Location(p.getWorld(), p.getLocation().getX(),
//                                    p.getLocation().getY(), p.getLocation().getZ());
//                            list.add(newLoc);
//                        }
//                        if (list.size() > 6) list.removeFirst();
//                    }
//
//                    for (Location loc : list) {
//                        p.getWorld().playEffect(loc, Effect.valueOf(plugin.getConfig().getString("trail_effect").toUpperCase()), plugin.getConfig().getInt("trail_data"));
//                    }

//                    // display for each hunter
//                    for (Entity en : p.getNearbyEntities(30,20,30)) {
//                        if (en.getType() == EntityType.PLAYER) {
//                            Player otherP = (Player) en;
//                            if (!SurvivorManager.isSurvivor(p)) {
//                                for (Location loc : list) {
//                                    p.playEffect(loc, Effect.valueOf(plugin.getConfig().getString("trail_effect").toUpperCase()), plugin.getConfig().getInt("trail_data"));
//                                }
//                            }
//                        }
//                    }
                     for (int i = 0; i < 8; i++) {
                         p.getWorld().playEffect(p.getLocation().add(0, 0.1, 0), Effect.valueOf(plugin.getConfig().getString("trail_effect").toUpperCase()), plugin.getConfig().getInt("trail_data"));
                     }
                 }
            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
