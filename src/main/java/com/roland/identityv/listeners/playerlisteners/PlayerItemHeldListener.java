package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerHeldItemListener implements Listener {
    private IdentityV plugin;

    public PlayerHeldItemListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when a player changes hand slot
     * @param e
     */
    @EventHandler
    public void onPlayerHeldItem(PlayerHeldItemEvent e) {

    }
}
