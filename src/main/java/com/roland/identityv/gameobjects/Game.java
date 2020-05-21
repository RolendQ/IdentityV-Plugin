package com.roland.identityv.gameobjects;

import com.roland.identityv.core.IdentityV;
import com.roland.identityv.managers.gamecompmanagers.CipherManager;

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
        //this.cipherM = cipherM;
    }


    public void incCiphersDone() {
        ciphersDone++;
    }

    public int getCiphersDone() {
        return ciphersDone;
    }
}
