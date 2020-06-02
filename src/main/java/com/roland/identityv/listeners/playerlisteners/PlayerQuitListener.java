package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    public IdentityV plugin;

    public PlayerQuitListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player joins
     * @param e
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (SurvivorManager.isSurvivor(p)) {
            Console.log("Removed a survivor");
            SurvivorManager.removeSurvivor(p);
        } else if (HunterManager.isHunter(p)) {
            Console.log("Removed a hunter");
            HunterManager.removeHunter(p);
        }
    }
}