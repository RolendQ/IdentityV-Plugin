package com.roland.identityv.actions.animated;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.listeners.playerlisteners.PlayerSneakListener;
import com.roland.identityv.managers.statusmanagers.freeze.DestroyPalletManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DestroyPallet {
    /**
     * Initiates the action when a hunter destroys a pallet
     * @param block
     * @param hunter
     */
    public DestroyPallet(final Block block, final Hunter hunter) {
        final Player hunterP = hunter.getPlayer();
        int destroyPalletTimer = Config.getInt("timers.hunter","destroy_pallet");

        hunter.resetInvisTimer();
        DestroyPalletManager.getInstance().add(hunterP, destroyPalletTimer);

        Animations.decreasing_ring(hunterP.getLocation(),"animations.hunter","destroy_pallet",2.5, destroyPalletTimer);

        // After animation, remove the pallet
        new BukkitRunnable() {
            public void run() {
                block.setType(Material.AIR);
                for (BlockFace face : PlayerSneakListener.faces) {
                    if (block.getRelative(face).getType() == Material.STAINED_CLAY && block.getRelative(face).getData() == DyeColor.GREEN.getData()) {
                        block.getRelative(face).setType(Material.AIR);
                    }
                }
                //block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(),1F,false, false);
                block.getWorld().playEffect(block.getLocation(), Effect.POTION_BREAK, 1, 10);
                cancel();
            }
        }.runTaskLater(IdentityV.plugin,destroyPalletTimer);
    }
}
