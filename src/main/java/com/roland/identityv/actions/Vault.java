package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.Persona;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.listeners.playerlisteners.PlayerSneakListener;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.VaultManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    public Vault(IdentityV plugin, final Player p, final Location firstLoc, final int direction, int speed, byte color) {
        this.plugin = plugin;
        // Both survivors and hunters can vault

        VaultManager.getInstance().add(p,speed);

        final int[] frame = {0};
        if (SurvivorManager.isSurvivor(p)) {
            Survivor s = SurvivorManager.getSurvivor(p);
            s.setAction(Action.VAULT);

            // Alert
            for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(),20,20,20)) {
                if (entity.getType() == EntityType.PLAYER) {
                    Player p2 = (Player) entity;
                    if (!SurvivorManager.isSurvivor(p2) && !p2.hasLineOfSight(p)) { // Hunter
                        Holograms.alert(p2,p.getLocation(),40);
                    }
                }
            }

            // Speed boost
            // TODO change number
            if (color == DyeColor.LIGHT_BLUE.getData()) {
                //Console.log("Detected blue vault");
                if (s.getPersonaWeb()[Persona.BROKEN_WINDOWS] == Config.getInt("attributes.survivor","boost_cd") / 20) {
                    s.increaseSpeed(Config.getInt("attributes.survivor","boost_speed"), Config.getInt("attributes.survivor","boost_length"));
                    s.boostsCD(Persona.BROKEN_WINDOWS);
                }
            } else if (color == DyeColor.GREEN.getData()) {
                //Console.log("Detected green vault");
                if (s.getPersonaWeb()[Persona.PALLET_BOOST] == Config.getInt("attributes.survivor","boost_cd") / 20) {
                    s.increaseSpeed(Config.getInt("attributes.survivor","boost_speed"), Config.getInt("attributes.survivor","boost_length"));
                    s.boostsCD(Persona.PALLET_BOOST);
                }
            }
        }

        if (HunterManager.isHunter(p)) {
            HunterManager.getHunter(p).resetInvisTimer();
        }

        // Actual vault code
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
                    if (HunterManager.isHunter(p)) {
                        blockWindow(firstLoc);
                    }
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(plugin, 0, speed/4);
    }

    public void blockWindow(final Location loc) {
        // Window Blocker
        // Set to acacia fences
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.ACACIA_FENCE);
        for (BlockFace face : PlayerSneakListener.faces) {
            Block b = loc.getBlock().getRelative(face);
            if (b.getType() == Material.STAINED_CLAY) {
                b.getRelative(BlockFace.UP).setType(Material.ACACIA_FENCE);
            }
        }
        // Delete the fences
        new BukkitRunnable() {
            public void run() {
                loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                for (BlockFace face : PlayerSneakListener.faces) {
                    Block b = loc.getBlock().getRelative(BlockFace.UP).getRelative(face);
                    if (b.getType() == Material.ACACIA_FENCE) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(plugin, Config.getInt("timers.hunter","window_blocker_length"));
    }
}
