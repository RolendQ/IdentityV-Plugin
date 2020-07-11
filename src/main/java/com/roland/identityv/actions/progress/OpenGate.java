package com.roland.identityv.actions.progress;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Gate;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OpenGate {
    public OpenGate(final Survivor s, final Gate gate) {
        if (s.game != null && s.game.getCiphersDone() < 5) return; // need to do 5 ciphers

        s.setAction(Action.OPEN);
        Console.log("Start open gate");
        gate.setOpener(s);

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                gate.incProgress(10);
                Player player = s.getPlayer();
                player.setExp((float) gate.getProgress() / (float) Config.getInt("timers.survivor","open_gate"));
                if (player.getExp() >= 1) {
                    gate.open();
                    player.sendMessage("You have opened the gate");
                    s.clearActionRunnable();
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, 5, 10);
    }
}
