package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.managers.statusmanagers.freeze.StruggleRecoveryManager;
import com.roland.identityv.utils.Animations;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Very simple hunter class
 */
public class Hunter {
    public IdentityV plugin;
    public Player player;
    public Game game;
    public int presence;
    public int invisTimer;
    public BukkitRunnable invisTask;
    public int foggyTimer;
    public boolean hasDetention = false;
    // Maybe add character here

    public Hunter(IdentityV plugin, Player player, Game game) {
        this.plugin = plugin;
        this.player = player;
        this.game = game;
        this.presence = 0;

        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(2);
        player.setSaturation(1000);

        player.setWalkSpeed((float) Config.getDouble("attributes.hunter","walk"));
    }

    public void resetInvisTimer() {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SPEED);
        invisTimer = 0;
        // TODO consider stopping the task and resuming it after pallet break/vault/etc
    }

    public void incPresence(int amount) {
        int prevPresence = presence;
        presence += amount;
        if (presence > 10) presence = 10; // can't exceed 10

        if (presence != prevPresence) {
            player.sendMessage("Presence increased: "+presence);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',ScoreboardUtil.createBar(((float) presence) / 10,"6")),"");
        }

        // Play sound
        if ((prevPresence < 4 && presence >= 4) || (prevPresence < 10 && presence >= 10)) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.NOTE_PLING,1,1);
            }

            if (invisTask != null) invisTask.cancel();
            invisTask = new BukkitRunnable() {
                public void run() {
                    // Make sure not ballooning or frozen
                    if (player.getPassenger() == null && !FreezeActionManager.getInstance().isFrozen(player)) {
                        incInvisTimer(5);
                    }
                }
            };
            invisTask.runTaskTimer(plugin,0,5); // 4 times a second
        }
    }

    public void incInvisTimer(int amount) {
        invisTimer += amount;

        // At milestones, flicker
        if (presence >= 4 && presence < 10) {
            if (invisTimer == 100 || invisTimer == 140 || invisTimer == 160 || invisTimer == 170 ||
                    invisTimer == 175) {
                flicker();
            }

            if (invisTimer == 180) {
                fullInvis();
            }
            return;
        }

        if (presence == 10) {
            if (invisTimer == 60 || invisTimer == 90 || invisTimer == 110 || invisTimer == 120 ||
                    invisTimer == 125) {
                flicker();
            }

            if (invisTimer == 130) {
                fullInvis();
                // Movement speed for Tier 2
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0, true, false),true);
            }
            return;
        }
    }

    public int getPresence() {
        return presence;
    }

    public Player getPlayer() {
        return player;
    }

    public void stun(int duration) {
        // TODO consider adding this as an action object
        Animations.falling_rings(player.getLocation().add(0,1,0),"animations.hunter","stun_recovery", duration);
        FreezeActionManager.getInstance().add(player, duration);

        resetInvisTimer();

        // If holding survivor
        if (player.getPassenger() != null) {
            final Survivor s = SurvivorManager.getSurvivor((Player) player.getPassenger());
            plugin.getServer().broadcastMessage(s.getPlayer().getDisplayName() + " was freed!");

            player.setExp(0);
            SitHandler.unsit(player);
            s.setHunter(null);

            new BukkitRunnable() {

                public void run() {
                    s.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                    s.getPlayer().setHealth(2);
                    s.setState(State.NORMAL);
                }
            }.runTaskLater(plugin, 30); // Must add a delay or else they don't get dismounted
        }
    }

    public void flicker() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Config.getInt("attributes.hunter","flicker_length"), 0, true, false),true);
    }

    public void fullInvis() {
        player.sendMessage("You are now fully invisible");
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, true, false),true);
    }

    public void resetFoggyTimer() {
        if (player.getItemInHand() != null) {
            player.getItemInHand().removeEnchantment(Enchantment.FIRE_ASPECT);
        }
        //foggyTimer = 0;
        Console.log("Updating durability");

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack == null) return;

        itemStack.setDurability((short) (itemStack.getType().getMaxDurability()-1));
        new BukkitRunnable() {

            public void run() {
                //foggyTimer++;

                int newDur = itemStack.getDurability() - Config.getInt("attributes.hunter","foggy_durability");
                itemStack.setDurability((short) newDur);
                //player.updateInventory();
                if (itemStack.getDurability() <= 0) {
                    if (player.getItemInHand() != null) {
                        player.getItemInHand().addEnchantment(Enchantment.FIRE_ASPECT,1);
                    }
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(plugin,0,10);
    }

    public boolean hasDetention() {
        return hasDetention;
    }

    public void setDetention(boolean hasDetention) {
        this.hasDetention = hasDetention;
    }
}
