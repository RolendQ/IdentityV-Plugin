package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ElbowPad extends Item {
    public int useTime = 0;

    public ElbowPad(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
        this.task = null;
    }

    public boolean use() {
        if (PlayerUtil.isTouchingWall(p)) {

            Animations.one(p.getLocation(),"animations.item","elbowpad_use",3);

            useTime = 0;
            reduceItem();
            task = new BukkitRunnable() {
                public void run() {
                    useTime++;
                    if (useTime == 5) {
                        cancel();
                        // TODO NEEDS TESTING? UNSURE IF BELOW LINE BREAKS STUFF
                        task = null;
                        return;
                    }
                    // Dash
                    Vector dashDir = p.getEyeLocation().getDirection();
                    dashDir.setY(0);
                    dashDir.normalize();
                    dashDir.multiply(3);
                    useTime++;
                    p.setVelocity(dashDir);
                }
            };
            task.runTaskTimer(plugin, 10, 2);

            return true;
        }
        return false;
    }

    @Override
    public int getCD() {
        return Config.getInt("attributes.item","elbowpad_cd");
    }

    @Override
    public Material getMat() {
        return Material.IRON_CHESTPLATE;
    }
}
