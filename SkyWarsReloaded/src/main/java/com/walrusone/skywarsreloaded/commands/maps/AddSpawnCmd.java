package com.walrusone.skywarsreloaded.commands.maps;

import org.bukkit.Material;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

import net.md_5.bungee.api.ChatColor;


public class AddSpawnCmd extends BaseCmd { 

	public AddSpawnCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "spawn";
		alias = new String[]{"sp"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			final String type = args[1];
			GameMap gMap = GameMap.getMap(player.getLocation().getWorld().getName());
			if (gMap == null || !gMap.isEditing()) {
				player.sendMessage(new Messaging.MessageFormatter().format("error.map-not-editing"));
				return true;
			}
			if (type.equalsIgnoreCase("player") || type.equalsIgnoreCase("p")) {
				gMap.addPlayerCard(player.getLocation());
				player.getLocation().getBlock().setType(Material.DIAMOND_BLOCK);
				player.sendMessage(new Messaging.MessageFormatter().setVariable("num", "" + gMap.getMaxPlayers()).setVariable("mapname", gMap.getDisplayName()).format("maps.addSpawn"));
			} else if (type.equalsIgnoreCase("spec") || type.equalsIgnoreCase("s")) {
				gMap.setSpectateSpawn(player.getLocation());
				player.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", gMap.getDisplayName()).format("maps.specSpawn"));
			} else if (type.equalsIgnoreCase("death") || type.equalsIgnoreCase("d")) {
				
			} else {
				player.sendMessage(ChatColor.RED + "Type must be: " + "player OR spec");
			}
			return true;
		}
		return true; 
	}
}
