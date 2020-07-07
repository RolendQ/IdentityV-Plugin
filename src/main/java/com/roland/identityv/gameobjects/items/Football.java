package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Football extends Item {
    public int useTime;
    public ItemStack itemStack; // doesn't track durability??

    public Football(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
        //this.itemStack = itemStack;
        this.task = null;
    }

    public boolean use() {
        if (task != null) { // Cancel dash
            stop();
            return true;
        }
        useTime = 0;
        p.setWalkSpeed(0);
        task = new BukkitRunnable() {

            public void run() {
                if (PlayerUtil.isTouchingWall(p)) {
                    p.sendMessage("Crashed");
                    Animations.one(p.getLocation(),"animations.survivor","miss_calibration",9);
                    FreezeActionManager.getInstance().add(p,(3*useTime)+10);
                    cancel();
                    task = null;
                    s.setItem(null);
                    return;
                }
                Vector dashDir = p.getEyeLocation().getDirection();
                dashDir.setY(0);
                dashDir.normalize();
                dashDir.multiply(Config.getDouble("attributes.item","football_speed"));
                useTime++;
                //Console.log("Dash: " + dashDir.toString());
                p.setVelocity(dashDir);

                Animations.one(p.getLocation().clone().add(0,1,0),"animations.item","football_use");

                itemStack = p.getItemInHand(); // TODO if not football, cancel

                int newDur = itemStack.getDurability() + Config.getInt("attributes.item","football_durability");
                itemStack.setDurability((short) newDur);
                p.updateInventory();
                if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                    p.sendMessage("Your football ran out");
                    reduceItem();
                    stop();
                }
            }
        };
        task.runTaskTimer(plugin, 10, 3);
        return true;
    }

    public void stop() {
        p.sendMessage("Cancelled");
        FreezeActionManager.getInstance().add(p,3*useTime);
        task.cancel();
        task = null;
        s.setItem(null);
    }

    @Override
    public Material getMat() {
        return Material.LEATHER_HELMET;
    }
}
