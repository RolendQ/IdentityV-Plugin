package com.roland.identityv.managers.gamecompmanagers;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.*;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemManager {
    public static ArrayList<Material> items = new ArrayList<Material>();
    public static HashMap<Survivor, Integer> itemCDs = new HashMap<Survivor, Integer>();

    public static IdentityV plugin;

    public ItemManager(IdentityV plugin) {
        this.plugin = plugin;

        items.add(Material.FIREWORK);
        items.add(Material.LEATHER_HELMET);
        items.add(Material.GOLD_CHESTPLATE);
        items.add(Material.BLAZE_ROD);
        items.add(Material.SHEARS);
        items.add(Material.IRON_CHESTPLATE);
        items.add(Material.IRON_PICKAXE);
    }

    public static boolean isItem(Material m) {
        return items.contains(m);
    }

    public static void setItem(Material m, Survivor s) {
        Item item = null;
        if (m == Material.FIREWORK) {
            item = new FlareGun(plugin, s);
        } else if (m == Material.LEATHER_HELMET) {
            item = new Football(plugin, s);
        } else if (m == Material.GOLD_CHESTPLATE) {
            item = new Perfume(plugin, s);
        } else if (m == Material.BLAZE_ROD) {
            item = new Wand(plugin, s);
        } else if (m == Material.SHEARS) {
            item = new Syringe(plugin, s);
        } else if (m == Material.IRON_CHESTPLATE) {
            item = new ElbowPad(plugin, s);
        } else if (m == Material.IRON_PICKAXE) {
            item = new Controller(plugin, s);
        }
        s.setItem(item);
    }

    public static void useItem(Material m, final Survivor s) {
        if (s.getState() != State.NORMAL) return;

        if (itemCDs.containsKey(s)) return;

        // If not controller
        if (m != Material.IRON_PICKAXE) {
            s.clearActionRunnable(); // Auto clear any actions upon using the item
        }

        Item item = s.getItem();
        if (item == null) {
            setItem(m,s);
        }
        if (item != null && item.use() && item.getCD() > 0) {
            addCD(s, item.getCD());
        }
    }

    public static void addCD(final Survivor s, int itemCD) {
        itemCDs.put(s,itemCD);
        // Decrease item cd
        new BukkitRunnable() {
            public void run() {
                int cd = itemCDs.get(s);
                s.getPlayer().setLevel(cd);
                if (cd > 0) {
                    itemCDs.put(s, cd - 1);
                } else {
                    itemCDs.remove(s);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public static boolean isStackable(Material mat) {
        return mat == Material.IRON_CHESTPLATE || mat == Material.GOLD_CHESTPLATE || mat == Material.BLAZE_ROD;
    }
}
