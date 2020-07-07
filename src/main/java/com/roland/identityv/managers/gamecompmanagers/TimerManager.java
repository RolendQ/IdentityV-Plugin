package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
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
                    if (s.getState() == State.NORMAL) {
                        // Reset crows
                        if (HunterManager.getHunters().size() == 0 || s.getState() != State.NORMAL ||
                                s.getAction() == Action.DECODE || s.getAction() == Action.HEAL || s.getAction() == Action.RESCUE ||
                                s.isVisibleToAHunter()) {
                            s.clearCrowsTimer();
                            continue;
                        }
                        s.incCrowsTimer();
                        if (s.getCrowsTimer() >= Config.getInt("timers.survivor","crows")) {
                            // Crows animation
                            Animations.random(s.getPlayer().getLocation().add(0, 2, 0),"animations.survivor","crows",1, 4);
                            // Alert hunter
                            if (s.getCrowsTimer() % 16 == 0) {
                                for (Hunter h : HunterManager.getHunters()) {
                                    Holograms.alert(h.getPlayer(), s.getPlayer().getLocation(), 40);
                                }
                            }
                        }
                    } else if (s.getState() == State.INCAP) {
                        s.incBleedOutTimer();
                        // TODO for testing
                        //if (s.getBleedOutTimer() % 5 == 0) plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " is bleeding out: "+s.getBleedOutTimer());
                        if (s.getBleedOutTimer() == Config.getInt("timers.survivor","bleed")) s.death();
                    } else if (s.getState() == State.CHAIR) {
                        s.incChairTimer();
                        //if (s.getChairTimer() % 5 == 0) plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " is dying on chair: "+s.getChairTimer());
                        if (s.getChairTimer() == Config.getInt("timers.survivor","chair")/2) s.incTimesOnChair();
                        if (s.getChairTimer() == Config.getInt("timers.survivor","chair")) {
                            RocketChairManager.getChair(s.getPlayer()).fly();
                            s.death();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }
}
