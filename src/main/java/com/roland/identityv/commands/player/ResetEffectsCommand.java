package com.roland.identityv.commands.player;

import com.roland.identityv.commands.player.PlayerCommand;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Random;

public class ResetEffectsCommand extends PlayerCommand {
    public ResetEffectsCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Resets effects of a survivor
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("resete")) {
            //p.sendMessage(String.valueOf(p.getWorld().getTime()));
            p.setWalkSpeed((float) Config.getDouble("attributes.survivor","walk"));
            p.setMaxHealth(4);
            p.setHealth(4);
            p.setFoodLevel(2);
            p.setSaturation(1000);
            if (SurvivorManager.isSurvivor(p)) {
                SurvivorManager.getSurvivor(p).setState(State.NORMAL);
            }
        }
        return true;
    }
}