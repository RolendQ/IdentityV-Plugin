package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.StruggleRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StruggleFree {
    public IdentityV plugin;

    /**
     * Initiates the action when a survivor struggles free from balloons
     * @param plugin
     * @param hunter
     * @param survivor
     */
    public StruggleFree(IdentityV plugin, final Player hunter, final Player survivor) {
        this.plugin = plugin;
        SitHandler.unsit(survivor);
        // Stun hunter

        Animations.one(survivor.getLocation(),"animations.survivor","struggle",11);

        plugin.getServer().broadcastMessage(survivor.getName() + " struggled free!");

        new BukkitRunnable() {

            public void run() {
                survivor.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                survivor.setHealth(2);
                SurvivorManager.getSurvivor(survivor).setState(State.NORMAL);
                StruggleRecoveryManager.getInstance().add(hunter,Config.getInt("timers.hunter","struggle_free"));
            }
        }.runTaskLater(plugin, 20); // Must add a delay or else they don't get dismounted
    }
}
