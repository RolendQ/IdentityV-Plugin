package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EditConfigCommand extends PlayerCommand {
    public EditConfigCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * Edits the configuration file
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("edit")) {
            if (args.length > 0 && args[0].contains(".")) {
                try {
                    ConfigurationSection cs = Config.getSection(args[0]);
                    p.sendMessage("Previous value: "+cs.getString(args[1]));
                    Config.set(cs, args[1], args[2]);
                    p.sendMessage("New value: "+cs.getString(args[1]));
                    plugin.saveConfig();
                } catch (Exception e) {

                }
                return true;
            }
            // TODO remove this eventually
            Config.set(args[1], args[2]);
            plugin.saveConfig();
        }
        return true;
    }
}