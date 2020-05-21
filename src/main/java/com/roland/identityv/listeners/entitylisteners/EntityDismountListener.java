package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EntityDismountListener implements Listener {

    private IdentityV plugin;

    public EntityDismountListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when any entity dismounts another one
     * @param e
     */
    @EventHandler
    public void onEntityDismount(final EntityDismountEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {

            if (!SitHandler.wasForcibyEjected()) { // If player did it on their own, remount them
                new BukkitRunnable() {
                    public void run() {
                        e.getDismounted().setPassenger(e.getEntity()); // remount
                    }
                }.runTaskLater(plugin, 3); // Must have a delay
            }
        }
    }
}