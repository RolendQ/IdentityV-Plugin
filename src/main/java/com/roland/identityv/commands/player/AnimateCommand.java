package com.roland.identityv.commands.player;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.utils.Animations;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class AnimateCommand extends PlayerCommand {
    public AnimateCommand(IdentityV plugin) {
        super(plugin);
    }

    /**
     * [TEST]
     * Generates particles from config under a given pattern
     * @param p
     * @param command
     * @param s
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player p, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("animate")) {
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("one")) {
                    if (args.length >= 4) Animations.one(p.getEyeLocation(),args[1],args[2],Integer.parseInt(args[3]));
                    else Animations.one(p.getEyeLocation(),args[1],args[2]);
                } else if (args[0].equalsIgnoreCase("cube")) {
                    Animations.cube(p.getEyeLocation(),args[1],args[2],Integer.parseInt(args[3]));
                } else if (args[0].equalsIgnoreCase("helix")) {
                    Animations.helix(p.getEyeLocation(),args[1],args[2],Integer.parseInt(args[3]));
                } else if (args[0].equalsIgnoreCase("sphere")) {
                    Animations.sphere(p.getEyeLocation(),args[1],args[2],Double.parseDouble(args[3]));
                } else if (args[0].equalsIgnoreCase("ring")) {
                    Animations.ring(p.getEyeLocation(),args[1],args[2],Double.parseDouble(args[3]));
                } else if (args[0].equalsIgnoreCase("random")) {
                    Animations.random(p.getEyeLocation(),args[1],args[2],Double.parseDouble(args[3]),Integer.parseInt(args[4]));
                } else if (args[0].equalsIgnoreCase("multiple")) {
                    Animations.multiple(p.getEyeLocation(),args[1],args[2],Integer.parseInt(args[3]));
                } else if (args[0].equalsIgnoreCase("decreasing_ring")) {
                    Animations.decreasing_ring(p.getEyeLocation(),args[1],args[2],Double.parseDouble(args[3]),Double.parseDouble(args[4]));
                } else if (args[0].equalsIgnoreCase("falling_rings")) {
                    Animations.falling_rings(p.getEyeLocation(),args[1],args[2],Double.parseDouble(args[3]));
                }
            }
        }
        return true;
    }
}
