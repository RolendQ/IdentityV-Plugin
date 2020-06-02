package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
    public IdentityV plugin;
    public Game game;
    public Location particlesLoc;
    public ArrayList<Survivor> survivorsDecoding;

    public Cipher(IdentityV plugin, Location loc) {
        this.loc = loc;
        this.plugin = plugin;
        this.block = loc.getWorld().getBlockAt(loc);
        this.progress = 0;
        this.particlesLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.1, loc.getZ());
        this.game = null; // Won't be able to open gates
        survivorsDecoding = new ArrayList<Survivor>();
    }

    public Cipher(IdentityV plugin, Location loc, Game game) {
        this.loc = loc;
        this.plugin = plugin;
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
        addBlackGlass();
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
