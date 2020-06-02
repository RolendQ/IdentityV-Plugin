package com.roland.identityv.listeners.entitylisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EntityShootBowListener implements Listener {

    private IdentityV plugin;

    public EntityShootBowListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when entity shoots a bow
     *
     * @param e
     */
    @EventHandler
    public void onEntityShootBow(final EntityShootBowEvent e) {
        final Arrow arrow = (Arrow) e.getProjectile();
        new BukkitRunnable() {

            public void run() {
                for (Entity en : arrow.getNearbyEntities(6, 6, 6)){
                    if (en != arrow.getShooter() && en instanceof Player && HunterManager.isHunter((Player) en)){
                        if (en.getType().isAlive()){
                            Location from = arrow.getLocation();
                            Location to = en.getLocation().add(0,1.5,0);
                            Vector vFrom = from.toVector();
                            Vector vTo = to.toVector();
                            Vector direction = vTo.subtract(vFrom).normalize(); //Subtracts the to variable to the from variable and normalizes it.
                            arrow.setVelocity(direction); //Sets the arrows newfound direction
                            break;
                        }
                    }
                }
                if (arrow.isDead() || arrow.isOnGround()) {
                    arrow.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 2);



    }
}
