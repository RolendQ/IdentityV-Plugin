package com.roland.identityv.gameobjects;

import org.bukkit.entity.Player;

/**
 * Currently unused
 */
public class Hunter {
    public Player player;
    // Maybe add character here

    public Hunter(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
