package com.roland.identityv.actions.progress;

import com.roland.identityv.actions.animated.StruggleFree;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class Struggle {
    public Struggle(Survivor s) {
        Player hunterP = s.getHunter().getPlayer();
        if (s.getHunter() == null) return;

        s.incStruggleProgress(1);
        Player player = s.getPlayer();
        player.setExp((float) s.getStruggleProgress() / (float) Config.getInt("timers.survivor","struggle"));

        if (s.getStruggleProgress() % Config.getInt("timers.survivor","struggle_tilt_interval") == 0) { // Make player tilt
            Console.log("Tilting "+hunterP.getDisplayName()+": "+hunterP.getLocation().getYaw());


            Location newYaw = hunterP.getLocation().clone();
            Random r = new Random();
            int chance = r.nextInt(2);
            int angle = Config.getInt("attributes.hunter","struggle_tilt_angle");
            if (chance == 0) newYaw.setYaw(newYaw.getYaw() + 60);
            else newYaw.setYaw(newYaw.getYaw() - 60);

            hunterP.eject(); // temporarily so they can teleport
            hunterP.teleport(newYaw);
        }
        if (player.getExp() == 1) { // Struggle free
            new StruggleFree(s.getHunter(),s);
            s.setStruggleProgress(0);
            player.setExp(0);
            s.setHunter(null);
        }
    }
}
