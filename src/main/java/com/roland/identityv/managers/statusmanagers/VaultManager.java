package com.roland.identityv.managers.statusmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Console;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VaultManager extends ActionManager {
    public VaultManager(IdentityV plugin) {
        super(plugin);
        instance = this;
    }

    public static VaultManager instance;

    public static VaultManager getInstance() {
        return instance;
    }

    // For detecting multiple vaults
    public boolean hasNearbyVaults(Player attemptVaulter) {
        for (Entity en : attemptVaulter.getNearbyEntities(2.5,2.5,2.5)) {
            if (en instanceof Player) {
                Player p = (Player) en;
                if (isDoingTask(p)) {
                    Console.log("Found nearby vaulter");
                    return true;
                }
            }
        }
        return false;
    }
}
