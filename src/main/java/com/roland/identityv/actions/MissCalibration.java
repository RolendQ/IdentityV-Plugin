package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Holograms;
import org.bukkit.entity.Player;

public class MissCalibration {
    public IdentityV plugin;

    public MissCalibration(IdentityV plugin, Player p) {
        this.plugin = plugin;
        p.sendMessage("You failed the calibration!");

        // Alert
        for (Player p2 : p.getServer().getOnlinePlayers()) {
            if (!SurvivorManager.isSurvivor(p)) { // Hunter
                Holograms.alert(p2,p.getLocation());
            }
        }

        if (SurvivorManager.isSurvivor(p) && SurvivorManager.getSurvivor(p).getAction() == Action.DECODE) {
            SurvivorManager.getSurvivor(p).clearActionRunnable(); // stop decoding
        }

        Animations.one(p.getLocation(),"animations.survivor","miss_calibration",5);

        FreezeActionManager.getInstance().add(p, Config.getInt("timers.survivor","miss_calibration"));
    }
}
