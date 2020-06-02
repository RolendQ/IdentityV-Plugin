package com.roland.identityv.actions;

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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChairPlayer {
    public IdentityV plugin;

    /**
     * Initiates the action when a hunter chairs a survivor at a rocket chair
     * @param plugin
     * @param hunter
     * @param survivor
     * @param chair
     */
    public ChairPlayer(final IdentityV plugin, final Hunter hunter, final Survivor survivor, final RocketChair chair) {
        this.plugin = plugin;
        final Player hunterP = hunter.getPlayer();
        final Player survivorP = survivor.getPlayer();

        int chairPlayerTimer = Config.getInt("timers.hunter","chair_survivor");

        ChairPlayerManager.getInstance().add(hunterP, chairPlayerTimer);

        Animations.decreasing_ring(hunterP.getLocation(),"animations.hunter","chair",2.5,chairPlayerTimer);

        new BukkitRunnable() {
            public void run() {
                SitHandler.unsit(survivorP);
                chair.setSurvivor(survivorP);
                Survivor s = SurvivorManager.getSurvivor(survivorP);
                s.incTimesOnChair(); // adds 1
                if (s.getTimesOnChair() == 3) { // Dead on chair
                    chair.fly();
                    s.death();
                    cancel();
                    return;
                }
                s.setState(State.CHAIR);
                plugin.getServer().broadcastMessage(survivorP.getName() + " was chaired");
                cancel();
            }
        }.runTaskLater(plugin,chairPlayerTimer);
    }
}
