package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Rocket chair object (initiates automatically)
 */
public class RocketChair {
    public Location loc;
    public IdentityV plugin;
    public Player survivor;

    public RocketChair(Location loc, IdentityV plugin) {
        this.loc = loc;
        this.plugin = plugin;
    }

    public void setSurvivor(Player survivor) {
        this.survivor = survivor;
        Console.log("Set survivor: "+survivor.getName());
        SitHandler.sit(survivor,loc);
    }

    public void releaseSurvivor(Player survivor) {
        this.survivor = null;
        SitHandler.unsit(survivor); // May need to move them to a specific location
    }

    public Player getSurvivor() {
        return survivor;
    }

    public boolean isOccupied() {
        return survivor != null;
    }

    public Location getLocation() {
        return loc;
    }

}
