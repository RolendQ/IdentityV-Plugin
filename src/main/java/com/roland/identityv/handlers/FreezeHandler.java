package com.roland.identityv.handlers;

import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Handles freezing and unfreezing players
 */
public class FreezeHandler {
    public static void freeze(Player player) {
        //player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 250));
        //Console.log("Frozen "+player.getName());
        //player.setGameMode(GameMode.SURVIVAL);
        //player.setFlying(false);
        player.setWalkSpeed(0);
        //player.setFoodLevel(2);
        //player.setSaturation(1000);
    }

    public static void unfreeze(Player player) {
        //player.removePotionEffect(PotionEffectType.JUMP);
        if (SurvivorManager.isSurvivor(player)) player.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
        else player.setWalkSpeed((float) Config.getDouble("attributes.hunter","walk"));
        //player.setFoodLevel(20);
    }

    public static boolean isFrozen(Player player) {
        return FreezeActionManager.getInstance().isFrozen(player);
    }
}
