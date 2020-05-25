package com.roland.identityv.managers.statusmanagers;

import com.roland.identityv.core.IdentityV;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Manages all actions that have a duration
 */
public abstract class ActionManager {

    public static IdentityV plugin;

    public static ActionManager instance;

    public ActionManager(IdentityV plugin) {
        this.plugin = plugin;
        instance = this;
        tasks = new HashMap<Player, BukkitRunnable>();
    }

    public static ActionManager getInstance() {
        return instance;
    }

    public HashMap<Player, BukkitRunnable> tasks;

    public void add(final Player p, long time) {
        //recoveryTimes.put(p, time);
        tasks.put(p, new BukkitRunnable() {
            public void run() {
                //recoveryTimes.put(p, recoveryTimes.get(p)-5);
                tasks.remove(p);
                cancel();
            }
        });
        tasks.get(p).runTaskLater(plugin, time);
    }

    public void remove(Player p) {
        if (tasks.containsKey(p)) {
            tasks.get(p).cancel();
            tasks.remove(p);
        }
    }

    public boolean isDoingTask(Player p) {
        return tasks.containsKey(p);
    }
}
