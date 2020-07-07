package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Syringe extends Item {
    public Syringe(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
    }

    public boolean use() {
        // Full health
        if (p.getHealth() >= 4) {
            return false;
        }

        if (task != null) { // Cancel syringe
            stop();
            return true;
        }
        Console.log("Starting syringe");
        s.setAction(Action.SELFHEAL);

        s.setActionRunnable(new BukkitRunnable() {
            public void run() {
                if (s.getAction() != Action.SELFHEAL) { // Player cleared it
                    s.clearActionRunnable();
                    return;
                }

                s.incHealingProgress(10);

                p.setExp((float) s.getHealingProgress() / (float) Config.getInt("timers.survivor","heal"));
                if (p.getExp() >= 1) { // Finished healing
                    p.setHealth(p.getHealth() + 2);
                    s.setHealingProgress(0);
                    p.sendMessage("You have healed yourself");
                    s.clearActionRunnable();
                }

                ItemStack itemStack = p.getItemInHand();
                int newDur = itemStack.getDurability() + Config.getInt("attributes.item","syringe_durability");
                itemStack.setDurability((short) newDur);
                p.updateInventory();
                if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                    p.sendMessage("Your syringe ran out");
                    reduceItem();
                    stop();
                }
            }
        });
        task = s.getActionRunnable();
        task.runTaskTimer(plugin, 5, Config.getInt("attributes.item","syringe_timer_speed")); // slower
        return true;
    }

    public void stop() {
        p.sendMessage("Cancelled");
        s.clearActionRunnable();
        task.cancel();
        task = null;
    }

    @Override
    public Material getMat() {
        return Material.SHEARS;
    }
}
