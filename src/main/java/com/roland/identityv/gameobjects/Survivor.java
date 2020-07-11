package com.roland.identityv.gameobjects;

import com.roland.identityv.actions.animated.StruggleFree;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.Persona;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.items.Controller;
import com.roland.identityv.gameobjects.items.ElbowPad;
import com.roland.identityv.gameobjects.items.Football;
import com.roland.identityv.gameobjects.items.Item;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.managers.statusmanagers.CancelProtectionManager;
import com.roland.identityv.managers.statusmanagers.freeze.FreezeActionManager;
import com.roland.identityv.utils.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Survivor object (handles all events and actions involving a survivor)
 */
public class Survivor {

    public Game game;

    public Player player;
    public Hunter hunter; // for balloon
    public int state; // enum
    public int action; // enum
    public int struggleProgress;
    public IdentityV plugin = IdentityV.plugin;
    public int timesOnChair; // 0 is start. 1 is before half. 2 is after half (dead on chair)
    public int bleedOutTimer;
    public int chairTimer;
    public int healingProgress;
    public int rescuingProgress;
    public double cipherProgressAtStart;
    public Survivor owner;
    public Location cloneLoc;
    public Survivor healer;

    public int[] personaWeb; // tracks if they have the persona and the cooldown

    public long lastHeartbeat;
    public int selfHeal;
    public int crowsTimer;

    public Chest chest;
    public boolean wasJustHit;
    public int tideDuration;
    public boolean hitUnderTide = false;

    public Survivor target; // for heal and rescue
    public Item item;
    // Scoreboard
    public int line;

    public BukkitRunnable actionRunnable;

    public ItemStack[] armor;

    // Maybe add character here

    public Survivor(Player player) {
        this(player, null); // Won't be able to open gates
    }

    public Survivor(Survivor owner, Location cloneLoc, Game game) {
        // Bot
        this.game = game;
        this.player = null; // Make sure stuff checks for this

        this.owner = owner;
        this.cloneLoc = cloneLoc;

        personaWeb = new int[]{0, 0, 0, 0}; // empty
    }

    public Survivor(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.target = null;
        this.healer = null;

        actionRunnable = null;
        state = State.NORMAL;
        action = Action.NONE;
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(4);
        player.setHealthScale(4);
        player.setFoodLevel(2);
        player.setSaturation(1000);
        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
        item = null;
        player.setCanPickupItems(true);

        personaWeb = new int[]{1, 1, 30, 30}; // default persona web

        lastHeartbeat = 0;
        struggleProgress = 0;
        bleedOutTimer = 0; // config to set limit
        chairTimer = 0;
        crowsTimer = 0;
        selfHeal = 1;

        line = (SurvivorManager.getSurvivors().size()*2) + 2; // Line for their name
//        ScoreboardUtil.set("&a"+player.getDisplayName(), getNameLine());
//        ScoreboardUtil.setBar(1,"a",getBarLine());

        armor = player.getInventory().getArmorContents();
    }

    public void hit(Hunter h, final int damage) {
        if (state != State.NORMAL) return; // chair

        h.incPresence(damage);

        healingProgress = 0; // Reset healing progress

        // Cancel certain items like football && elbow pad
        // Note: Syringe is a terror shock
        if (item != null) {
            if (item.getMat() == Material.LEATHER_HELMET && item.task != null) {
                ((Football) item).task.cancel();
                item.task = null;
                //setItem(null); // ??
            } else if (item.getMat() == Material.IRON_CHESTPLATE && item.task != null) {
                ((ElbowPad) item).task.cancel();
                item.task = null;
                //setItem(null);
            }
        }

        // Check for under tide
        if (tideDuration > 0) {
            h.getPlayer().sendTitle(ChatColor.RED + "Last Effort!", "");
            player.sendTitle(ChatColor.RED + "Last Effort!", "");
            hitUnderTide = true;
            // Delay damage
            new BukkitRunnable() {
                public void run() {
                    if (state == State.NORMAL) {
                        player.sendMessage("Receiving one damage delayed by tide turner");
                        if (damage >= player.getHealth()) { // If they will die
                            player.damage(0.001); // for animation?
                            incapacitate();
                            return;
                        }

                        player.damage(damage);
                    }
                }
            }.runTaskLater(plugin, tideDuration * 20);
        } else {
            // Incapacitate instead of vanilla dying
            if (damage >= player.getHealth()) { // If they will die
                player.damage(0.001); // for animation?
                incapacitate();
                return;
            }

            player.damage(damage);
        }

        // If frozen, unfreeze
        if (FreezeActionManager.getInstance().isFrozen(player)) {
            FreezeActionManager.getInstance().remove(player);
            player.setLevel(0);
        }

        // Brief invulnerability
        wasJustHit = true;
        new BukkitRunnable() {
            public void run() {
                wasJustHit = false;
            }
        }.runTaskLater(plugin, 10);

        // Speed boost
        increaseSpeed(0.7,50);
        //player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Config.getInt("attributes.survivor","hit_boost_length"), 1, true, false),true);
    }

    public void incapacitate() {
//        ScoreboardUtil.set("&e"+player.getDisplayName(), getNameLine());
//        ScoreboardUtil.setBar((float)bleedOutTimer / (float) Config.getInt("timers.survivor","bleed"),"e",getBarLine());

        Animations.decreasing_ring(player.getLocation(),"animations.survivor","incap",2,40);
        //player.removePotionEffect(PotionEffectType.SPEED); // remove any speed boost
        plugin.getServer().broadcastMessage(player.getDisplayName() + " was incapacitated!");
        player.playSound(player.getLocation(),Sound.ENDERMAN_DEATH,0.5F,1);

        //IncapacitationManager.getInstance().add(player, 200); // 10 seconds for now
        healingProgress = 0;
        struggleProgress = 0;
        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","incap"));
        player.setHealth(1);
        state = State.INCAP;

        player.getWorld().playEffect(player.getLocation(), Effect.CRIT, 1, 10);
    }

    public void death() {
        if (state != State.DEAD) {
            state = State.DEAD;
            SitHandler.fakeUnsit(player);
            player.setGameMode(GameMode.SPECTATOR);
            Animations.one(player.getLocation(),"animations.survivor","death",12);
            plugin.getServer().broadcastMessage(player.getDisplayName() + " died!");
            SurvivorManager.checkIfOver();
        }
    }

    public void escape() {
        state = State.ESCAPE;
        player.setGameMode(GameMode.SPECTATOR);
        //Animations.one(player.getLocation(),"animations.survivor","escape",12);
        plugin.getServer().broadcastMessage(player.getDisplayName() + " escaped!");
        SurvivorManager.checkIfOver();
    }

    public void drop() {
        plugin.getServer().broadcastMessage(player.getDisplayName() + " was dropped");

        setState(State.INCAP);
        player.setWalkSpeed((float) Config.getDouble("attributes.survivor","incap"));
        hunter = null;
        struggleProgress += 6;
        player.setExp(0);
        SitHandler.unsit(player);
    }

    public void useTide(final Survivor rescued) {
        Console.log("Using tide");
        personaWeb[Persona.TIDE_TURNER] = 0;
        tideDuration = Config.getInt("attributes.survivor","tide_length") / 20;
        rescued.setTideDuration(tideDuration); // Manage rescued's tide duration too
        new BukkitRunnable() {
            public void run() {
                tideDuration--;
                rescued.setTideDuration(tideDuration);
                if (tideDuration == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin,0,20);
    }

    public boolean clearActionRunnable() {
        return clearActionRunnable(false);
    }

    public boolean clearActionRunnable(boolean isBotSwitch) {
        if (player != null)  player.setExp(0);

        if (action == Action.OPENCHEST) {
            if (!chest.isEmpty()) {
                // Player tried to cancel
                chest.animateOpenAndClose();
                FreezeActionManager.getInstance().add(player, Config.getInt("timers.survivor", "open_and_close_chest_duration"));
            }
            chest.setOpener(null);
            chest = null; // TODO needs testing with attacking
        }

        action = Action.NONE;
        if (actionRunnable != null) {
            if (!isBotSwitch && target != null) { // So switching from bot doesn't stop heal
                target.clearActionRunnable(true);
                target = null;
            }
            actionRunnable.cancel();
            actionRunnable = null;
            return true;
        }
        // Clear from decoding any ciphers
        CipherManager.removeDecodingSurvivor(this);
        GateManager.removeOpeningSurvivor(this);
        return false;
    }

    // Other

    // Returns if this survivor is actually a robot (can't be healed or open chests)
    public boolean isControllingRobot() {
        if (item != null && item instanceof Controller) {
            Controller cont = (Controller) item;
            if (cont.isRobot) return true;
        }
        return false;
    }

    public void increaseSpeed(double percentage, int duration) {
        Console.log("Increasing speed by "+percentage);
        final double amount = Config.getDouble("attributes.survivor","walk") * percentage;
        player.setWalkSpeed((float) (player.getWalkSpeed() + amount));
        new BukkitRunnable() {
            public void run() {
                player.setWalkSpeed((float) (player.getWalkSpeed() - amount));
            }
        }.runTaskLater(plugin,duration);
    }

    public void boostsCD(final int boostType) {
        personaWeb[boostType] = 0;
        new BukkitRunnable() {
            public void run() {
                personaWeb[boostType]++;
                if (personaWeb[boostType] == Config.getInt("attributes.survivor","boost_cd") / 20) {
                    //player.sendMessage("Your boost is now available!");
                    player.playSound(player.getLocation(),Sound.NOTE_PLING,1,1);
                    cancel();
                }
            }
        }.runTaskTimer(plugin,0,20);
    }

    // Getters and setters

    public Survivor getHealer() { return healer; }

    public void setHealer(Survivor healer) { this.healer = healer; }

    public ItemStack[] getArmor() {
        return armor;
    }

    // Only for bots
    public Survivor getOwner() {
        return owner;
    }

    public int getNameLine() {
        return line;
    }

    public int getBarLine() {
        return line-1;
    }

    public int[] getPersonaWeb() {
        return personaWeb;
    }

    public Player getPlayer() {
        return player;
    }

    public void setStruggleProgress(int struggleProgress) {
        this.struggleProgress = struggleProgress;
    }

    public Hunter getHunter() { return hunter; }

    public void setHunter(Hunter hunter) { this.hunter = hunter; }

    public void incCrowsTimer() {
        crowsTimer++;
    }

    public int getCrowsTimer() {
        return crowsTimer;
    }

    public void clearCrowsTimer() {
        crowsTimer = 0;
    }

    public boolean isVisibleToAHunter() {
        for (Hunter h : HunterManager.getHunters()) {
            if (h.getPlayer().hasLineOfSight(player)) return true;
        }
        return false;
    }

    public void incTimesOnChair() {
        timesOnChair += 1;
    }

    public int getTimesOnChair() {
        return timesOnChair;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAction() { return action; }

    public void setAction(int action) {
        clearActionRunnable();
        this.action = action;
        // Cancel Protection
        CancelProtectionManager.getInstance().add(player,10);
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public void incBleedOutTimer() {
        bleedOutTimer += 1;
        Animations.random(player.getLocation(),"animations.survivor","bleed",1.5,3);
    }

    public int getBleedOutTimer() { return bleedOutTimer;}

    public int getChairTimer() { return chairTimer;}

    public void incChairTimer() {
        chairTimer += 1;
    }

    public void setChairTimer(int chairTimer) {
        this.chairTimer = chairTimer;
    }

    public int getStruggleProgress() {
        return struggleProgress;
    }

    public int getHealingProgress() {
        return healingProgress;
    }

    public void incHealingProgress(int amount) {
        incHealingProgress(amount, true);
    }

    public void incHealingProgress(int amount, boolean animate) {
        healingProgress += amount;
        if (animate) Animations.random(player.getLocation(),"animations.survivor","heal",1.5,3);
    }

    public int getRescuingProgress() { return rescuingProgress; }

    public void incRescuingProgress(int amount) {
        rescuingProgress += amount;
        Animations.ring(player.getLocation(),"animations.survivor","rescue",2);
    }

    public void incStruggleProgress(int amount) {
        struggleProgress += amount;
    }

    public int getHealth() {
        return (int) player.getHealth();
    }

    public Location getLocation() {
        return cloneLoc;
    }

    public void setRescuingProgress(int rescuingProgress) {
        this.rescuingProgress = rescuingProgress;
    }

    public void setHealingProgress(int healingProgress) {
        this.healingProgress = healingProgress;
    }

    public BukkitRunnable getActionRunnable() {
        return actionRunnable;
    }

    public void setActionRunnable(BukkitRunnable actionRunnable) {
        this.actionRunnable = actionRunnable;
    }

    public int getSelfHeal() {
        return selfHeal;
    }

    public boolean wasJustHit() {
        return wasJustHit;
    }

    public Survivor getTarget() {
        return target;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCipherProgressAtStart(double cipherProgressAtStart) {
        this.cipherProgressAtStart = cipherProgressAtStart;
    }

    public double getCipherProgressAtStart() {
        return cipherProgressAtStart;
    }

    public int getTideDuration() {
        return tideDuration;
    }

    public void setTideDuration(int tideDuration) {
        this.tideDuration = tideDuration;
    }

    public boolean wasHitUnderTide() {
        return hitUnderTide;
    }
}

