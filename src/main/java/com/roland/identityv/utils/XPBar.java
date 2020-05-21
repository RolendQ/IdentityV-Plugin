package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

/**
 * Uses the xpbar as a progress bar
 */
public class XPBar {
    public static IdentityV plugin;

    public XPBar(IdentityV plugin) {
        this.plugin = plugin;
    }

    public static void decreasing(final Player p, final long ticks) {
        final long start = p.getWorld().getTime();
        new BukkitRunnable() {
            public void run() {
                long diff = p.getWorld().getTime() - start;
                if (diff > ticks) {
                    cancel();
                }
                p.setExp(1F - ((float) diff / (float) ticks));
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
