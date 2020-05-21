package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.command.ConsoleCommandSender;

public class Console {
    public static IdentityV plugin;

    public Console(IdentityV plugin) {
        this.plugin = plugin;
    }

    public static void log(String s) {
       plugin.getServer().getConsoleSender().sendMessage(s);
    }
}
