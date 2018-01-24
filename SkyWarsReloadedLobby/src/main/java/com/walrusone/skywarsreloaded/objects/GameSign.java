package com.walrusone.skywarsreloaded.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.utilities.Util;

public class GameSign {

	private static HashMap<Location, SWRServer> gameSigns;
	
    static {
        GameSign.gameSigns = new HashMap<Location, SWRServer>();
    }
	
	public static void addSign(Location loc, SWRServer server) {
		gameSigns.put(loc, server);
		saveSigns();
	}
	
	public static void clearSigns() {
		gameSigns.clear();
	}
	
	public static boolean hasGameSign(Location loc) {
		if (gameSigns.containsKey(loc)) {
			return true;
		} 
		return false;
	}
	public static void removeSign(Location loc) {
		gameSigns.remove(loc);
		saveSigns();
	}

	public static void saveSigns() {
		for (SWRServer server: SWRServer.getServers()) {
			List<String> signLocs = new ArrayList<String>();
			for (Location loc: gameSigns.keySet()) {
				if (gameSigns.get(loc) == server) {
					signLocs.add(Util.get().locationToString(loc));
				}
			}
			SkyWarsReloaded.get().getConfig().set(server.getServerName(), signLocs);
		}
		SkyWarsReloaded.get().saveConfig();
	}
	
	public static void updateSigns() {
		for (Location loc: gameSigns.keySet()) {
			SWRServer server = gameSigns.get(loc);
			BlockState bs = loc.getBlock().getState();
			Sign sign = null;
			if (bs instanceof Sign) {
				sign = (Sign) bs;
			}
			Block b = sign.getBlock();
			org.bukkit.material.Sign meteSign = new org.bukkit.material.Sign();
			meteSign = (org.bukkit.material.Sign) b.getState().getData();
			Block attachedBlock = b.getRelative(meteSign.getAttachedFace());
			setMaterial(server, attachedBlock);
			String state = "";
			if (!server.isOnline()) {
				state = "OFFLINE";
			} else if (server.getState().equalsIgnoreCase("WAITINGSTART")) {
				state = "JOINABLE";
			} else if (server.getState().equalsIgnoreCase("PLAYING") || server.getState().equalsIgnoreCase("SUDDENDEATH")) {
				state = "IN PROGRESS";
			} else if (server.getState().equalsIgnoreCase("ENDING")) {
				state = "RESTARTING";
			}
			if (sign != null) {
				sign.getBlock().getChunk().load();
				sign.setLine(0, ChatColor.GREEN + "SkyWars");
				sign.setLine(1, ChatColor.UNDERLINE + "" + ChatColor.DARK_RED + server.getDisplayName().toUpperCase());
				sign.setLine(2, ChatColor.BLUE + state);
				sign.setLine(3, ChatColor.BLUE + "" + server.getPlayerCount() + " of " + server.getMaxPlayers());
				sign.update();
			}
		}
	}
	
	private static void setMaterial(SWRServer server, Block attachedBlock) {
		if (!server.isOnline()) {
			attachedBlock.setType(Material.COAL_BLOCK);
		} else if (server.getState().equalsIgnoreCase("WAITINGSTART")) {
			attachedBlock.setType(Material.EMERALD_BLOCK);
		} else if (server.getState().equalsIgnoreCase("PLAYING") || server.getState().equalsIgnoreCase("SUDDENDEATH")) {
			attachedBlock.setType(Material.REDSTONE_BLOCK);
		} else if (server.getState().equalsIgnoreCase("ENDING")) {
			attachedBlock.setType(Material.LAPIS_BLOCK);
		}
	}

	public static SWRServer getSign(Location loc) {
		return gameSigns.get(loc);
	}
	
	
}
