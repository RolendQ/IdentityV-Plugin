package com.roland.identityv.managers.statusmanagers.freeze;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.statusmanagers.ActionManager;
import com.roland.identityv.utils.XPBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Manages actions that involve freezing the player (mostly hunter)
 */
public abstract class FreezeActionManager { // used to extend ActionManager
    public IdentityV plugin;

    public FreezeActionManager(IdentityV plugin) {
        this.plugin = plugin;
        instance = this;
        tasks = new HashMap<Player, BukkitRunnable>();
    }

    public static FreezeActionManager instance;
    public HashMap<Player, BukkitRunnable> tasks;

    public static FreezeActionManager getInstance() {
        return instance;
    }

    public void add(final Player p, long time) {
        FreezeHandler.freeze(p);
        XPBar.decreasing(p,time);
        tasks.put(p, new BukkitRunnable() {
            public void run() {
                FreezeHandler.unfreeze(p);
                tasks.remove(p);
                cancel();
            }
        });
        tasks.get(p).runTaskLater(plugin, time);
    }

    public void remove(Player p) {
        if (tasks.containsKey(p)) {
            FreezeHandler.unfreeze(p);
            tasks.get(p).cancel();
            tasks.remove(p);
        }
    }

    public boolean isFrozen(Player p) {
        return tasks.containsKey(p);
    }
}
