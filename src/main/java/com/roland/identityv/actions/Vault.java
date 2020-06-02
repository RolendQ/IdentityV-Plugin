package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.VaultManager;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Vault {
    public IdentityV plugin;
    public static Vector[][] vaultSteps = {{new Vector(0.5,0,1.5),new Vector(0.5,0.8,1.2),new Vector(0.5,1,0.5),new Vector(0.5,0.8,-0.2),new Vector(0.5,0,-0.5)},
            {new Vector(-0.5,0,0.5),new Vector(-0.2,0.8,0.5),new Vector(0.5,1,0.5),new Vector(1.2,0.8,0.5),new Vector(1.5,0,0.5)},
            {new Vector(0.5,0,-0.5),new Vector(0.5,0.8,-0.2),new Vector(0.5,1,0.5),new Vector(0.5,0.8,1.2),new Vector(0.5,0,1.5)},
            {new Vector(1.5,0,0.5),new Vector(1.2,0.8,0.5),new Vector(0.5,1,0.5),new Vector(-0.2,0.8,0.5),new Vector(-0.5,0,0.5)},
    };

    public Vault(IdentityV plugin, final Player p, final Location firstLoc, final int direction, int speed) {
        this.plugin = plugin;
        // Both survivors and hunters can vault

        VaultManager.getInstance().add(p,speed);

        final int[] frame = {0};
        if (SurvivorManager.isSurvivor(p)) {
            SurvivorManager.getSurvivor(p).setAction(Action.VAULT);

            // Alert
            for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(),20,20,20)) {
                if (entity.getType() == EntityType.PLAYER) {
                    Player p2 = (Player) entity;
                    if (!SurvivorManager.isSurvivor(p2) && !p2.hasLineOfSight(p)) { // Hunter
                        Holograms.alert(p2,p.getLocation());
                    }
                }
            }
        }

        new BukkitRunnable() {

            public void run() {
                //Console.log("Vault step");
                Vector v = Vault.vaultSteps[direction][frame[0]];
                Location newLoc = new Location(firstLoc.getWorld(), firstLoc.getX() + v.getX(),
                        firstLoc.getY() + v.getY(),
                        firstLoc.getZ() + v.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                p.teleport(newLoc);
                p.setAllowFlight(true);
                p.setFlySpeed(0);
                p.setFlying(true);
                frame[0]++;
                // Finish
                if (frame[0] > 4) {
                    p.setFlying(false);
                    p.setFlySpeed(0.1F);
                    if (SurvivorManager.isSurvivor(p)) {
                        SurvivorManager.getSurvivor(p).setAction(Action.NONE);
                    }
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(plugin, 0, speed/4);
    }


}
