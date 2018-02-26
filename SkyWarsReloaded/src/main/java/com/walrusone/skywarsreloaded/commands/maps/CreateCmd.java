package com.walrusone.skywarsreloaded.commands.maps;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class CreateCmd extends BaseCmd {

	public CreateCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "create";
		alias = new String[]{"c"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			String worldName = args[1];
			GameMap gMap = GameMap.getMap(worldName);
			if (gMap != null) {
				player.sendMessage(new Messaging.MessageFormatter().format("error.map-exists"));
				return true;
			}
				World result = GameMap.createNewMap(worldName);
				if (result == null) {
					player.sendMessage(new Messaging.MessageFormatter().format("error.map-world-exists"));
					return true;
				} else {
					player.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.created"));
					gMap = GameMap.getMap(worldName);
					gMap.setEditing(true);
					player.setGameMode(GameMode.CREATIVE);
					player.teleport(new Location(result, 0, 76, 0), TeleportCause.PLUGIN);
					player.setAllowFlight(true);
					player.setFlying(true);
					return true;
				}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.nospawn"));
			return false;
		}
	}
}
