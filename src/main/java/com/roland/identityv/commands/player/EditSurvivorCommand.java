package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class EditSurvivorCommand extends PlayerCommand {

    public EditSurvivorCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Adds a survivor manually
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("survivor")) {
            if (args.length == 0) return true;

            if (args[0].equalsIgnoreCase("list")) {
                String survivors = "Survivors: ";
                for (Survivor surv : SurvivorManager.getSurvivors()) {
                    survivors += surv.getPlayer().getDisplayName() + " ";
                }
                p.sendMessage(survivors);
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                Player target = p.getServer().getPlayer(args[1]);
                if (SurvivorManager.isSurvivor(target)) return true;

                if (HunterManager.isHunter(target)) HunterManager.removeHunter(target);

                SurvivorManager.addSurvivor(target);
                p.sendMessage("Added: "+args[1]);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                SurvivorManager.removeSurvivor(p.getServer().getPlayer(args[1]));

                p.sendMessage("Removed: "+args[1]);
                return true;
            }
        }
        return true;
    }
}
