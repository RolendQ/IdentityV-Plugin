package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Gate object (similar to cipher)
 */
public class Gate {
    // Maybe make this related to cipher through a parent class
    public Location loc; // points to keypad (tripwire hook)
    public int progress;
    public IdentityV plugin;
    public Survivor opener; // Only one player can open it at a time
    // Game?

    public Gate(IdentityV plugin, Location loc) {
        this.loc = loc;
        this.plugin = plugin;
        this.progress = 0;
        this.opener = null;
    }

    public double getProgress() {
        return progress;
    }

    // Returns if done
    public void incProgress(int bit) {
        Animations.random(loc,"animations.survivor","decode",1.5,3);
        progress += bit;
    }

    public void notify(Player p) {
        p.sendMessage("Gate Progress: "+progress);
    }

    public boolean isDone() {
        return progress >= Config.getInt("timers.survivor","open_gate");
    }

    public Location getLocation() {
        return loc;
    }

    public void open() {
        Animations.random(loc,"animations.survivor","pop",1, 5);

        // Replace any iron bars within 5 of each direction
        World w = loc.getWorld();
        for (int x = loc.getBlockX() - 5; x < loc.getBlockX() + 5; x++) {
            for (int y = loc.getBlockY() - 5; y < loc.getBlockY() + 5; y++) {
                for (int z = loc.getBlockZ() - 5; z < loc.getBlockZ() + 5; z++) {
                    Block b = w.getBlockAt(x,y,z);
                    if (b.getType() == Material.IRON_FENCE) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void setOpener(Survivor opener) {
        this.opener = opener;
    }

    public Survivor getOpener() {
        return opener;
    }
}
