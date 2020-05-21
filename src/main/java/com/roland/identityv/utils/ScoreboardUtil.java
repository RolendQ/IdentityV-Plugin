package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Adjusting the scoreboard to display information about the game
 */
public class ScoreboardUtil {
    public static IdentityV plugin;
    public static ScoreboardManager sbm;
    public static Scoreboard sb;
    public static Objective ob;

    public ScoreboardUtil(IdentityV plugin) {
        ScoreboardUtil.plugin = plugin;
        sbm = plugin.getServer().getScoreboardManager();
        sb = sbm.getNewScoreboard();
        ob = sb.registerNewObjective("Display","");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);
        ob.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Identity V");

        // Default setup
        set("&e5 CIPHERS",10);
        set("",9);
        // 4,3,2,1
    }

    public static void set(String s, int line) {
        for (String entry : sb.getEntries()) {
            if (ob.getScore(entry).getScore() == line) {
                //Console.log("reset entry");
                sb.resetScores(entry);
            }
        }

        s = ChatColor.translateAlternateColorCodes('&',s);
        Score sc = ob.getScore(s);
        if (sc.getScore() != 0) { // so no duplicate entries
            Console.log("Handling duplicate entry");
            sc = ob.getScore(ChatColor.translateAlternateColorCodes('&',s + "&" + line));
        }
        sc.setScore(line);

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.setScoreboard(sb);
        }
    }

    public static String createBar(float percentage, String color) {
        // [||||||||||||||| |||||||||||||||] &c&l 30 bars

        // &e&l[|||| &c&l|||...

        String result = "&"+color+"&l[";
        int bars = Math.round(percentage * 28);
        for (int i = 0; i < 28; i++) {
            if (i == bars) {
                result += "&7&l";
            }
            result += "|";
        }
        result += "]";
        return result;
    }

    public static void setBar(float percentage, String color, int line) {
        set(createBar(percentage,color),line);
    }
}
