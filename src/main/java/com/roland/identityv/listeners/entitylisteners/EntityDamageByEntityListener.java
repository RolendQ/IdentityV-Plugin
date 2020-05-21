package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.actions.HunterSwing;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.AttackRecoveryManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Console;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageByEntityListener implements Listener {

    private IdentityV plugin;

    public EntityDamageByEntityListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when entities are damaged by other entities (used for hunter swings)
     * @param e
     */
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        Console.log("Damaged by " + e.getDamager().getName());

        if (e.getDamager().getType() == EntityType.PLAYER) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Player p = (Player) e.getDamager();

                e.setCancelled(true);

                if (FreezeHandler.isFrozen(p)) { // Return if they are frozen
                    e.setCancelled(true);
                    return;
                }

                // Hunter swing
                if (p.getItemInHand().getType() == Material.GOLD_SWORD) {
                    new HunterSwing(plugin,p);
                    e.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }
}
