package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlareGun extends Item {
    public FlareGun(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
        this.task = null;
    }

    public boolean use() {
        if (!hasNearbyHunter()) return false;

        reduceItem();

        final Arrow arrow = p.launchProjectile(Arrow.class);
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
        //p.setItemInHand(null);
        //s.setItem(null);
        return true;
    }

    private boolean hasNearbyHunter() {
        for (Entity en : p.getNearbyEntities(6, 6, 6)) {
            if (en.getEntityId() != p.getEntityId() && en instanceof Player && HunterManager.isHunter((Player) en)) {
                return true;
            }
        }
        return false;
    }
}
