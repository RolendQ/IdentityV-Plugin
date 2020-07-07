package com.roland.identityv.core;

import com.roland.identityv.commands.*;
import com.roland.identityv.commands.player.*;
import com.roland.identityv.gameobjects.Game;
import com.roland.identityv.listeners.entitylisteners.EntityDamageByEntityListener;
import com.roland.identityv.listeners.entitylisteners.EntityDamageListener;
import com.roland.identityv.listeners.entitylisteners.EntityDismountListener;
import com.roland.identityv.listeners.entitylisteners.EntityShootBowListener;
import com.roland.identityv.listeners.playerlisteners.*;
import com.roland.identityv.managers.gamecompmanagers.*;
import com.roland.identityv.managers.statusmanagers.CancelProtectionManager;
import com.roland.identityv.managers.statusmanagers.SwingManager;
import com.roland.identityv.managers.statusmanagers.VaultManager;
import com.roland.identityv.managers.statusmanagers.freeze.*;
import com.roland.identityv.utils.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class IdentityV extends JavaPlugin {

    public FileConfiguration config;
    public Game testGame;

    public File mapFile;
    public FileConfiguration mapConfig;

    /**
     * Sets up everything
     */
    @Override
    public void onEnable() {
        // Test game

        new Console(this); // for debugging

        // Make sure natural regen is off

        loadConfigFiles();
        setupManagers();
        registerCommands();
        registerListeners();

        new Config(this); // for code efficiency
        new Animations(this);
        new XPBar(this);
        new Holograms(this);
        new ScoreboardUtil(this);
        new NPCs(this);

        createTestGame(); // must be last
    }

    public void onDisable() {
        //
    }

    public void createTestGame() {
        testGame = new Game(this);
    }

    /**
     * Load configuration files
     */
    public void loadConfigFiles() {
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        mapFile = new File(this.getDataFolder().getPath(), "mapConfig.yml");

        reloadConfigs(); // All configs
    }

    public void reloadConfigs() {
        reloadConfig(); // Main config.yml

        mapConfig = YamlConfiguration.loadConfiguration(mapFile);
    }

    /**
     * Setup action/game component managers
     */
    public void setupManagers() {
        // Singletons
        new AttackRecoveryManager(this);
        new StunRecoveryManager(this);
        new DestroyPalletManager(this);
        new CipherManager(this);
        //new IncapacitationManager(this);
        new TrailManager(this);
        new RocketChairManager(this);
        new RedLightManager(this);
        new SurvivorManager(this);
        new BalloonPlayerManager(this);
        new ChairPlayerManager(this);
        new TimerManager(this);
        new SwingManager(this);
        new StruggleRecoveryManager(this);
        new HeartbeatManager(this);
        new GateManager(this);
        new CalibrationManager(this);
        new VaultManager(this);
        new HunterManager(this);
        new ItemManager(this);
        new DungeonManager(this);
        new CancelProtectionManager(this);
        new ChestManager(this);
    }

    /**
     * Register all the commands
     */
    public void registerCommands() {

        getCommand("creload").setExecutor(new ReloadConfigCommand(this));
        getCommand("setcipher").setExecutor(new SetCipherCommand(this));
        getCommand("sit").setExecutor(new SitCommand(this));
        getCommand("resete").setExecutor(new ResetEffectsCommand(this));
        getCommand("edit").setExecutor(new EditConfigCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("survivor").setExecutor(new EditSurvivorCommand(this));
        getCommand("animate").setExecutor(new AnimateCommand(this));
        getCommand("hologram").setExecutor(new HologramCommand(this));
        getCommand("xpbar").setExecutor(new XPCommand(this));
        getCommand("sb").setExecutor(new ScoreboardCommand(this));
        getCommand("calib").setExecutor(new CalibrationCommand(this));
        getCommand("debug").setExecutor(new DebugTestCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("hunter").setExecutor(new EditHunterCommand(this));
    }

    /**
     * Register all the event listeners
     */
    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new EntityDamageByEntityListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new PlayerSneakListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new EntityDamageListener(this), this);
        pm.registerEvents(new PlayerRespawnListener(this), this);
        pm.registerEvents(new PlayerInteractEntityListener(this), this);
        pm.registerEvents(new EntityDismountListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new EntityShootBowListener(this), this);
        pm.registerEvents(new PlayerItemHeldListener(this), this);
        pm.registerEvents(new PlayerPickupItemListener(this), this);
        pm.registerEvents(new PlayerDropItemListener(this), this);
    }

    public Game getGame() {
        // for now
        return testGame;
    }

    public FileConfiguration getMapConfig() {
        return mapConfig;
    }

    public void saveMapConfig() {
        try {
            mapConfig.save(mapFile);
        } catch (IOException e) {

        }
    }
}
