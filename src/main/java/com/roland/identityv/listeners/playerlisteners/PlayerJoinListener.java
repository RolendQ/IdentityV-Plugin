package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerJoinListener implements Listener {

    public IdentityV plugin;

    public PlayerJoinListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player joins
     * @param e
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Console.log("Player joined!");
//        Player p = e.getPlayer();
//        p.setMaxHealth(4);
//        p.setFoodLevel(2);
//        p.setSaturation(1000);
        // Have to reset stuff when they respawn too
    }
}