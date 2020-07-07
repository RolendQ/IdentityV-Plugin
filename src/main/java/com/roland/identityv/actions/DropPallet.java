package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.managers.statusmanagers.freeze.StunRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Holograms;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
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
    public DropPallet(IdentityV plugin, final Survivor survivor, final Block block, final BlockFace otherFace) {
        this.plugin = plugin;

        // TODO might make the survivor freeze and add animation

        new BukkitRunnable() {
            public void run() {
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                block.setType(Material.STAINED_CLAY);
                block.setData(DyeColor.GREEN.getData());

                Block b = block.getRelative(otherFace);
                b.setType(Material.STAINED_CLAY);
                b.setData(DyeColor.GREEN.getData());

                //block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(),1F,false, false);

                block.getWorld().playEffect(block.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 1, 10);

                // Sound
                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),20,20,20)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Player p = (Player) entity;
                        if (HunterManager.isHunter(p) && !p.hasLineOfSight(survivor.getPlayer())) { // Hunter
                            Holograms.alert(p,block.getLocation(),40);
                        }
                    }
                }

                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),Config.getInt("attributes.survivor","pallet_range"),2,Config.getInt("attributes.survivor","pallet_range"))) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Player p = (Player) entity;
                        if (HunterManager.isHunter(p)) { // Hunter
                            p.getServer().broadcastMessage(survivor.getPlayer().getDisplayName() + " stunned the hunter with a pallet");
                            HunterManager.getHunter(p).stun(Config.getInt("timers.hunter","pallet_stun"));
                        }
                    }
                }
                cancel();
            }
        }.runTaskLater(plugin,Config.getInt("timers.survivor","pallet_delay"));
    }
}
