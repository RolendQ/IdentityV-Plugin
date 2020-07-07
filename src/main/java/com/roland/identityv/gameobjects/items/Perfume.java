package com.roland.identityv.gameobjects.items;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.ItemManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import org.bukkit.Location;
import org.bukkit.Material;
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
                    itemStack = p.getItemInHand();

                    int newDur = itemStack.getDurability() + Config.getInt("attributes.item","perfume_durability");
                    itemStack.setDurability((short) newDur);
                    p.updateInventory();
                    if (itemStack.getDurability() >= itemStack.getType().getMaxDurability()) {
                        reset();
                        p.sendMessage("Your perfume ran out");
                        perfumeLoc = null;
                    }
                }
            };
            task.runTaskTimer(plugin, 0, 10);
        } else {
            // Rewind
            reset();
            p.setHealth(savedHealth);
            p.teleport(perfumeLoc);
            FreezeActionManager.getInstance().add(p, Config.getInt("attributes.item","perfume_rewind"));
            perfumeLoc = null;
        }
        return true;
    }

    public void reset() {
        reduceItem();
        armorStand.remove();
        task.cancel();
        task = null;
        ItemManager.addCD(s, Config.getInt("attributes.item","perfume_cd"));
    }

    @Override
    public Material getMat() {
        return Material.GOLD_CHESTPLATE;
    }
}
