package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Persona;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Game object (keeps track of ciphers done)
 */
public class Game {
    public int ciphersDone;
    // Cipher manager? Or reference to ciphers
    // Reference to survivors?

    //public CipherManager cipherM;
    public IdentityV plugin;

    public Game(IdentityV plugin) {
        this.plugin = plugin;
        ScoreboardUtil.set("&e5 CIPHERS",10);
        this.ciphersDone = 0;
        //this.cipherM = cipherM;
    }


    public void incCiphersDone() {
        ciphersDone++;

        if (ciphersDone == 2) {
            plugin.getServer().broadcastMessage("Dungeon has spawned!");
            DungeonManager.spawnRandom();
        }

        if (ciphersDone >= 5) {
            ScoreboardUtil.set("&eEXIT GATES ARE ACTIVE", 10);
            // Highlight exit gates (needs refresh)
            for (Gate g : GateManager.gates) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Holograms.alert(p, g.getLocation(), 200); // 10 seconds
                }
            }

            // Borrowed Time
            for (Survivor s : SurvivorManager.getSurvivors()) {
                if (s.getPersonaWeb()[Persona.BORROWED_TIME] <= 0) return;

                //if (!s.isControllingRobot()) {
                    Console.log(s.getPlayer().getDisplayName() + " activates borrowed");
                    // TODO SPEED
                    if (s.getState() == State.NORMAL) {
                        double newHealth = (s.getPlayer().getHealth() + 2);
                        if (newHealth > 4) newHealth = 4;
                        s.getPlayer().setHealth(newHealth);
                        s.setHealingProgress(0);
                        s.getPlayer().sendMessage("You have been healed by Borrowed Time");
                    } else if (s.getState() == State.INCAP) {
                        s.getPlayer().setHealth(2);
                        s.getPlayer().setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
                        s.setHealingProgress(0);
                        s.setState(State.NORMAL);
                        s.getPlayer().sendMessage("You have been revived by Borrowed Time");
                    }
                    s.increaseSpeed(Config.getDouble("attributes.survivor","borrowed_time_speed"),Config.getInt("attributes.survivor","borrowed_time_length")); // speed boost
                //}
            }
            CipherManager.addBlackGlass();

            // Hunter Detention
            for (final Hunter h : HunterManager.getHunters()) {
                h.setDetention(true); // Could have red eyes effect?
                h.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Config.getInt("attributes.hunter","detention_length"),1,false),false);
                h.getPlayer().sendMessage("You now have Detention (2x Melee Damage)");
                new BukkitRunnable() {
                    public void run() {
                        h.setDetention(false);
                        h.getPlayer().sendMessage("Your Detention wore off");
                    }
                }.runTaskLater(plugin,Config.getInt("attributes.hunter","detention_length"));
            }
        } else {
            ScoreboardUtil.set("&e" + (5 - ciphersDone) + " CIPHERS", 10);
        }
    }

    public int getCiphersDone() {
        return ciphersDone;
    }
}
