package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    public IdentityV plugin;

    public PlayerRespawnListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player respawns
     * @param e
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        p.setMaxHealth(4);
        p.setFoodLevel(2);
        p.setSaturation(1000);
        // Have to reset stuff when they respawn too
    }
}
