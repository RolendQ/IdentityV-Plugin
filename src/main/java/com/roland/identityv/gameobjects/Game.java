package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.CipherManager;
import com.roland.identityv.utils.ScoreboardUtil;

/**
 * Game object (keeps track of ciphers done)
 */
public class Game {
    public int ciphersDone;
    // Cipher manager? Or reference to ciphers
    // Reference to survivors?

    //public CipherManager cipherM;
    public IdentityV plugin;

    public Game(IdentityV plugin) {
        this.plugin = plugin;
        ScoreboardUtil.set("&e5 CIPHERS",10);
        //this.cipherM = cipherM;
    }


    public void incCiphersDone() {
        ciphersDone++;
        if (ciphersDone == 5) {
            ScoreboardUtil.set("&eEXIT GATES ARE ACTIVE", 10);
            CipherManager.addBlackGlass();
        } else {
            ScoreboardUtil.set("&e" + (5 - ciphersDone) + " CIPHERS", 10);
        }
    }

    public int getCiphersDone() {
        return ciphersDone;
    }
}
