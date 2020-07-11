package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Adjusting the scoreboard to display information about the game
 */
public class ScoreboardUtil {
    public static ScoreboardManager sbm;
    public static Scoreboard sb;
    public static Objective ob;
    public static Team hiddenNames;

    public static void setup() {
        sbm = IdentityV.plugin.getServer().getScoreboardManager();
        sb = sbm.getNewScoreboard();
        ob = sb.registerNewObjective("Display","");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);
        ob.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Identity V");

        // Default setup
        //set("&e5 CIPHERS",10);
        set("",9);
        // 4,3,2,1


        // For hidden name tags
        hiddenNames = sb.registerNewTeam("hiddenNames");
        hiddenNames.setNameTagVisibility(NameTagVisibility.NEVER);
    }

    public static void reset() {
        for (String entry : sb.getEntries()) {
            sb.resetScores(entry);
        }
        set("",9);
    }

    public static void clear(int line) {
        for (String entry : sb.getEntries()) {
            if (ob.getScore(entry).getScore() == line) {
                //Console.log("reset entry");
                sb.resetScores(entry);
            }
        }
    }

    public static void set(String s, int line) {
        clear(line);

        s = ChatColor.translateAlternateColorCodes('&',s);
        Score sc = ob.getScore(s);
        if (sc.getScore() != 0) { // so no duplicate entries
            //Console.log("Handling duplicate entry");
            sc = ob.getScore(ChatColor.translateAlternateColorCodes('&',s + "&" + line));
        }
        sc.setScore(line);

        for (Player p : IdentityV.plugin.getServer().getOnlinePlayers()) {
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

    public static void addHiddenName(String name) {
//        for (Player p : IdentityV.plugin.getServer().getOnlinePlayers()) {
//            if (!hiddenNames.hasEntry(p.getDisplayName())) {
//                hiddenNames.addEntry(p.getDisplayName());
//            }
//        }
        //Console.log("Adding to hiddenNames: "+name);
        hiddenNames.addEntry(name);
    }
}
