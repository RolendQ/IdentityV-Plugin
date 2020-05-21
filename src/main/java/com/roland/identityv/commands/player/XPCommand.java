package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Holograms;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class XPCommand extends PlayerCommand {
    public XPCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Adjust xp bar
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(final Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("xpbar")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reset")) {
                    p.setLevel(0);
                    p.setExp(0);
                    return true;
                }

                if (args[0].equalsIgnoreCase("speed")) {
                    final long duration = TimeUnit.SECONDS.toNanos(Integer.parseInt(args[1]));
                    final long start = System.nanoTime(); //start point
                    new BukkitRunnable() {
                        public void run() {
                            long diff = System.nanoTime() - start;
                            if (diff > duration) {
                                cancel();
                            }
                            p.setExp(((float) diff / (float) duration));
                        }
                    }.runTaskTimer(plugin, 0, 1);
                    return true;
                }

                p.setExp((float) Double.parseDouble(args[0]));
            }
        }
        return true;
    }
}
