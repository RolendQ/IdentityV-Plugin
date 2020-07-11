package com.roland.identityv.actions.animated;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.ChairPlayerManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Holograms;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChairPlayer {

    /**
     * Initiates the action when a hunter chairs a survivor at a rocket chair
     * @param hunter
     * @param survivor
     * @param chair
     */
    public ChairPlayer(final Hunter hunter, final Survivor survivor, final RocketChair chair) {
        final Player hunterP = hunter.getPlayer();
        final Player survivorP = survivor.getPlayer();

        int chairPlayerTimer = Config.getInt("timers.hunter","chair_survivor");

        ChairPlayerManager.getInstance().add(hunterP, chairPlayerTimer);

        Animations.decreasing_ring(hunterP.getLocation(),"animations.hunter","chair",2.5,chairPlayerTimer);

        // After animation, sit the survivor
        new BukkitRunnable() {
            public void run() {
                SitHandler.unsit(survivorP);
                chair.setSurvivor(survivorP);
                Survivor s = SurvivorManager.getSurvivor(survivorP);
                s.incTimesOnChair();
                if (s.getTimesOnChair() == 2) { // Second time
                    s.setChairTimer(Config.getInt("timers.survivor","chair")/2);
                } else if (s.getTimesOnChair() == 3) { // Dead on chair
                    chair.fly();
                    s.death();
                    return;
                }
                s.setState(State.CHAIR);
                Holograms.alertSurvivors(hunter,60);
                IdentityV.plugin.getServer().broadcastMessage(survivorP.getName() + " was chaired!");
            }
        }.runTaskLater(IdentityV.plugin,chairPlayerTimer);
    }
}
