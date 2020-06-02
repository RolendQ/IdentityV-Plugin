package com.roland.identityv.gameobjects.items;

import com.avaje.ebeaninternal.server.type.ScalarTypeURI;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Item {
    public IdentityV plugin;
    public BukkitRunnable task;
    public Player p;
    public Survivor s;

    public abstract boolean use();

    public void reduceItem() {
        ItemStack itemStack = p.getItemInHand();
        if (itemStack.getAmount() > 1)
            itemStack.setAmount(itemStack.getAmount() - 1);
        else {
            p.setItemInHand(null);
        }
        s.setItem(null);
    }

    public Survivor getSurvivor() {
        return s;
    }

    public int getCD() {
        return 0;
    }
    // drop?
}
