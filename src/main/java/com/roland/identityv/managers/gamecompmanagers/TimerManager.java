package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Manages the bleed out and chair timer
 */
public class TimerManager {
    public static IdentityV plugin;

    public TimerManager(final IdentityV plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {

            public void run() {
                for (Survivor s : SurvivorManager.getSurvivors()) {
                    if (s.getState() == State.INCAP) {
                        s.incBleedOutTimer();
                        // TODO for testing
                        if (s.getBleedOutTimer() % 5 == 0) plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " is bleeding out: "+s.getBleedOutTimer());
                        if (s.getBleedOutTimer() == Config.getInt("timers.survivor","bleed")) s.death();
                    } else if (s.getState() == State.CHAIR) {
                        s.incChairTimer();
                        if (s.getChairTimer() % 5 == 0) plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " is dying on chair: "+s.getChairTimer());
                        if (s.getChairTimer() == Config.getInt("timers.survivor","chair")/2) s.incTimesOnChair();
                        if (s.getChairTimer() == Config.getInt("timers.survivor","chair")) s.death();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
