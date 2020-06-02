package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Manages all survivors and the scoreboard
 */
public class SurvivorManager {
    public static HashMap<Player, Survivor> survivors;

    public static IdentityV plugin;

    public SurvivorManager(IdentityV plugin) {
        this.plugin = plugin;
        survivors = new HashMap<Player, Survivor>();
        new BukkitRunnable() {

            public void run() {
                for (Survivor s : getSurvivors()) {
                    if (s.getState() == State.NORMAL) {
                        ScoreboardUtil.set("&a" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.setBar((float) s.getPlayer().getHealth() / (float) 4, "a", s.getBarLine());
                    } else if (s.getState() == State.INCAP) {
                        ScoreboardUtil.set("&e" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.setBar((float) s.getBleedOutTimer() / (float) Config.getInt("timers.survivor","bleed"), "e", s.getBarLine());
                    } else if (s.getState() == State.CHAIR) {
                        ScoreboardUtil.set("&c" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.setBar((float) s.getChairTimer() / (float) Config.getInt("timers.survivor","chair"), "c", s.getBarLine());
                    } else if (s.getState() == State.BALLOON) {
                        ScoreboardUtil.set("&6" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.set("",s.getBarLine());
                    } else if (s.getState() == State.DEAD) {
                        ScoreboardUtil.set("&8" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.set("", s.getBarLine());
                    } else if (s.getState() == State.ESCAPE) {
                        ScoreboardUtil.set("&f" + s.getPlayer().getDisplayName(), s.getNameLine());
                        ScoreboardUtil.set("", s.getBarLine());
                    }
                }
            }
        }.runTaskTimer(plugin,0,5);

    }

    public static void reset() {
        survivors = new HashMap<Player, Survivor>();
        ScoreboardUtil.reset();
    }

    public static void addSurvivor(Player p) {
        survivors.put(p, new Survivor(plugin,p,plugin.getGame()));
    }

    public static void removeSurvivor(Player p) {
        ScoreboardUtil.clear(survivors.get(p).getNameLine());
        ScoreboardUtil.clear(survivors.get(p).getBarLine());
        survivors.remove(p);
    }

    public static boolean isSurvivor(Player p) {
        return survivors.containsKey(p);
    }

    public static Survivor getSurvivor(Player p) { return survivors.get(p); }

    public static Collection<Survivor> getSurvivors() {
        return survivors.values();
    }

    // Runs whenever someone dies or escapes
    public static void checkIfOver() {
        int escapeCount = 0;
        int playersLeft = 0;
        for (Survivor s : getSurvivors()) {
            if (s.getState() == State.ESCAPE) {
                escapeCount++;
                continue;
            }
            if (s.getState() != State.DEAD && s.getState() != State.ESCAPE) {
                playersLeft++;
            }
        }

        if (playersLeft == 1) {
            if (DungeonManager.getActiveDungeon() != null) {
                DungeonManager.getActiveDungeon().spawn();
            }
            return;
        }

        if (playersLeft == 0) {
            // Game Over
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "Game Over!", ChatColor.GOLD + "" + escapeCount + " Survivors Escaped");
            }
        }
    }
}
