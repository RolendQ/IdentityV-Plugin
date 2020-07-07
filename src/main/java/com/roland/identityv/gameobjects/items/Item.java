package com.roland.identityv.gameobjects.items;

import com.avaje.ebeaninternal.server.type.ScalarTypeURI;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Item {
    public IdentityV plugin;
    public BukkitRunnable task;
    public Player p;
    public Survivor s;

    public abstract boolean use();

    public abstract Material getMat();

    public void reduceItem() {
        ItemStack itemStack = p.getItemInHand(); // TODO
        if (itemStack.getAmount() > 1) {
            // Remove one
            itemStack.setAmount(itemStack.getAmount() - 1);
            itemStack.setDurability((short) 0);
        } else {
            // Clear hand
            p.setItemInHand(null);
            // Reset item
            s.setItem(null);
        }
    }

    public Survivor getSurvivor() {
        return s;
    }

    public int getCD() {
        return 0;
    }
    // drop?
}
