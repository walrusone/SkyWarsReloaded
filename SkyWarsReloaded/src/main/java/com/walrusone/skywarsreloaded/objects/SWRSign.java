package com.walrusone.skywarsreloaded.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class SWRSign {
	private String gameName;
	private Location location;
	
	public SWRSign(String name, Location loc) {
		this.gameName = name;
		this.location = loc;
	}
	
	public void update() {
		GameMap gMap = GameMap.getMap(this.gameName);
		Location loc = this.location;
		BlockState bs = loc.getBlock().getState();
		Sign sign = null;
		if (bs instanceof Sign) {
			sign = (Sign) bs;
		}
		Block b = sign.getBlock();
		org.bukkit.material.Sign meteSign = new org.bukkit.material.Sign();
		meteSign = (org.bukkit.material.Sign) b.getState().getData();
		Block attachedBlock = b.getRelative(meteSign.getAttachedFace());
		setMaterial(gMap, attachedBlock);
		String state = "";
		if (gMap == null) {
			state = new Messaging.MessageFormatter().format("signs.offline");
		} else if (gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
			state = new Messaging.MessageFormatter().format("signs.joinable");
		} else if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
			state =  new Messaging.MessageFormatter().format("signs.playing");
		} else if (gMap.getMatchState().equals(MatchState.ENDING)) {
			state =  new Messaging.MessageFormatter().format("signs.ending");;
		}
		if (sign != null) {
				sign.getBlock().getChunk().load();
				if (gMap != null) {
					sign.setLine(0, new Messaging.MessageFormatter().setVariable("matchstate", state).
							setVariable("mapname", gMap.getDisplayName().toUpperCase()).
							setVariable("playercount", "" + gMap.getPlayerCount()).
							setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line1"));
					sign.setLine(1, new Messaging.MessageFormatter().setVariable("matchstate", state).
							setVariable("mapname", gMap.getDisplayName().toUpperCase()).
							setVariable("playercount", "" + gMap.getPlayerCount()).
							setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line2"));
					sign.setLine(2, new Messaging.MessageFormatter().setVariable("matchstate", state).
							setVariable("mapname", gMap.getDisplayName().toUpperCase()).
							setVariable("playercount", "" + gMap.getPlayerCount()).
							setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line3"));
					sign.setLine(3, new Messaging.MessageFormatter().setVariable("matchstate", state).
							setVariable("mapname", gMap.getDisplayName().toUpperCase()).
							setVariable("playercount", "" + gMap.getPlayerCount()).
							setVariable("maxplayers", "" + gMap.getMaxPlayers()).format("signs.line4"));
					}
					sign.update();
		}
	}
	
	private static void setMaterial(GameMap gMap, Block attachedBlock) {
		attachedBlock.getWorld().loadChunk(attachedBlock.getChunk());
		if (gMap == null) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockoffline")));
		} else if (gMap.getMatchState().equals(MatchState.WAITINGSTART)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockwaiting")));
		} else if (gMap.getMatchState().equals(MatchState.PLAYING) || gMap.getMatchState().equals(MatchState.SUDDENDEATH)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockplaying")));
		} else if (gMap.getMatchState().equals(MatchState.ENDING)) {
			attachedBlock.setType(Material.valueOf(SkyWarsReloaded.getCfg().getMaterial("blockending")));
		}
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public String getName() {
		return this.gameName;
	}
}
