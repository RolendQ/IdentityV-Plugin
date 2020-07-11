package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.gameobjects.items.Football;
import com.roland.identityv.gameobjects.items.Syringe;
import com.roland.identityv.managers.gamecompmanagers.ItemManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerDropItemListener implements Listener {
    private IdentityV plugin;

    public PlayerDropItemListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when a player drops an item
     *
     * @param e
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (SurvivorManager.isSurvivor(e.getPlayer())) {
            Survivor s = SurvivorManager.getSurvivor(e.getPlayer());

            if (s.getItem() != null && ItemManager.isItem(e.getItemDrop().getItemStack().getType())) {
                // Prevent dropping perfume mid perfume and football mid dash, controller while using, syringe while using
                if (e.getItemDrop().getItemStack().getType() == Material.GOLD_CHESTPLATE && e.getItemDrop().getItemStack().getDurability() > 0 ||
                        e.getItemDrop().getItemStack().getType() == Material.LEATHER_HELMET && ((Football) s.getItem()).task != null ||
                        e.getItemDrop().getItemStack().getType() == Material.IRON_PICKAXE && ((Controller) s.getItem()).isRobot ||
                        e.getItemDrop().getItemStack().getType() == Material.SHEARS && ((Syringe) s.getItem()).task != null) {
                    e.setCancelled(true);
                    return;
                }


                // TODO drop whole stack? or just individual
                //e.getPlayer().sendMessage("Dropped item: " + e.getItemDrop().getItemStack().getType());
                if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
                    s.setItem(null);
                }
            }
        }
    }
}