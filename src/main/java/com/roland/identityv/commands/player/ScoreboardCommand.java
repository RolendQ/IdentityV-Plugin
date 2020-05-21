package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Holograms;
import com.roland.identityv.utils.ScoreboardUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ScoreboardCommand extends PlayerCommand {
    public ScoreboardCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Adjust scoreboard display manually
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("sb")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("remove")) {
                    //
                    return true;
                }

                if (args[0].equalsIgnoreCase("bar")) {
                    String bar = ScoreboardUtil.createBar((float) Double.parseDouble(args[1]),"3");
                    p.sendMessage(bar);
                    ScoreboardUtil.set(bar, 5);
                    return true;
                }

                String str = "";
                for (int i = 1; i < args.length; i++) {
                    str += " " + args[i];
                }
                str = str.substring(1);
                ScoreboardUtil.set(str,Integer.parseInt(args[0]));
            }
        }
        return true;
    }
}
