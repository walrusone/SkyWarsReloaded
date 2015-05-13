package com.walrusone.skywars;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.walrusone.skywars.commands.MainCommand;
import com.walrusone.skywars.controllers.ChestController;
import com.walrusone.skywars.controllers.GameController;
import com.walrusone.skywars.controllers.GlassController;
import com.walrusone.skywars.controllers.InventoryController;
import com.walrusone.skywars.controllers.KitController;
import com.walrusone.skywars.controllers.MapController;
import com.walrusone.skywars.controllers.PlayerController;
import com.walrusone.skywars.controllers.ScoreboardController;
import com.walrusone.skywars.controllers.ShopController;
import com.walrusone.skywars.controllers.WorldController;
import com.walrusone.skywars.dataStorage.DataStorage;
import com.walrusone.skywars.dataStorage.Database;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GameMap;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.listeners.IconMenuController;
import com.walrusone.skywars.listeners.PingListener;
import com.walrusone.skywars.listeners.PlayerListener;
import com.walrusone.skywars.listeners.SignListener;
import com.walrusone.skywars.listeners.SpectatorListener;
import com.walrusone.skywars.runnables.CheckForMinPlayers;
import com.walrusone.skywars.runnables.SavePlayers;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.LoggerFilter;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.SaveDefaultMaps;
 
public class SkyWarsReloaded extends JavaPlugin implements PluginMessageListener {

	private static SkyWarsReloaded instance;
    private GameController gc;
    private MapController mc;
    private WorldController wc;
    private DataStorage ds;
    private Database db;
    private InventoryController invc;
    private PlayerController pc;
    private ChestController cc;
    private KitController kc;
    private IconMenuController ic;
    private GlassController glc;
    private ShopController sc;
    private ScoreboardController score;
    private Messaging messaging;
    private boolean finishedStartup;
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    private boolean bungeeMode = false;
    private boolean signJoinMode = false;
    private MultiverseCore mv;
    
    public void onEnable() {
    	instance = this;
		
    	getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        
     	bungeeMode = getConfig().getBoolean("bungeeMode.enabled");
    	if (bungeeMode) {
    		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    		Bukkit.getPluginManager().registerEvents(new PingListener(), this);
    	}
   
       	mv = (MultiverseCore) this.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (mv == null) {
        		this.getLogger().info("Disabling SkyWarsReloaded: Multiverse-Core was not found");
        		onDisable();
        }

    	
        boolean sqlEnabled = getConfig().getBoolean("sqldatabase.enabled");
        if (sqlEnabled) {
        	getSWRDatabase();
        }
        
        boolean economy = this.getConfig().getBoolean("gameVariables.useExternalEconomy");
        if (economy) {
            if (!setupEconomy() ) {
                log.severe(String.format("[%s] - Disabling SkyWarsReloaded: No Economy Plugin Found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        
        setupPermissions();
        setupChat();

        boolean saveDefaultMaps = getConfig().getBoolean("resaveDefaultMaps");
        if (saveDefaultMaps) {
            SaveDefaultMaps.saveDefaultMaps();
            getConfig().set("resaveDefaultMaps", false);
            saveConfig();
        }
        
        if(getConfig().getBoolean("gameVariables.enableLogFilter")) {
            getServer().getLogger().setFilter(new LoggerFilter());
        }
        messaging = new Messaging(this);
        wc = new WorldController();
        mc = new MapController();
        gc = new GameController();
        ds = new DataStorage();
        pc = new PlayerController();
        invc = new InventoryController();
        cc = new ChestController();
        kc = new KitController();
        ic = new IconMenuController();
        sc = new ShopController();
        glc = new GlassController();
        score = new ScoreboardController();
        
        getCommand("swr").setExecutor(new MainCommand());
        
        getCommand("global").setExecutor(new CommandExecutor() {
        	
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            	boolean hasPerm = false;
        		if (!(sender instanceof Player)) {
        			hasPerm = true;
        		} else if (sender instanceof Player) {
        			Player player = (Player) sender;
        			if (SkyWarsReloaded.perms.has(player, "swr.global")) {
        				hasPerm = true;
        			} else {
            			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
        			} 
        		}
        		if (hasPerm) {
                    if (args.length == 0) {
                        sender.sendMessage("\247cUsage: /" + label + " <message>");
                        return true;
                    }

                    Player player = (Player) sender;
                    StringBuilder messageBuilder = new StringBuilder();
                    for (String arg : args) {
                        messageBuilder.append(arg);
                        messageBuilder.append(" ");
                    }
                    
                    GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
                	String name = player.getDisplayName();
                	String prefix = "";
                	if (SkyWarsReloaded.chat.getPlayerPrefix(gPlayer.getP()) != null) {
                    	prefix = SkyWarsReloaded.chat.getPlayerPrefix(gPlayer.getP());
                	}
                	String colorMessage = ChatColor.translateAlternateColorCodes('&', messageBuilder.toString());
                 	String message = "";
                	if (SkyWarsReloaded.perms.has(gPlayer.getP(), "swr.color")) {
                    	message = colorMessage;
                	} else {
                		message = ChatColor.stripColor(colorMessage);
                		while (message.contains("&")) {
                			message = ChatColor.translateAlternateColorCodes('&', message);
                    		message = ChatColor.stripColor(message);
                		}
                	}
                	int scoreValue = gPlayer.getScore();
                	String score;
                	if (scoreValue < 0) {
                        score = ChatColor.RED + "(" + gPlayer.getScore() + ")";
                	} else {
                		score = ChatColor.GREEN + "(+" + gPlayer.getScore() + ")";
                	}
            		Bukkit.broadcastMessage(new Messaging.MessageFormatter()
            			.setVariable("score", score)
            			.setVariable("prefix", prefix)
            			.setVariable("player", name)
            			.setVariable("message", message)
            			.format("globalchat"));

                    return true;
                }
				return true;
        		}
        });
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(ic, this);
        boolean allowSpectating = this.getConfig().getBoolean("gameVariables.allowSpectating");
        if (allowSpectating) {
        	Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        }
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckForMinPlayers(), 20L, 20L);
        
        int saveInterval = getConfig().getInt("sqldatabase.saveInterval");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SavePlayers(), 0, (1200 * saveInterval));
        
        if (bungeeMode) {
      		@SuppressWarnings("unused")
			Game game = gc.createGame();
        }
        
        signJoinMode = getConfig().getBoolean("signJoinMode");
        if (signJoinMode) {
        	gc.signJoinLoad();
        }
        finishedStartup = true;
    } 
    
    private void getSWRDatabase() {
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
    	Connection conn = db.getConnection();
    	DatabaseMetaData metadata;
		try {
			metadata = conn.getMetaData();
        	ResultSet resultSet;
        	resultSet = metadata.getTables(null, null, "swreloaded_player", null);
        	if(resultSet.next()) {
            	resultSet = metadata.getColumns(null, null, "swreloaded_player", "playername");
            	if(!resultSet.next()) {
            		try {
						db.addColumn("playername");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			metadata = conn.getMetaData();
        	ResultSet resultSet;
        	resultSet = metadata.getTables(null, null, "swreloaded_player", null);
        	if(resultSet.next()) {
            	resultSet = metadata.getColumns(null, null, "swreloaded_player", "balance");
            	if(!resultSet.next()) {
            		try {
						db.addColumn("balance");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			metadata = conn.getMetaData();
        	ResultSet resultSet;
        	resultSet = metadata.getTables(null, null, "swreloaded_player", null);
        	if(resultSet.next()) {
            	resultSet = metadata.getColumns(null, null, "swreloaded_player", "glasscolor");
            	if(!resultSet.next()) {
            		try {
						db.addColumn("glasscolor");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void onDisable() {
    	if (finishedStartup) {
           	gc.shutdown();
            pc.shutdown();
            invc.save();
        	deleteWorlds();
    	}
    }
    
    public void reload() {
    	finishedStartup = false;
		reloadConfig();
		saveConfig();
        gc.shutdown();
        invc.save();
        messaging = null;
		messaging = new Messaging(this);
		cc = null;
        cc = new ChestController();
        mc = null;
        mc = new MapController();
        kc = null;
        kc = new KitController();
        sc = null;
        sc = new ShopController();
        glc = null;
        glc = new GlassController();
        invc = null;
        invc = new InventoryController();
        db = null;
        boolean sqlEnabled = getConfig().getBoolean("sqldatabase.enabled");
        if (sqlEnabled) {
        	getSWRDatabase();
        }
        gc = null;
        gc = new GameController();
        if (bungeeMode) {
      		@SuppressWarnings("unused")
			Game game = gc.createGame();
        }
        
        signJoinMode = getConfig().getBoolean("signJoinMode");
        if (signJoinMode) {
        	gc.signJoinLoad();
        }
        finishedStartup = true;
    }
    
    private void deleteWorlds() {
    	for (GameMap map: mc.getRegisteredMaps()) {
    		mv.getMVWorldManager().deleteWorld(map.getName());
    	}
    }
    
    public static boolean deleteFolder(File file) {
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
    
    public boolean loadingEnded() {
    	return finishedStartup;
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
    
    public static Messaging getMessaging() {
        return instance.messaging;
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
 
    public static InventoryController getInvC() {
        return instance.invc;
    }
    
    public static GlassController getGLC() {
        return instance.glc;
    }
    
    public static MultiverseCore getMV() {
        return instance.mv;
    }
    
    public static ScoreboardController getScore() {
    	return instance.score;
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
    
    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
    }
    
    @Override
 	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
 		if (!channel.equals("BungeeCord")) {
 			return;
 		}
 		ByteArrayDataInput in = ByteStreams.newDataInput(message);
 		String subchannel = in.readUTF();
 		
 		if (subchannel.equals("SkyWarsReloadedRequest")) { 
 			short len = in.readShort();
 			byte[] msgbytes = new byte[len];
 			in.readFully(msgbytes);

				Game game = gc.getGame(1);
				if (game != null) {
						BungeeUtil.sendSignUpdateRequest(game);
				} else {
					System.out.println("Game " + game + " couldn't be found, please fix your setup.");
				}
 		}
 	}
}


