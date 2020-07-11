package com.roland.identityv.commands;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.MapManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class GameCommand implements CommandExecutor {

    private IdentityV plugin;

    public GameCommand(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * All commands for playing a game
     * @param commandSender
     * @param command
     * @param s
     * @param args
     * @return
     */
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("game")) {
            plugin.reloadConfigs();
            plugin.getServer().broadcastMessage("Starting a game...");

            if (args.length > 0) {
                // TODO this makes first name the hunter
//                try {
                    for (int i = 0; i < args.length; i++) {
                        Player p = plugin.getServer().getPlayer(args[i]);
                        // Clear stuff
                        p.setFlying(false);
                        p.setGameMode(GameMode.SURVIVAL);
                        p.getInventory().clear();
                        p.setHealth(p.getMaxHealth());
                        for (PotionEffect effect : p.getActivePotionEffects()) {
                            p.removePotionEffect(effect.getType());
                        }

                        // Hide name
                        ScoreboardUtil.addHiddenName(p.getDisplayName());

                        // Set hunter/survivor
                        if (i == 0) {
                            HunterManager.addHunter(p);
                            ItemStack weapon = new ItemStack(Material.GOLD_SWORD,1);
                            weapon.addEnchantment(Enchantment.FIRE_ASPECT,1);
                            p.getInventory().addItem(weapon);
                        } else {
                            SurvivorManager.addSurvivor(p);
                            p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
                            p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
                        }
                    }
//                } catch (Exception e) {
//                    Console.log("Error: Cannot add the players");
//                }

                // Refresh and spawn
                MapManager.refresh();
                MapManager.spawn();
            }
        }
        return true;
    }
}
