package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;


public class DeleteMapCmd extends BaseCmd {

	public DeleteMapCmd() {
		forcePlayer = false;
		cmdName = "delete";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Deletes the map named <mapname>";

	}

	@Override
	public boolean run() {
		String worldName = args[1].toLowerCase();
		for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
				for (Player player: editWorld.getPlayers()) {
					player.teleport(new Location(SkyWarsReloaded.get().getServer().getWorld("world"), -250, 64, -50));
				}
				editWorld.save();
				SkyWarsReloaded.getMV().getMVWorldManager().deleteWorld(worldName);
				File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
				File target = new File (dataDirectory, worldName);
				SkyWarsReloaded.getWC().deleteWorld(target);
				if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
					SkyWarsReloaded.getMC().removeMap(worldName);
				}
				if (SkyWarsReloaded.getMC().mapExists(worldName)) {
					SkyWarsReloaded.getMC().removeEditMap(worldName);
				}
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.deleted"));
				return true;
			}
		}
		File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
		File target = new File (dataDirectory, worldName);
		SkyWarsReloaded.getWC().deleteWorld(target);
		sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.deleted"));
		if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
			SkyWarsReloaded.getMC().removeMap(worldName);
		}
		if (SkyWarsReloaded.getMC().mapExists(worldName)) {
			SkyWarsReloaded.getMC().removeEditMap(worldName);
		}
		return true;
	}
}
