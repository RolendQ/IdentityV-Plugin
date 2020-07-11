package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.utils.Console;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

/**
 * Rocket chair object (initiates automatically)
 */
public class RocketChair {
    public Location loc;
    public Player survivor;
    public boolean isUsed;

    public RocketChair(Location loc) {
        this.loc = loc;
        this.survivor = null;
        this.isUsed = false;
    }

    public void setSurvivor(Player survivor) {
        this.survivor = survivor;
        Console.log("Set survivor: "+survivor.getName());
        //SitHandler.sit(survivor,loc.add(0.5, 0, 0.5));
        // TODO make it look like they are sitting
        SitHandler.fakeSit(survivor,loc.add(0.5, 0, 0.5));
        Console.log("Sitting on chair");
    }

    public void resetSurvivor() {
        this.survivor = null;
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

    public void fly() {
        isUsed = true;
        Console.log("Chair flew away!");
        loc.getBlock().setType(Material.AIR);
        Firework fw = (Firework) survivor.getWorld().spawnEntity(survivor.getLocation().clone().add(0,2,0), EntityType.FIREWORK);
        //fw.detonate();
    }

    public boolean isUsed() {
        return isUsed;
    }


}
