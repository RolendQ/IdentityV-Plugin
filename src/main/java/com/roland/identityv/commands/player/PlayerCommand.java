package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract class for commands only a player can use
 */
public abstract class PlayerCommand implements CommandExecutor {

    public IdentityV plugin;

    public PlayerCommand(IdentityV plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player!");
            return false;
        }

        Player p = (Player) commandSender;
        return onPlayerCommand(p, command, s, args);
    }

    public boolean onPlayerCommand(Player player, Command command, String s, String[] args) {
        return false;
    }
}
