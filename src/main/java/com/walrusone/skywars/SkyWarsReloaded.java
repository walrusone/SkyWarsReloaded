package com.walrusone.skywars;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.pgcraft.spectatorplus.SpectateAPI;
import com.pgcraft.spectatorplus.SpectatorMode;
import com.pgcraft.spectatorplus.SpectatorPlus;
import com.walrusone.skywars.commands.MainCommand;
import com.walrusone.skywars.controllers.ChestController;
import com.walrusone.skywars.controllers.GameController;
import com.walrusone.skywars.controllers.KitController;
import com.walrusone.skywars.controllers.MapController;
import com.walrusone.skywars.controllers.PlayerController;
import com.walrusone.skywars.controllers.WorldController;
import com.walrusone.skywars.dataStorage.DataStorage;
import com.walrusone.skywars.dataStorage.Database;
import com.walrusone.skywars.listeners.IconMenuController;
import com.walrusone.skywars.listeners.PlayerListener;
import com.walrusone.skywars.listeners.SignListener;
import com.walrusone.skywars.runnables.CheckForMinPlayers;
import com.walrusone.skywars.runnables.SavePlayers;
import com.walrusone.skywars.utilities.LoggerFilter;
import com.walrusone.skywars.utilities.SaveDefaultMaps;
 
public class SkyWarsReloaded extends JavaPlugin {

    private static SkyWarsReloaded instance;
    private GameController gc;
    private MapController mc;
    private WorldController wc;
    private DataStorage ds;
    private Database db;
    private PlayerController pc;
    private ChestController cc;
    private KitController kc;
    private IconMenuController ic;
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    private SpectateAPI specAPI; 
    
    public void onEnable() {
    	instance = this;
    	getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        getLogger().info("Skywars Reloaded Enabled");        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        boolean sqlEnabled = getConfig().getBoolean("sqldatabase.enabled");
        if (sqlEnabled) {
        	try {
				db = new Database();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
        	try {
				db.createTables();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        
        if (!setupEconomy() ) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
        }
        setupPermissions();

        boolean allowSpectating = this.getConfig().getBoolean("gameVariables.allowSpectating");
        if (allowSpectating) {
        	SpectatorPlus sp = (SpectatorPlus) this.getServer().getPluginManager().getPlugin("SpectatorPlus");
        	if (sp != null) {
        		specAPI = sp.getAPI();
        		specAPI.setSpectatorPlusMode(SpectatorMode.WORLD);
        		specAPI.setTeleportToSpawnOnSpecChangeWithoutLobby(false, false);
        		specAPI.setUseSpawnCommandToTeleport(false, false);
        		specAPI.setBlockCommands(false, false);
        		specAPI.setInspector(false, false);
        		specAPI.setSpectatorsTools(false, false);
        	} else {
        		this.getLogger().info("Disabling SkyWarsReloaded: SpectatorPlus was not found");
        		onDisable();
        	}
        }
        
        boolean saveDefaultMaps = getConfig().getBoolean("resaveDefaultMaps");
        if (saveDefaultMaps) {
            SaveDefaultMaps.saveDefaultMaps();
            getConfig().set("resaveDefaultMaps", false);
            saveConfig();
        }
        
        getServer().getLogger().setFilter(new LoggerFilter());
        wc = new WorldController();
        mc = new MapController();
        gc = new GameController();
        ds = new DataStorage();
        pc = new PlayerController();
        cc = new ChestController();
        kc = new KitController();
        ic = new IconMenuController();
                   
        getCommand("swr").setExecutor(new MainCommand());
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(ic, this);
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckForMinPlayers(), 20L, 20L);
        
        int saveInterval = getConfig().getInt("sqldatabase.saveInterval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SavePlayers(), 0, (1200 * saveInterval));
    }
        
    
    public void onDisable() {
    	gc.shutdown();
    	pc.shutdown();
        getLogger().info("SkyWarsReloaded Disabled");
        saveConfig();

    }
    
    public static SkyWarsReloaded get() {
        return instance;
    }
    
    public static GameController getGC() {
        return instance.gc;
    }
    
    public static WorldController getWC() {
        return instance.wc;
    }
    
    public static MapController getMC() {
        return instance.mc;
    }
    
    public static DataStorage getDS() {
        return instance.ds;
    }
    
    public static Database getDB() {
        return instance.db;
    }
    
    public static PlayerController getPC() {
        return instance.pc;
    }
    
    public static ChestController getCC() {
        return instance.cc;
    }
    
    public static KitController getKC() {
        return instance.kc;
    }
    
    public static IconMenuController getIC() {
        return instance.ic;
    }
    
    public static SpectateAPI getSpectate() {
        return instance.specAPI;
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

}


