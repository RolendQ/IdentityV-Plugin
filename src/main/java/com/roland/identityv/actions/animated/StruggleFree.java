package com.roland.identityv.actions.animated;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.StruggleRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StruggleFree {
    /**
     * Initiates the action when a survivor struggles free from balloons
     * @param hunter
     * @param survivor
     */
    public StruggleFree(final Hunter hunter, final Survivor survivor) {
        final Player hunterP = hunter.getPlayer();
        final Player survivorP = survivor.getPlayer();

        SitHandler.unsit(survivorP);

        Animations.one(survivorP.getLocation(),"animations.survivor","struggle",11);

        IdentityV.plugin.getServer().broadcastMessage(survivorP.getName() + " struggled free!");
        //StruggleRecoveryManager.getInstance().add(hunterP,Config.getInt("timers.hunter","struggle_free"));
        hunter.stun(Config.getInt("timers.hunter","struggle_free"));

        // After short delay, make survivor able to move
        new BukkitRunnable() {

            public void run() {
                survivorP.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                survivorP.setHealth(2);
                survivor.setState(State.NORMAL);
            }
        }.runTaskLater(IdentityV.plugin, 20); // Must add a delay or else they don't get dismounted
    }
}
