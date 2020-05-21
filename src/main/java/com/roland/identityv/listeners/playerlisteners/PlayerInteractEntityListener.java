package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.BalloonPlayer;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
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

        if (FreezeHandler.isFrozen(e.getPlayer())) { // Return if they are frozen
            return;
        }

        if (e.getRightClicked().getType() == EntityType.PLAYER) {

            Player clickedP = (Player) e.getRightClicked();

            if (!SurvivorManager.isSurvivor(clickedP)) return;

            // HUNTER BALLOON
            if (!SurvivorManager.isSurvivor(e.getPlayer())) {
                if (SurvivorManager.getSurvivor(clickedP).getState() == State.INCAP && clickedP.getVehicle() == null) {
                    new BalloonPlayer(plugin, e.getPlayer(), clickedP);
                }
            // SURVIVOR
            } else {
                // HEAL
                if (SurvivorManager.getSurvivor(clickedP).getState() == State.NORMAL ||
                        SurvivorManager.getSurvivor(clickedP).getState() == State.INCAP) {
                    if (clickedP.getHealth() < 4) {
                        SurvivorManager.getSurvivor(e.getPlayer()).startHeal(SurvivorManager.getSurvivor(clickedP));
                    }
                    return;
                }
//                // RESCUE
//                if (SurvivorManager.getSurvivor(clickedP).getState() == State.CHAIR) {
//                    SurvivorManager.getSurvivor(e.getPlayer()).startRescue(SurvivorManager.getSurvivor(clickedP));
//                }
            }
        }


    }
}