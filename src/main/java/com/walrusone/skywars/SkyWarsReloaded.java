package com.walrusone.skywars;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.mcstats.MetricsLite;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.pgcraft.spectatorplus.SpectateAPI;
import com.pgcraft.spectatorplus.SpectatorMode;
import com.pgcraft.spectatorplus.SpectatorPlus;
import com.walrusone.skywars.bungee.BungeeSocket;
import com.walrusone.skywars.commands.MainCommand;
import com.walrusone.skywars.controllers.ChestController;
import com.walrusone.skywars.controllers.GameController;
import com.walrusone.skywars.controllers.KitController;
import com.walrusone.skywars.controllers.MapController;
import com.walrusone.skywars.controllers.PlayerController;
import com.walrusone.skywars.controllers.ShopController;
import com.walrusone.skywars.controllers.WorldController;
import com.walrusone.skywars.dataStorage.DataStorage;
import com.walrusone.skywars.dataStorage.Database;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.listeners.IconMenuController;
import com.walrusone.skywars.listeners.PlayerListener;
import com.walrusone.skywars.listeners.SignListener;
import com.walrusone.skywars.runnables.CheckForMinPlayers;
import com.walrusone.skywars.runnables.SavePlayers;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.LoggerFilter;
import com.walrusone.skywars.utilities.SaveDefaultMaps;
 
public class SkyWarsReloaded extends JavaPlugin implements PluginMessageListener {

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
    private ShopController sc;
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    private SpectateAPI specAPI; 
    private SpectatorPlus sp;
    private boolean finishedEnable = false;
    private boolean bungeeMode = false;
    
    public void onEnable() {
    	instance = this;
		
    	getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        getLogger().info("Skywars Reloaded Enabled");  
        deleteIslandWorlds();
        
    	bungeeMode = getConfig().getBoolean("bungeeMode.enabled");
    	if (bungeeMode) {
    		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    	}
		
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
        boolean economy = this.getConfig().getBoolean("gameVariables.useEconomy");
        if (economy) {
            if (!setupEconomy() ) {
                log.severe(String.format("[%s] - Disabling SkyWarsReloaded: No Economy Plugin Found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        
        setupPermissions();

        boolean allowSpectating = this.getConfig().getBoolean("gameVariables.allowSpectating");
        if (allowSpectating) {
        	sp = (SpectatorPlus) this.getServer().getPluginManager().getPlugin("SpectatorPlus");
        	if (sp != null) {
        		specAPI = sp.getAPI();
        		specAPI.setSpectatorPlusMode(SpectatorMode.ARENA);
        		specAPI.setTeleportToSpawnOnSpecChangeWithoutLobby(false, false);
        		specAPI.setUseSpawnCommandToTeleport(false, false);
        		specAPI.setBlockCommands(false, false);
        		specAPI.setInspector(false, false);
        		specAPI.setSpectatorsTools(false, false);
        		specAPI.setArenaClock(false, false);
        		specAPI.setCompass(true, true);
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
        sc = new ShopController();
        new com.walrusone.skywars.utilities.Messaging(this);
        
        if (bungeeMode) {
        	int numGame = getConfig().getInt("gameVariables.maxNumberOfGames");
        	if (numGame > 25 || numGame == -1) {
        		numGame = 5;
        	}
        	for (int x = 0; x < numGame; x++) {
        		Game game = gc.createGame();
        		BungeeSocket.sendSignUpdate(game);
        	}
        }
        
        finishedEnable = true;
        
        getCommand("swr").setExecutor(new MainCommand());
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(ic, this);
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckForMinPlayers(), 20L, 20L);
        
        int saveInterval = getConfig().getInt("sqldatabase.saveInterval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SavePlayers(), 0, (1200 * saveInterval));
    }
        
    
    public void onDisable() {
    	if (finishedEnable) {
        	gc.shutdown();
        	pc.shutdown();
    	}
    	deleteIslandWorlds();
        getLogger().info("SkyWarsReloaded Disabled");
        saveConfig();

    }
    
    private void deleteIslandWorlds() {
        // Worlds
        File workingDirectory = new File(this.getServer().getWorldContainer().getAbsolutePath());
        File[] contents = workingDirectory.listFiles();

        if (contents != null) {
            for (File file : contents) {
                if (!file.isDirectory() || !file.getName().matches("island-\\d+")) {
                    continue;
                }
                String name = file.getName();
                this.getServer().unloadWorld(name, true);
                
                deleteFolder(file);
            }
        }

        // WorldGuard
        workingDirectory = new File(workingDirectory, "/plugins/WorldGuard/worlds/");
        contents = workingDirectory.listFiles();

        if (contents != null) {
            for (File file : contents) {
                if (!file.isDirectory() || !file.getName().matches("island-\\d+")) {
                    continue;
                }
                deleteFolder(file);
            }
        }
    }
    
    public static boolean deleteFolder(@Nonnull File file) {
        if (file.exists()) {
            boolean result = true;

            if (file.isDirectory()) {
                File[] contents = file.listFiles();

                if (contents != null) {
                    for (File f : contents) {
                        result = result && deleteFolder(f);
                    }
                }
            }

            return result && file.delete();
        }

        return false;
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
    
    public static ShopController getSC() {
        return instance.sc;
    }
    
    public static SpectateAPI getSpectate() {
        return instance.specAPI;
    }
    
    public static SpectatorPlus getSP() {
        return instance.sp;
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
    
    @Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		System.out.println(subchannel);
		if (subchannel.equals("SkyWarsReloadedPlayer")) {
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String playerData = msgin.readUTF();
				final String gameName = playerData.split(":")[0];
				final String uuid = playerData.split(":")[1];
				final UUID uid = UUID.fromString(uuid);
				final Game game = SkyWarsReloaded.getGC().getGameByName(gameName);
				if (game != null) {
					if (game.getState() != GameState.PREGAME && game.getState() != GameState.PLAYING && game.getState() != GameState.RESTARTING && !game.isFull()) {
						SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
							public void run() {
								Player p = Bukkit.getPlayer(uid);
								if (!game.containsPlayer(p)) {
									GamePlayer player = SkyWarsReloaded.getPC().getPlayer(p);
									game.addPlayer(player);
								}
							}
						}, 20);
					} else {
						Player p = Bukkit.getPlayer(uid);
						BungeeUtil.connectToServer(p, SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
					}
				} else {
					System.out.println("Game " + game + " couldn't be found, please fix your setup.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (subchannel.equals("SkyWarsReloadedRequest")) { // Lobby requests sign data
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String requestData = msgin.readUTF();
				final String gameName = requestData.split(":")[0];
				Game game = SkyWarsReloaded.getGC().getGameByName(gameName);
				if (game != null) {
						BungeeUtil.sendSignUpdateRequest(gameName, game);
				} else {
					System.out.println("Game " + game + " couldn't be found, please fix your setup.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}


