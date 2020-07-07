package com.roland.identityv.commands.player;

import com.mojang.authlib.GameProfile;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.gameobjects.items.Wand;
import com.roland.identityv.managers.gamecompmanagers.DungeonManager;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DebugTestCommand extends PlayerCommand {
    public DebugTestCommand(IdentityV plugin) {
        super(plugin);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("debug")) {
            if (args.length == 0) return true;

            if (args[0].equalsIgnoreCase("popcipher")) {
                plugin.getGame().incCiphersDone();
            }
            
            if (args[0].equalsIgnoreCase("spawn")) {
                if (Config.getMapData("emerald") == null) {
                    p.sendMessage("Error: No spawns loaded");
                    return true;
                }

                // TODO for now, random teleport to unoccupied spawn block
                List<Player> survivors = new ArrayList<Player>();
                for (Player player : p.getServer().getOnlinePlayers()) {
                    if (!SurvivorManager.isSurvivor(player)) {
                        // Hunter
                        List<String> hunterSpawns = Config.getMapData("emerald");
                        String[] split = hunterSpawns.get(0).split(",");
                        player.teleport(Adjustments.spawnBlock(player.getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
                    } else {
                        survivors.add(player);
                    }
                }

                // Shuffle to randomize survivor spawns
                Collections.shuffle(survivors);
                List<String> survivorSpawns = Config.getMapData("gold");
                for (int i = 0; i < survivors.size(); i++) {
                    String[] split = survivorSpawns.get(i).split(",");
                    survivors.get(i).teleport(Adjustments.spawnBlock(survivors.get(i).getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
                }

                plugin.getServer().broadcastMessage("Teleported everyone to spawns");
            }

            if (args[0].equalsIgnoreCase("reset")) {
                SurvivorManager.reset(); // resets map of survivors
                HunterManager.reset();
                plugin.createTestGame();
                p.sendMessage("Reset survivors and test game");
            }

            if (args[0].equalsIgnoreCase("dur")) {
                p.sendMessage("Dur: "+p.getItemInHand().getDurability());
                p.getItemInHand().setDurability(Short.parseShort(args[1]));
                p.sendMessage("New dur: "+p.getItemInHand().getDurability());
            }

            if (args[0].equalsIgnoreCase("stack")) {
                p.getItemInHand().setAmount(Integer.parseInt(args[1]));
            }

            if (args[0].equalsIgnoreCase("npc")) {
                NPCs.spawnNPC(p,p.getLocation());
                p.sendMessage("Spawned an npc");
            }

            if (args[0].equalsIgnoreCase("opendungeon")) {
                DungeonManager.getActiveDungeon().open();
            }

            if (args[0].equalsIgnoreCase("dmg")) {
                p.setHealth(2);
                if (SurvivorManager.isSurvivor(p)) {
                    SurvivorManager.getSurvivor(p).increaseSpeed(0.3,60);
                }
            }

            if (args[0].equalsIgnoreCase("presence")) {
                HunterManager.getHunter(p).incPresence(1);
            }

            if (args[0].equalsIgnoreCase("flicker")) {
                HunterManager.getHunter(p).flicker();
            }

            if (args[0].equalsIgnoreCase("sethealth")) {
                p.setMaxHealth(2);
                p.setHealth(2);
            }

//            if (args[0].equalsIgnoreCase("chest")) {
//                Block b = p.getLocation().getBlock();
//                b.setType(Material.CHEST);
//                BlockFace[] faces = {BlockFace.NORTH,BlockFace.SOUTH,BlockFace.EAST, BlockFace.WEST};
//                for (BlockFace face : faces) {
//                    if (b.getRelative(face).getType() == Material.AIR) {
//                        b.getRelative(face).setType(Material.ITEM_FRAME);
//                        ItemFrame i = p.getWorld().spawn(b.getRelative(face).getLocation(), ItemFrame.class);
//                        i.setFacingDirection(face);
//                    }
//                }
//            }

            if (args[0].equalsIgnoreCase("perks")) {
                if (SurvivorManager.isSurvivor(p)) {
                    int[] persona = SurvivorManager.getSurvivor(p).getPersonaWeb();
                    String str = "Your Persona Web: ";
                    for (int n : persona) {
                        str += n + ", ";
                    }
                    p.sendMessage(str);
                }
            }

        }
        return true;
    }
}
