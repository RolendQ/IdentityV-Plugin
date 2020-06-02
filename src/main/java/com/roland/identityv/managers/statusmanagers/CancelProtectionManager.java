package com.roland.identityv.managers.statusmanagers;

import com.roland.identityv.core.IdentityV;

public class CancelProtectionManager extends ActionManager {

    public CancelProtectionManager(IdentityV plugin) {
        super(plugin);
        instance = this;
    }

    public static CancelProtectionManager instance;

    public static CancelProtectionManager getInstance() {
        return instance;
    }
}
