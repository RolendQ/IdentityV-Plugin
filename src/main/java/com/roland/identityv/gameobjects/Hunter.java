package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Config;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Very simple hunter class
 */
public class Hunter {
    public IdentityV plugin;
    public Player player;
    public Game game;
    // Maybe add character here

    public Hunter(IdentityV plugin, Player player, Game game) {
        this.plugin = plugin;
        this.player = player;
        this.game = game;

        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(2);
        player.setSaturation(1000);

        player.setWalkSpeed((float) Config.getDouble("attributes.hunter","walk"));
    }

    public Player getPlayer() {
        return player;
    }
}
