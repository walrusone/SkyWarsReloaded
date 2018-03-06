package com.walrusone.skywarsreloaded.objects;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.listeners.LobbyListener;
import com.walrusone.skywarsreloaded.objects.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class SWRServer {
    
	private static ArrayList<SWRServer> servers;
    private String serverName;
    private String displayName;
    private int playerCount;
    private int maxPlayers;
    private int port;
    private MatchState state;
	private ArrayList<Location> signs;
    
    public SWRServer(final String name, final int port) {
    	this.serverName = name;
    	signs = new ArrayList<Location>();
    	this.displayName = "Initializing";
    	this.playerCount = 0;
    	this.maxPlayers = 0;
    	this.port = port;
    	this.state = MatchState.OFFLINE;
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

	public static SWRServer getAvailableServer() {
		int highestPlayers = 0;
		SWRServer swrServer = null;
		for (SWRServer server: getServers()) {
			if (server.getMatchState().equals(MatchState.WAITINGSTART) && server.getPlayerCount() < server.getMaxPlayers()) {
				if (server.getPlayerCount() >= highestPlayers) {
					highestPlayers = server.getPlayerCount();
					swrServer = server;
				}
			}
		}
		return swrServer;
	}

	public void setMatchState(String gameStarted) {
		this.state = MatchState.valueOf(gameStarted);
	}
	
	public MatchState getMatchState() {
		return this.state;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}

	public int getPort() {
		return this.port;
	}

	public void setDisplayName(String string) {
		this.displayName = string;
		
	}

	public ArrayList<Location> getSigns() {
		return signs;
	}

	public void addSign(Location loc) {
		this.signs.add(loc);
		saveSigns();
	}
	
	public void clearSigns() {
		signs.clear();
	}
	
	public boolean hasGameSign(Location loc) {
		if (signs.contains(loc)) {
			return true;
		} 
		return false;
	}
	public void removeSign(Location loc) {
		signs.remove(loc);
		saveSigns();
	}
	
	public void saveSigns() {
		List<String> signLocs = new ArrayList<String>();
		for (Location loc: signs) {
				signLocs.add(Util.get().locationToString(loc));
		}
		SkyWarsReloaded.get().getConfig().set(this.getServerName(), signLocs);
	}
	
	public static void saveAllSigns() {
		for (SWRServer server: servers) {
			server.saveSigns();
		}
	}
	
	
	public static SWRServer getSign(Location loc) {
		for (SWRServer server: servers) {
			if (server.getSigns().contains(loc)) {
				return server;
			}
		}
		return null;
	}
	
	public void updateSigns() {
		LobbyListener.updateJoinMenu();
		for (Location loc: signs) {
			BlockState bs = loc.getBlock().getState();
			Sign sign = null;
			if (bs instanceof Sign) {
				sign = (Sign) bs;
			}
			Block b = sign.getBlock();
			org.bukkit.material.Sign meteSign = new org.bukkit.material.Sign();
			meteSign = (org.bukkit.material.Sign) b.getState().getData();
			Block attachedBlock = b.getRelative(meteSign.getAttachedFace());
			setMaterial(attachedBlock);
			String signState = "";
			if (state.equals(MatchState.OFFLINE)) {
				signState = new Messaging.MessageFormatter().format("signs.offline");
			} else if (state.equals(MatchState.WAITINGSTART)) {
				signState = new Messaging.MessageFormatter().format("signs.joinable");
			} else if (state.equals(MatchState.PLAYING) || state.equals(MatchState.SUDDENDEATH)) {
				signState = new Messaging.MessageFormatter().format("signs.playing");
			} else if (state.equals(MatchState.ENDING)) {
				signState = new Messaging.MessageFormatter().format("signs.ending");
			}
			if (sign != null) {
				sign.getBlock().getChunk().load();
					sign.setLine(0, new Messaging.MessageFormatter().setVariable("matchstate", signState).
							setVariable("mapname", displayName.toUpperCase()).
							setVariable("playercount", "" + playerCount).
							setVariable("maxplayers", "" + maxPlayers).format("signs.line1"));
					sign.setLine(1, new Messaging.MessageFormatter().setVariable("matchstate", signState).
							setVariable("mapname", displayName.toUpperCase()).
							setVariable("playercount", "" + playerCount).
							setVariable("maxplayers", "" + maxPlayers).format("signs.line2"));
					sign.setLine(2, new Messaging.MessageFormatter().setVariable("matchstate", signState).
							setVariable("mapname", displayName.toUpperCase()).
							setVariable("playercount", "" + playerCount).
							setVariable("maxplayers", "" + maxPlayers).format("signs.line3"));
					sign.setLine(3, new Messaging.MessageFormatter().setVariable("matchstate", signState).
							setVariable("mapname", displayName.toUpperCase()).
							setVariable("playercount", "" + playerCount).
							setVariable("maxplayers", "" + maxPlayers).format("signs.line4"));
					sign.update();
			}
		}
	}
	
	private void setMaterial(Block attachedBlock) {
		if (state.equals(MatchState.OFFLINE)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockoffline")));
		} else if (state.equals(MatchState.WAITINGSTART)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockwaiting")));
		} else if (state.equals(MatchState.PLAYING) || state.equals(MatchState.SUDDENDEATH)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockplaying")));
		} else if (state.equals(MatchState.ENDING)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockending")));
		}
	}

	public void setMatchState(MatchState serverState) {
		state = serverState;
	}

	public static SWRServer getServerByDisplayName(String stripColor) {
		for (SWRServer server: servers) {
			if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', server.getDisplayName())).equalsIgnoreCase(stripColor)) {
				return server;
			}
		}
		return null;
	}


}
