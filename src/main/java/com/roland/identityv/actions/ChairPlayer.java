package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
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
    public ChairPlayer(final IdentityV plugin, final Player hunter, final Player survivor, final RocketChair chair) {
        this.plugin = plugin;

        int chairPlayerTimer = Config.getInt("timers.hunter","chair_survivor");

        ChairPlayerManager.getInstance().add(hunter, chairPlayerTimer);

        Animations.decreasing_ring(hunter.getLocation(),"animations.hunter","chair",2.5,chairPlayerTimer);

        new BukkitRunnable() {
            public void run() {
                SitHandler.unsit(survivor);
                chair.setSurvivor(survivor);
                Survivor s = SurvivorManager.getSurvivor(survivor);
                s.incTimesOnChair(); // adds 1
                if (s.getTimesOnChair() == 3) { // Dead on chair
                    s.death();
                }
                s.setState(State.CHAIR);
                plugin.getServer().broadcastMessage(survivor.getName() + " was chaired");
                cancel();
            }
        }.runTaskLater(plugin,chairPlayerTimer);
    }
}
