package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Cipher object (initiates automatically when first decoding)
 */
public class Cipher {
    public Location loc;
    public Block block;
    public int progress;
    public Game game;
    public Location particlesLoc;
    public ArrayList<Survivor> survivorsDecoding;
    public boolean popped = false;

    public Cipher(Location loc) {
        this.loc = loc;
        this.block = loc.getWorld().getBlockAt(loc);
        this.progress = 0;
        this.particlesLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.1, loc.getZ());
        this.game = null; // Won't be able to open gates
        survivorsDecoding = new ArrayList<Survivor>();
    }

    public Cipher(Location loc, Game game) {
        this.loc = loc;
        this.block = loc.getWorld().getBlockAt(loc);
        this.progress = 0;
        this.particlesLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.1, loc.getZ());
        this.game = game;
        survivorsDecoding = new ArrayList<Survivor>();
    }

    public double getProgress() {
        return progress;
    }

    // Returns if done
    public void incProgress(int bit) {
        Animations.random(particlesLoc,"animations.survivor","decode",1.5,3);
        progress += bit;
        if (progress < (Config.getInt("timers.survivor","decode") / 3)) {
            loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1, 0.5F);
        } else if (progress < (Config.getInt("timers.survivor","decode") / 3) * 2) {
            loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1, 1F);
        } else {
            loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1, 1.5F);
        }
    }

    public boolean isDone() {
        return progress >= Config.getInt("timers.survivor","decode");
    }

    public Location getLocation() {
        return loc;
    }

    public void pop() {
        if (!popped) { // To ensure duo decoding doesn't pop twice
            Animations.random(particlesLoc, "animations.survivor", "pop", 1, 5);
            loc.getWorld().playSound(loc, Sound.ANVIL_LAND, 1, 0.5F);
            game.incCiphersDone();
            addBlackGlass();
            popped = true;
        }
    }

    public void addBlackGlass() {
        Block b = loc.getWorld().getBlockAt(loc.add(0,1,0));
        b.setType(Material.STAINED_GLASS);
        b.setData(DyeColor.BLACK.getData());
    }

    public ArrayList<Survivor> getSurvivorsDecoding() {
        return survivorsDecoding;
    }

    public void addSurvivor(Survivor s) {
        survivorsDecoding.add(s);
    }
}
