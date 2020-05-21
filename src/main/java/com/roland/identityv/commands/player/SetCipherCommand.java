package com.roland.identityv.commands.player;

import com.roland.identityv.commands.player.PlayerCommand;
import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.CipherManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetCipherCommand extends PlayerCommand {
    public SetCipherCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * Currently useless
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        // Not needed TODO remove
        if (command.getName().equalsIgnoreCase("setcipher")) {
            //Location loc = p.getEyeLocation();
            Location loc = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
            if (p.getWorld().getBlockAt(loc).getType() == Material.JUKEBOX) {

                CipherManager.add(loc);

                p.sendMessage(ChatColor.GREEN + "Set a cipher at: " + loc.getX() + ", "
                + loc.getY() + ", " + loc.getZ());
            }
            return true;
        }
        return false;
    }
}
