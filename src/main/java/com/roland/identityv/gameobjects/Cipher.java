package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Cipher object (initiates automatically when first decoding)
 */
public class Cipher {
    public Location loc;
    public Block block;
    public int progress;
    public IdentityV plugin;
    public Game game;
    public Location particlesLoc;

    public Cipher(IdentityV plugin, Location loc) {
        this.loc = loc;
        this.plugin = plugin;
        this.block = loc.getWorld().getBlockAt(loc);
        this.progress = 0;
        this.particlesLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.1, loc.getZ());
        this.game = null; // Won't be able to open gates
    }

    public Cipher(IdentityV plugin, Location loc, Game game) {
        this.loc = loc;
        this.plugin = plugin;
        this.block = loc.getWorld().getBlockAt(loc);
        this.progress = 0;
        this.game = game;
    }

    public double getProgress() {
        return progress;
    }

    // Returns if done
    public void decodeBit(int bit) {
        Animations.random(particlesLoc,"animations.survivor","decode",1.5,3);
        progress += bit;
    }

    public void notify(Player p) {
        p.sendMessage("Cipher Machine Progress: "+progress);
    }

    public boolean isDone() {
        return progress >= Config.getInt("timers.survivor","decode");
    }

    public Location getLocation() {
        return loc;
    }

    public void pop() {
        Animations.random(particlesLoc,"animations.survivor","pop",1, 5);
        game.incCiphersDone();
    }
}