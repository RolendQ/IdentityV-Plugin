package com.roland.identityv.listeners.playerlisteners;

import com.roland.identityv.actions.ChairPlayer;
import com.roland.identityv.actions.DropPallet;
import com.roland.identityv.actions.Vault;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.enums.State;
import com.roland.identityv.gameobjects.Cipher;
import com.roland.identityv.gameobjects.Gate;
import com.roland.identityv.gameobjects.RocketChair;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.handlers.SitHandler;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.managers.statusmanagers.VaultManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerSneakListener implements Listener {

    public IdentityV plugin;
    public static BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public PlayerSneakListener(IdentityV plugin) {this.plugin = plugin;}

    /**
     * Detects when a player sneaks or unsneaks
     * @param e
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        if (!p.isSneaking()) return;

        // Survivor
        if (SurvivorManager.isSurvivor(p)) {
            Survivor s = SurvivorManager.getSurvivor(p);

            // Check for Dungeon first
            if (p.getLocation().getBlock().getType() == Material.DAYLIGHT_DETECTOR) {
                s.escape();
            }

            // NORMAL State
            if (s.getState() == State.NORMAL) {
                // Look for adjacent cipher or clay to vault
                for (int i = 0; i < PlayerSneakListener.faces.length; i++) {
                    Location loc = p.getLocation().getBlock().getRelative(PlayerSneakListener.faces[i]).getLocation();

                    // DROP PALLET
                    Block upper = loc.getBlock().getRelative(BlockFace.UP);
                    if (upper.getType() == Material.WALL_BANNER) {
                        Banner banner = (Banner) upper.getState();
                        if (banner.getBaseColor() == DyeColor.GREEN) {
                            org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                            new DropPallet(plugin, SurvivorManager.getSurvivor(p), upper.getRelative(BlockFace.DOWN), bannerData.getAttachedFace().getOppositeFace());
                            return;
                        }
                    }

                    // VAULT
                    if (loc.getBlock().getType() == Material.STAINED_CLAY && loc.getBlock().getData() != DyeColor.RED.getData()) {
                        // Check if there is anyone else vaulting or the window is blocked
                        if (!VaultManager.getInstance().hasNearbyVaults(p) && loc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                            new Vault(plugin, p, loc, i, Config.getInt("attributes.survivor", "vault"), loc.getBlock().getData());
                            return;
                        }
                    }
                }

                // Look for nearby chaired player
                for (Entity entity : p.getLocation().getWorld().getNearbyEntities(p.getLocation(),2,2,2)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Survivor s2 = SurvivorManager.getSurvivor((Player) entity);
                        if (s2 != null && s2.getAction() != Action.GETRESCUE && s2.getState() == State.CHAIR) {
                            s.startRescue(s2);
                            return;
                        }
                    }
                }
            }
            // INCAP
            else if (s.getState() == State.INCAP) {
                if (s.getAction() != Action.SELFHEAL) { // Self heal
                    s.startSelfHeal();
                }
            }
        } else if (HunterManager.isHunter(p)) {
            // Hunter
            if (p.getPassenger() == null) {
                // Vault
                for (int i = 0; i < PlayerSneakListener.faces.length; i++) {
                    Location loc = p.getLocation().getBlock().getRelative(PlayerSneakListener.faces[i]).getLocation();
                    if (loc.getBlock().getType() == Material.STAINED_CLAY && loc.getBlock().getData() != DyeColor.RED.getData()) {
                        // Check if there is anyone else vaulting or the window is blocked
                        if (!VaultManager.getInstance().hasNearbyVaults(p) && loc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                            new Vault(plugin, p, loc, i, Config.getInt("attributes.hunter", "vault"), loc.getBlock().getData());
                        }
                    }
                }
            } else {
                // BALLOON
                Survivor s = SurvivorManager.getSurvivor((Player) p.getPassenger());

                Location loc = p.getLocation().getBlock().getLocation();
                // CHAIR
                if (loc.getBlock().getType() == Material.STEP) {
                    RocketChair chair = RocketChairManager.getChair(loc);
                    if (!chair.isUsed() && !chair.isOccupied()) { // Makes sure the chair is empty/not used
                        new ChairPlayer(plugin, HunterManager.getHunter(p), s, chair);
                    }
                    return;
                }

                // DROP
                s.drop();
            }
        }
    }
}
