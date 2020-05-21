package com.roland.identityv.commands.player;

import com.roland.identityv.commands.player.PlayerCommand;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.FreezeHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class FreezeCommand extends PlayerCommand {
    public FreezeCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Freezes and unfreezes a player
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (args.length == 1) {
                p.sendMessage("Freezing " + args[0]);
                FreezeHandler.freeze(p.getServer().getPlayer(args[0]));
                return true;
            } else { // TODO change this eventually
                p.sendMessage("Unfreezing " + args[0]);
                FreezeHandler.unfreeze(p.getServer().getPlayer(args[0]));
                return true;
            }
        }
        return true;
    }
}
