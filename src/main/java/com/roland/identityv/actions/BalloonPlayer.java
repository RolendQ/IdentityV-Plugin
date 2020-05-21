package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.BalloonPlayerManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BalloonPlayer {

    public IdentityV plugin;

    /**
     * Initiates the action when a hunter balloons an incapacitated survivor
     * @param plugin
     * @param hunter
     * @param survivor
     */
    public BalloonPlayer(IdentityV plugin, final Player hunter, final Player survivor) {
        this.plugin = plugin;

        SurvivorManager.getSurvivor(survivor).setHunter(hunter); // set hunter

        plugin.getServer().broadcastMessage(survivor.getDisplayName() + " was picked up!");

        int balloonPlayerTimer = Config.getInt("timers.hunter","balloon_survivor");
        BalloonPlayerManager.getInstance().add(hunter, balloonPlayerTimer);

        Animations.decreasing_ring(hunter.getLocation(),"animations.hunter","balloon",2.5,balloonPlayerTimer);

        new BukkitRunnable() {
            public void run() {
                survivor.setExp((float) SurvivorManager.getSurvivor(survivor).getStruggleProgress() / (float) Config.getInt("timers.survivor","struggle"));
                hunter.setPassenger(survivor);
                SurvivorManager.getSurvivor(survivor).setState(State.BALLOON);
                // Survivor can't move
                cancel();
            }
        }.runTaskLater(plugin,balloonPlayerTimer);
    }
}
