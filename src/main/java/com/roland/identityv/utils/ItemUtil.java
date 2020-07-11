package com.roland.identityv.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtil {
    public static ItemStack create(Material mat, int amount) {
        if (mat == Material.FIREWORK) {
            ItemStack is = new ItemStack(Material.FIREWORK,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.RED + "Flare Gun");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Shoot a seeking flare at the nearest hunter");
            lore.add("to stun them for a moderate amount of time");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.IRON_PICKAXE) {
            ItemStack is = new ItemStack(Material.IRON_PICKAXE,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Controller");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Spawn a robot that you can use to decode or heal");
            lore.add("The hunter can destroy it with one hit");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.LEATHER_HELMET) {
            ItemStack is = new ItemStack(Material.LEATHER_HELMET,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.RED + "Football");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Start dashing to either escape the hunter or");
            lore.add("to crash into the hunter and stun them");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.IRON_CHESTPLATE) {
            ItemStack is = new ItemStack(Material.IRON_CHESTPLATE,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "Elbow Pad");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Use while touching a wall to dart forward briefly");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.GOLD_CHESTPLATE) {
            ItemStack is = new ItemStack(Material.GOLD_CHESTPLATE,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "Perfume");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Leave behind a reflection that you can rewind to");
            lore.add("within several seconds to restore lost health");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.BLAZE_ROD) {
            ItemStack is = new ItemStack(Material.BLAZE_ROD,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "Wand");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Leave behind a clone that can block a pathway");
            lore.add("or absorb a hit for you");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }
        if (mat == Material.SHEARS) {
            ItemStack is = new ItemStack(Material.SHEARS,amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Syringe");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Use to slowly self heal while at half health");
            im.setLore(lore);
            is.setItemMeta(im);
            return is;
        }


        return null;
    }
}
