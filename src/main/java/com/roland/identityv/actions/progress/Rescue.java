package com.roland.identityv.actions.progress;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.Persona;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.RocketChairManager;
import com.roland.identityv.utils.Config;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Rescue {
    public Rescue(final Survivor s, final Survivor chaired) {
        s.setAction(Action.RESCUE);
        chaired.setRescuingProgress(0); // Reset rescuing progress
        chaired.setAction(Action.GETRESCUE);

        s.target = chaired;

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                chaired.incRescuingProgress(1);
                Player player = s.getPlayer();
                player.setExp((float) chaired.getRescuingProgress() / (float) Config.getInt("timers.survivor","rescue"));
                chaired.getPlayer().setExp((float) chaired.getRescuingProgress() / (float) Config.getInt("timers.survivor","rescue"));

                if (player.getExp() >= 1) { // Finished rescuing
                    RocketChairManager.getChair(chaired.getPlayer()).resetSurvivor();
                    //SitHandler.unsit(chaired.getPlayer());
                    SitHandler.fakeUnsit(chaired.getPlayer());
                    // Use tide
                    if (s.getPersonaWeb()[Persona.TIDE_TURNER] > 0) {
                        s.useTide(chaired);
                    }
                    chaired.setAction(Action.NONE);
                    chaired.getPlayer().setHealth(2);
                    chaired.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                    chaired.setRescuingProgress(0);
                    chaired.setState(State.NORMAL);
                    chaired.getPlayer().sendMessage("You have been rescued");
                    chaired.clearActionRunnable();
                    s.clearActionRunnable();
                    IdentityV.plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " rescued " + chaired.getPlayer().getDisplayName());
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 5);
    }
}
