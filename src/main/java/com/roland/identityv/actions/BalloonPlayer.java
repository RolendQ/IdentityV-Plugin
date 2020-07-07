package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.BalloonPlayerManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
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
    public BalloonPlayer(IdentityV plugin, final Hunter hunter, final Survivor survivor) {
        this.plugin = plugin;
        final Player hunterP = hunter.getPlayer();
        final Player survivorP = survivor.getPlayer();

        survivor.setHunter(hunter); // set hunter

        hunter.resetInvisTimer();

        plugin.getServer().broadcastMessage(survivorP.getDisplayName() + " was picked up!");


        int balloonPlayerTimer = Config.getInt("timers.hunter","balloon_survivor");
        FreezeActionManager.getInstance().add(survivorP, balloonPlayerTimer);
        BalloonPlayerManager.getInstance().add(hunterP, balloonPlayerTimer);

        Animations.decreasing_ring(hunterP.getLocation(),"animations.hunter","balloon",2.5,balloonPlayerTimer);

        new BukkitRunnable() {
            public void run() {
                survivorP.setExp((float) survivor.getStruggleProgress() / (float) Config.getInt("timers.survivor","struggle"));
                hunter.getPlayer().setPassenger(survivor.getPlayer());
                survivor.setState(State.BALLOON);
                // Survivor can't move
                cancel();
            }
        }.runTaskLater(plugin,balloonPlayerTimer);
    }
}
