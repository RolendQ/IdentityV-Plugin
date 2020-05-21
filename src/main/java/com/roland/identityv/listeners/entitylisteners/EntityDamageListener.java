package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class EntityDamageListener implements Listener {

    private IdentityV plugin;

    public EntityDamageListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when an entity is damaged by anything (cancel most natural damage)
     * @param e
     */
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        Console.log("Cause " + e.getCause().name());

        // Removes all knockback
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                e.getEntity().setVelocity(new Vector());
            }
        }, 1l);

        // Cancel any explosion
        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            e.setCancelled(true);
            return;
        }

        // Return if they are not a player
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player p = (Player) e.getEntity();

        // Cancel if they are incapacitated, ballooned, chaired
        if (SurvivorManager.getSurvivor(p).getState() != State.NORMAL) {
            e.setCancelled(true);
            return;
        }
    }
}