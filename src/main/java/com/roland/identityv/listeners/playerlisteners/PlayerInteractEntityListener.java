package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.BalloonPlayer;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.ItemManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    private IdentityV plugin;

    public PlayerInteractEntityListener(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects when a player clicks on an entity
     * @param e
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() == null) return;


        Console.log("Detected right click: "+e.getRightClicked().getName() + " " + e.getRightClicked().getEntityId() + " "+e.getRightClicked().toString());

        if (FreezeHandler.isFrozen(e.getPlayer())) { // Return if they are frozen
            return;
        }

        Player p = e.getPlayer();

        if (e.getRightClicked().getType() == EntityType.VILLAGER) { // Cancel any villager interaction
            e.setCancelled(true);
        }

        // USE ITEM FIRST
        if (SurvivorManager.isSurvivor(p) && p.getItemInHand() != null) {
            if (ItemManager.isItem(p.getItemInHand().getType())) {
                ItemManager.useItem(p.getItemInHand().getType(), SurvivorManager.getSurvivor(p));
                return;
            }
        }

        if (e.getRightClicked().getType() == EntityType.PLAYER) {

            Player clickedP = (Player) e.getRightClicked();

            if (!SurvivorManager.isSurvivor(clickedP)) return;

            Survivor s = SurvivorManager.getSurvivor(clickedP);

            // HUNTER
            if (HunterManager.isHunter(e.getPlayer())) {
                // BALLOON
                if (s.getState() == State.INCAP && clickedP.getVehicle() == null) {
                    new BalloonPlayer(plugin, HunterManager.getHunter(p), s);
                }
            // SURVIVOR
            } else {

            }
        }

    }
}