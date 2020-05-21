package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.managers.statusmanagers.freeze.StunRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Holograms;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DropPallet {

    public IdentityV plugin;

    /**
     * Initiates the action when a survivor drops a pallet
     * @param plugin
     * @param block
     */
    public DropPallet(IdentityV plugin, final Block block) {
        this.plugin = plugin;

        // TODO might make the survivor freeze and add animation

        new BukkitRunnable() {
            public void run() {
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                block.setType(Material.COBBLE_WALL);

                //block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(),1F,false, false);

                block.getWorld().playEffect(block.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 1, 10);

                // Sound
                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),20,20,20)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Player p = (Player) entity;
                        if (!SurvivorManager.isSurvivor(p)) { // Hunter
                            Holograms.alert(p,block.getLocation());
                        }
                    }
                }

                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),1.5,1.5,1.5)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Player p = (Player) entity;
                        if (!SurvivorManager.isSurvivor(p)) { // Hunter
                            Animations.falling_rings(p.getLocation(),"animations.hunter","stun_recovery",Config.getInt("timers.hunter","pallet_stun"));
                            FreezeActionManager.getInstance().add(p, Config.getInt("timers.hunter","pallet_stun"));
                        }
                    }
                }
                cancel();
            }
        }.runTaskLater(plugin,15);
    }
}
