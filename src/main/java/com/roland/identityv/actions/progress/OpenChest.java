package com.roland.identityv.actions.progress;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.gameobjects.Chest;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OpenChest {
    public OpenChest(final Survivor s, final Chest chest) {
        s.setAction(Action.OPENCHEST);
        Console.log("Start open chest");
        chest.setOpener(s);
        s.chest = chest;

        // Freeze for 2 second
        chest.animateOpenAndClose();
        final Player player = s.getPlayer();
        FreezeActionManager.getInstance().add(player, Config.getInt("timers.survivor","open_and_close_chest_duration"));

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                chest.incProgress(10);
                player.setExp((float) chest.getProgress() / (float) Config.getInt("timers.survivor","open_chest"));
                if (player.getExp() >= 1) {
                    chest.open();
                    player.sendMessage("You have opened the chest");
                    s.clearActionRunnable();
                }
            }
        });
        s.getActionRunnable().runTaskTimer(IdentityV.plugin, Config.getInt("timers.survivor","open_and_close_chest_duration") + 5, 10);

    }
}
