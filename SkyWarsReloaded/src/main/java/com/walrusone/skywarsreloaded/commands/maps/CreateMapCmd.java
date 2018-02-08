package com.walrusone.skywarsreloaded.commands.maps;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class CreateMapCmd extends BaseCmd {

	public CreateMapCmd() {
		forcePlayer = true;
		cmdName = "create";
		alias = new String[]{};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			String worldName = args[1].toLowerCase();
			if (!GameMap.mapExists(worldName.toLowerCase())) {
				World newWorld = SkyWarsReloaded.getWM().createEmptyWorld(worldName);
				if (newWorld == null) {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.map-world-exists"));
					return true;
				}
				GameMap.addEditMap(worldName);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.created"));
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.setGameMode(GameMode.CREATIVE);
					player.teleport(new Location(newWorld, 0, 76, 0), TeleportCause.PLUGIN);
					player.setAllowFlight(true);
					player.setFlying(true);
				}
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-exists"));
				return true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.nospawn"));
			return false;
		}

	}
}
