package com.roland.identityv.commands.player;

import com.roland.identityv.commands.player.PlayerCommand;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.handlers.SitHandler;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SitCommand extends PlayerCommand {
    public SitCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Sit and unsit on an arrow entity
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("sit")) {
            if (p.getVehicle() != null) {
                SitHandler.unsit(p);
                return true;
            }
            SitHandler.sit(p, p.getLocation());
        }
        return true;
    }
}