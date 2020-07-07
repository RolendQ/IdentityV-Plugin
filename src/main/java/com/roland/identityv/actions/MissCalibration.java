package com.roland.identityv.actions;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Holograms;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MissCalibration {
    public IdentityV plugin;

    public MissCalibration(IdentityV plugin, Survivor survivor) {
        this.plugin = plugin;
        final Player survivorP = survivor.getPlayer();


        if (survivorP != null) {
            survivorP.sendMessage("You failed the calibration!");


            if (survivor.getAction() == Action.DECODE) {
                survivor.clearActionRunnable(); // stop decoding or healing
            }

            // Alert
            for (Hunter h : HunterManager.getHunters()) {
                Holograms.alert(h.getPlayer(), survivorP.getLocation(), 40);
            }

            Animations.one(survivor.getPlayer().getLocation(),"animations.survivor","miss_calibration",5);

            // TODO may move this to affect player
            FreezeActionManager.getInstance().add(survivorP, Config.getInt("timers.survivor","miss_calibration"));
        } else {
            survivor.getOwner().getPlayer().sendMessage("Your robot failed a calibration");

            ((Controller) survivor.getOwner().getItem()).clearRobotTask();

            // Bot triggers alert
            for (Hunter h : HunterManager.getHunters()) {
                Holograms.alert(h.getPlayer(), ((Controller) survivor.getOwner().getItem()).getEntityLoc(), 40);
            }

            Animations.one(((Controller) survivor.getOwner().getItem()).getEntityLoc(),"animations.survivor","miss_calibration",5);
        }
    }

//    // for robot
//    public MissCalibration(IdentityV plugin, Location loc) {
//        this.plugin = plugin;
//
//        // Alert
//        for (Hunter h : HunterManager.getHunters()) {
//            Holograms.alert(h.getPlayer(),loc);
//        }
//    }
}
