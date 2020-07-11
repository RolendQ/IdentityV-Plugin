package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerItemHeldListener implements Listener {
    private IdentityV plugin;

    public PlayerItemHeldListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when a player changes hand slot
     * @param e
     */
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        if (SurvivorManager.isSurvivor(e.getPlayer())) {
            Survivor s = SurvivorManager.getSurvivor(e.getPlayer());

            if (s.getItem() != null && e.getNewSlot() != 0) {
                //e.getPlayer().sendMessage("Cancelled hand slot change");
                e.setCancelled(true);
            }
        }
    }
}
