package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
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
                // TODO drop whole stack? or just individual
                e.getPlayer().sendMessage("Dropped item: " + e.getItemDrop().getItemStack().getType());
                if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
                    s.setItem(null);
                }
            }
        }
    }
}