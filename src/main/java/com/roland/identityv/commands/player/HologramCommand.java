package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.State;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import com.roland.identityv.utils.Config;
import com.roland.identityv.utils.Console;
import com.roland.identityv.utils.Holograms;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class HologramCommand extends PlayerCommand {
    public HologramCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Generates a hologram using armor stands
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("hologram")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Holograms.delete(p.getLocation());
                    return true;
                }

                if (args[0].equalsIgnoreCase("box")) {
                    Holograms.box(p,p.getLocation());
                    return true;
                }

                if (args[0].equalsIgnoreCase("alert")) {
                    Holograms.alert(p,p.getLocation());
                    return true;
                }

                String str = "";
                for (String arg : args) {
                    str += " " + arg;
                }
                str = str.substring(1);
                Holograms.create(p,p.getLocation().subtract(0,0.5,0),str);
            }
        }
        return true;
    }
}