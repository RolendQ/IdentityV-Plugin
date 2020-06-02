package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Perfume extends Item {
    public Location perfumeLoc;
    public ArmorStand armorStand;
    public double savedHealth;
    public ItemStack itemStack;

    public Perfume(IdentityV plugin, Survivor s) {
        this.plugin = plugin;
        this.s = s;
        this.p = s.getPlayer();
    }

    public boolean use() {

        if (task == null) {
            // Use perfume first time
            perfumeLoc = p.getLocation().clone();
            armorStand = (ArmorStand) p.getWorld().spawnEntity(perfumeLoc, EntityType.ARMOR_STAND);
            savedHealth = p.getHealth();
            Animations.one(p.getLocation(),"animations.item","perfume_use",1);
            task = new BukkitRunnable() {

                public void run() {
                    p.sendMessage("Your perfume ran out");
                    reduceItem();
                    armorStand.remove();
                    task = null;
                }
            };
            task.runTaskLater(plugin, 100); // 5 seconds

        } else {
            // Rewind
            task.cancel();
            task = null;
            reduceItem();
            armorStand.remove();
            p.setHealth(savedHealth);
            p.teleport(perfumeLoc);
            FreezeActionManager.getInstance().add(p, 15);
            perfumeLoc = null;
        }
        return true;
    }

    @Override
    public int getCD() {
        return Config.getInt("attributes.item","perfume_cd");
    }
}
