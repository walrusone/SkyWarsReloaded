package com.walrusone.skywarsreloaded;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.walrusone.skywarsreloaded.api.NMS;
import com.walrusone.skywarsreloaded.commands.CmdManager;
import com.walrusone.skywarsreloaded.config.Config;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.database.Database;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.listeners.IconMenuController;
import com.walrusone.skywarsreloaded.listeners.LobbyListener;
import com.walrusone.skywarsreloaded.listeners.PlayerJoinListener;
import com.walrusone.skywarsreloaded.listeners.PlayerQuitListener;
import com.walrusone.skywarsreloaded.listeners.PlayerTeleportListener;
import com.walrusone.skywarsreloaded.listeners.SignListener;
import com.walrusone.skywarsreloaded.managers.LevelManager;
import com.walrusone.skywarsreloaded.minecraftping.MinecraftPing;
import com.walrusone.skywarsreloaded.minecraftping.MinecraftPingOptions;
import com.walrusone.skywarsreloaded.minecraftping.MinecraftPingReply;
import com.walrusone.skywarsreloaded.managers.ItemsManager;
import com.walrusone.skywarsreloaded.objects.Leaderboard;
import com.walrusone.skywarsreloaded.objects.PlayerStat;
import com.walrusone.skywarsreloaded.objects.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class SkyWarsReloaded extends JavaPlugin implements PluginMessageListener {
	private static SkyWarsReloaded instance;
	private Messaging messaging;
	private static ArrayList<String> useable = new ArrayList<String>();
	private Leaderboard leaderboard;
	private IconMenuController ic;
	private ItemsManager im;
	private Config config;
	private static Database db;
    private LevelManager lm;
	private String servername;
	private NMS nmsHandler;
	
	public void onEnable() {
    	instance = this;
    	servername = "none";

    	this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    	this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    	String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            final Class<?> clazz = Class.forName("com.walrusone.skywarsreloaded.nms." + version + ".NMSHandler");
            // Check if we have a NMSHandler class at that location.
            if (NMS.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.nmsHandler = (NMS) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception e) {
            e.printStackTrace();
            this.getLogger().severe("Could not find support for this CraftBukkit version.");
            this.getLogger().info("Check for updates at URL HERE");
            this.setEnabled(false);
            return;
        }
        this.getLogger().info("Loading support for " + version);
        
    	if (nmsHandler.getVersion() < 9) {
    		File config = new File(SkyWarsReloaded.get().getDataFolder(), "config.yml");
            if (!config.exists()) {
            	SkyWarsReloaded.get().saveResource("config18.yml", false);
            	config = new File(SkyWarsReloaded.get().getDataFolder(), "config18.yml");
            	if (config.exists()) {
            		config.renameTo(new File(SkyWarsReloaded.get().getDataFolder(), "config.yml"));
            	}
            } 
    	}
    	
    	getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        load();
        
        this.getServer().getPluginManager().registerEvents(ic, this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new SignListener(), this);
        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);
        getCommand("skywars").setExecutor(new CmdManager());
        
        new BukkitRunnable() {
            public void run() {
            	if (servername.equalsIgnoreCase("none")) {
                	Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                	if (player != null) {
                    	sendBungeeMsg(player, "GetServer", "none");
                	}
            	} else {
            		this.cancel();
            	}
            }
        }.runTaskTimer(this, 0, 20);
	}

	public void updateServers() {
		new BukkitRunnable() {
			public void run() {
				for (SWRServer server: SWRServer.getServers()) {
					Player player = Iterables.getFirst(SkyWarsReloaded.get().getServer().getOnlinePlayers(), null);
						if (player != null) {
       					sendBungeeMsg(player, "PlayerCount", server.getServerName());
    				}
				}
			}
		}.runTask(this);
	}
	
	public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
		messaging = null;
		config = null;
        ic.destroyAll();
		ic = null;
		im = null;
		lm = null;
		
        for (final PlayerStat fData : PlayerStat.getPlayers()) {
        	DataStorage.get().saveStats(fData);
        }   
	}
	
	public void load() {
		messaging = new Messaging(this);
		reloadConfig();
        config = new Config();
        ic = new IconMenuController();
        im = new ItemsManager();
        lm = new LevelManager();
        
        boolean sqlEnabled = getConfig().getBoolean("sqldatabase.enabled");
        if (sqlEnabled) {
        	getFWDatabase();
        }
        
        useable.clear();
        if (SkyWarsReloaded.getCfg().eloEnabled()) {
			useable.add("ELO");
		} 
        if (SkyWarsReloaded.getCfg().winsEnabled()) {
			useable.add("WINS");
		}
        if (SkyWarsReloaded.getCfg().lossesEnabled()) {
			useable.add("LOSSES");
		}
        if (SkyWarsReloaded.getCfg().killsEnabled()) {
			useable.add("KILLS");
		}
        if (SkyWarsReloaded.getCfg().deathsEnabled()) {
			useable.add("DEATHS");
		}
        if (SkyWarsReloaded.getCfg().xpEnabled()) {
			useable.add("XP");
		}
        
        prepareServers();
        
        for (SWRServer server: SWRServer.getServers()) {
        	server.clearSigns();
			List<String> signLocs = SkyWarsReloaded.get().getConfig().getStringList(server.getServerName());
			if (signLocs != null) {
				for (String sign: signLocs) {
					Location loc = Util.get().stringToLocation(sign);
					server.addSign(loc);
				}
			}
		}

        new BukkitRunnable() {
            public void run() {
                for (final Player v : getServer().getOnlinePlayers()) {
                    if (PlayerStat.getPlayerStats(v.getUniqueId().toString()) == null) {
                        PlayerStat.getPlayers().add(new PlayerStat(v));
                    }
                }
                leaderboard = new Leaderboard();
            }
        }.runTaskAsynchronously(this);
	}
	
	public static SkyWarsReloaded get() {
        return instance;
    }
	
	public static Messaging getMessaging() {
	     return instance.messaging;
	}
	
	public static Leaderboard getLB() {
	     return instance.leaderboard;
	}
	
    public static Config getCfg() {
    	return instance.config;
    }
    
    public static IconMenuController getIC() {
    	return instance.ic;
    }
    
    public static Database getDb() {
    	return db;
    }
          
    private void getFWDatabase() {
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
	
	public static ItemsManager getIM() {
		return instance.im;
	}
	
	public static LevelManager getLM() {
		return instance.lm;
	}
	
	//**Bungeecord Methods **//
	
	private void prepareServers() {
		SWRServer.getServers().clear();
		for (String server: getCfg().getGameServers()) {
			final String[] serverParts = server.split(":");
            if (serverParts.length != 2) {
            } else {
            	SWRServer.getServers().add(new SWRServer(serverParts[0], Integer.valueOf(serverParts[1])));
            	new BukkitRunnable() {
        			@Override
        			public void run() {
        				SWRServer swrServer = SWRServer.getServer(serverParts[0]);
        				if (swrServer != null) {
        					Player player = Iterables.getFirst(SkyWarsReloaded.get().getServer().getOnlinePlayers(), null);
       						if (player != null) {
               					sendBungeeMsg(player, "PlayerCount", serverParts[0]);
            				} else {
            					try {
									MinecraftPingReply data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("127.0.0.1").setPort(swrServer.getPort()));
									final String[] serverInfo = data.getDescription().getText().split(":");
									if (serverInfo != null) {
										swrServer.setMatchState(serverInfo[0]);
										if (Util.get().isInteger(serverInfo[1])) {
											swrServer.setPlayerCount(Integer.valueOf(serverInfo[1]));
										}
										if (Util.get().isInteger(serverInfo[2])) {
											swrServer.setMaxPlayers(Integer.valueOf(serverInfo[2]));
										}
										swrServer.setDisplayName(serverInfo[3]);
										swrServer.updateSigns();
										
									}
            					} catch (IOException e) {
            						swrServer.setMatchState(MatchState.OFFLINE);
            						swrServer.updateSigns();
								}
            				}
        				}
        			}
        		}.runTaskTimer(this, 20, 200);
            }
		}
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
	    }
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    
	    if (subchannel.equals("GetServer")) {
	    	servername = in.readUTF();
	    }
	    
 		if (subchannel.equals("PlayerCount")) {
 			String server = in.readUTF(); 
 			int playercount = in.readInt();
 			
 			final SWRServer swrServer = SWRServer.getServer(server);
 			
 			if (swrServer != null) {
 	 			if (playercount == 0) {
 	 				new BukkitRunnable() {
						@Override
						public void run() {
							try {
								MinecraftPingReply data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("127.0.0.1").setPort(swrServer.getPort()));
								final String[] serverInfo = data.getDescription().getText().split(":");
								if (serverInfo != null) {
									swrServer.setMatchState(serverInfo[0]);
									if (Util.get().isInteger(serverInfo[1])) {
										swrServer.setPlayerCount(Integer.valueOf(serverInfo[1]));
									}
									if (Util.get().isInteger(serverInfo[2])) {
										swrServer.setMaxPlayers(Integer.valueOf(serverInfo[2]));
									}
									swrServer.setDisplayName(serverInfo[3]);
									swrServer.updateSigns();
									
								}
        					} catch (IOException e) {
								swrServer.setMatchState(MatchState.OFFLINE);
								swrServer.updateSigns();
							}
						}
 	 				}.runTask(this);
 	 			} else {
 					if (player != null) {
 						ArrayList<String> messages = new ArrayList<String>();
 						messages.add("RequestUpdate");
 						messages.add(servername);
 		 				sendSWRMessage(player, server, messages);
 					}
 	 			}
 			}
    	}
    
	    if (subchannel.equals("SWRMessaging")) {
	    	short len = in.readShort();
	    	byte[] msgbytes = new byte[len];
	    	in.readFully(msgbytes);

	    	DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
	    	try {
				String header = msgin.readUTF();
				if (header.equalsIgnoreCase("ServerUpdate")) {
					String server = msgin.readUTF();
					String playerCount = msgin.readUTF();
					String maxPlayers = msgin.readUTF();
					String gameStarted = msgin.readUTF();
					if (server != null) {
						SWRServer swrServer = SWRServer.getServer(server);
						if (swrServer != null) {
							if (playerCount != null && Util.get().isInteger(playerCount)) {
								swrServer.setPlayerCount(Integer.valueOf(playerCount));
							}
							if (maxPlayers != null && Util.get().isInteger(maxPlayers)) {
								swrServer.setMaxPlayers(Integer.valueOf(maxPlayers));
							}
							if (gameStarted != null) {
								swrServer.setMatchState(gameStarted);
							}
							swrServer.updateSigns();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
	    }
	  }

	public void sendBungeeMsg(Player player, String subchannel, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(subchannel);
		if (!message.equalsIgnoreCase("none")) {
			out.writeUTF(message);
		}
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
	
	public void sendSWRMessage(Player player, String server, ArrayList<String> messages) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward"); 
		out.writeUTF(server);
		out.writeUTF("SWRMessaging");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			for (String msg: messages) {
				msgout.writeUTF(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}

	public static NMS getNMS() {
		return instance.nmsHandler;
	}
	
	public PlayerStat getPlayerStat(Player player) {
		return PlayerStat.getPlayerStats(player);
	}
	
    public static ArrayList<String> getUseable() {
    	return useable;
    }
    
}
