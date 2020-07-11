package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;

public class HunterManager {
    public static HashMap<Player, Hunter> hunters;

    public static IdentityV plugin;

    public HunterManager(IdentityV plugin) {
        this.plugin = plugin;
        hunters = new HashMap<Player, Hunter>();
    }

    public static void reset() {
        hunters = new HashMap<Player, Hunter>();
    }

    public static void addHunter(Player p) {
        hunters.put(p, new Hunter(p,plugin.getGame()));
    }

    public static void removeHunter(Player p) {
        hunters.remove(p);
    }

    public static boolean isHunter(Player p) {
        return hunters.containsKey(p);
    }

    public static Hunter getHunter(Player p) { return hunters.get(p); }

    public static Collection<Hunter> getHunters() {
        return hunters.values();
    }
}
