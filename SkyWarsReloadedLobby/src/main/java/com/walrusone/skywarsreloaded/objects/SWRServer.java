package com.walrusone.skywarsreloaded.objects;

import com.google.common.collect.Iterables;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.SWRServer;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SWRServer {
    
	private static ArrayList<SWRServer> servers;
    private String serverName;
    private String displayName;
    private int playerCount;
    private int maxPlayers;
    private int port;
    private String state;
    private boolean initialized;
	private boolean online;
    
    public SWRServer(final String name, final String displayName, final int maxPlayers, final int port) {
    	this.serverName = name;
    	this.displayName = displayName;
    	this.playerCount = 0;
    	this.maxPlayers = maxPlayers;
    	this.port = port;
    	this.state = "ENDING";
    	this.online = true;
    	this.initialized = false;
    	attemptInitialization();
    }

	private void attemptInitialization() {
    	new BukkitRunnable() {
			@Override
			public void run() {
				Player player = Iterables.getFirst(SkyWarsReloaded.get().getServer().getOnlinePlayers(), null);
				if (player != null) {
					if (!initialized) {
						SkyWarsReloaded.get().sendBungeeMsg(player, "PlayerCount", serverName);
					}
				} else {
					attemptInitialization();
				}
			}
    	}.runTaskLater(SkyWarsReloaded.get(), 20);
	}

	public static ArrayList<SWRServer> getServers() {
        return SWRServer.servers;
    }
    
    public static void setPlayers(final ArrayList<SWRServer> serverData) {
        SWRServer.servers = serverData;
    }
    
    public static SWRServer getServer(final String name) {
        for (final SWRServer server : getServers()) {
            if (server.getServerName().equals(name)) {
                return server;
            }
        }
        return null;
    }
        
    static {
        SWRServer.servers = new ArrayList<SWRServer>();
    }

	public String getServerName() {
		return serverName;
	}
	
	public int getPlayerCount() {
		return this.playerCount;
	}
	
	public void setPlayerCount(int count) {
		this.playerCount = count;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public void setMaxPlayers(int max) {
		this.maxPlayers = max;
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void initialize() {
		this.initialized = true;
	}

	public static SWRServer getAvailableServer() {
		int highestPlayers = 0;
		SWRServer swrServer = null;
		for (SWRServer server: getServers()) {
			if (server.isInitialized() && server.isOnline() && server.getState().equalsIgnoreCase("WAITINGSTART") && server.getPlayerCount() < server.getMaxPlayers()) {
				if (server.getPlayerCount() >= highestPlayers) {
					highestPlayers = server.getPlayerCount();
					swrServer = server;
				}
			}
		}
		return swrServer;
	}

	public void setState(String gameStarted) {
		this.state = gameStarted;
	}
	
	public String getState() {
		return this.state;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}

	public void setOnline(boolean b) {
		this.online = b;
	}
	
	public boolean isOnline() {
		return this.online;
	}

	public int getPort() {
		return this.port;
	}
}
