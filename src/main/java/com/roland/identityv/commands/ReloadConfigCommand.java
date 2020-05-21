package com.roland.identityv.commands;

import com.roland.identityv.core.IdentityV;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {

    private IdentityV plugin;

    public ReloadConfigCommand(IdentityV plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads configuration from file
     * @param commandSender
     * @param command
     * @param s
     * @param strings
     * @return
     */
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("creload")) {
            // Other configs too
            //plugin.getServer().broadcastMessage("Before: "+plugin.getConfig().getString("config_reload_msg"));

            plugin.reloadConfigs();
            plugin.getServer().broadcastMessage(plugin.getConfig().getString("config_reload_msg"));
        }
        return true;
    }
}
