package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.command.ConsoleCommandSender;

public class Console {
    public static void log(String s) {
       IdentityV.plugin.getServer().getConsoleSender().sendMessage(s);
    }
}
