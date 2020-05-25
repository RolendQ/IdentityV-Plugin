package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.enums.Action;
import com.roland.identityv.handlers.FreezeHandler;
import com.roland.identityv.managers.gamecompmanagers.CalibrationManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CalibrationCommand extends PlayerCommand {
    public CalibrationCommand(IdentityV plugin) {
        super(plugin);
    }

    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("calib")) {
            CalibrationManager.give(p, Action.DECODE);
        }
        return true;
    }
}
