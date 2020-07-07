package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.gameobjects.Hunter;
import com.roland.identityv.gameobjects.Survivor;
import com.roland.identityv.managers.gamecompmanagers.HunterManager;
import com.roland.identityv.managers.gamecompmanagers.SurvivorManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class EditHunterCommand extends PlayerCommand {

    public EditHunterCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Adds a hunter manually
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("hunter")) {
            if (args.length == 0) return true;

            if (args[0].equalsIgnoreCase("list")) {
                String hunters = "Hunters: ";
                for (Hunter h : HunterManager.getHunters()) {
                    hunters += h.getPlayer().getDisplayName() + " ";
                }
                p.sendMessage(hunters);
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                Player target = null;
                try {
                    target = p.getServer().getPlayer(args[1]);
                } catch (Exception e) {
                    return true;
                }
                if (HunterManager.isHunter(target)) return true;

                if (SurvivorManager.isSurvivor(target)) SurvivorManager.removeSurvivor(target);

                HunterManager.addHunter(target);

                p.sendMessage("Added: "+args[1]);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                HunterManager.removeHunter(p.getServer().getPlayer(args[1]));

                p.sendMessage("Removed: "+args[1]);
                return true;
            }
        }
        return true;
    }
}
