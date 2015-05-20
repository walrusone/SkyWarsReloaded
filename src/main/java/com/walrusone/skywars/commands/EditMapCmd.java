package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;


public class EditMapCmd extends BaseCmd { 

	public EditMapCmd() {
		forcePlayer = true;
		cmdName = "edit";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Edits the map named <mapname>";

	}

	@Override
	public boolean run() {
		final String worldName = args[1].toLowerCase();
		if (SkyWarsReloaded.getMC().mapExists(worldName)) {
			boolean alreadyLoaded = false;
			final Player player = (Player) sender;
			for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
				if (world.getName().equalsIgnoreCase(worldName)) {
					alreadyLoaded = true;
					if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
						SkyWarsReloaded.getMC().removeMap(worldName);
					}
					World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
					player.teleport(new Location(editWorld, 0, 21, 0));
				}
			} 
			if (!alreadyLoaded) {
				File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
				File source = new File (dataDirectory, worldName);
				File target = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
				SkyWarsReloaded.getWC().copyWorld(source, target);
				for (MultiverseWorld mvworld: SkyWarsReloaded.getMV().getMVWorldManager().getMVWorlds()) {
					if (mvworld.getName().equalsIgnoreCase(worldName)) {
						SkyWarsReloaded.getMV().getMVWorldManager().deleteWorld(worldName);
					}
				}
				SkyWarsReloaded.getMV().getMVWorldManager().addWorld(worldName, Environment.NORMAL, null, null, null, "VoidWorld", false);
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
						player.teleport(new Location(editWorld, 0, 21, 0));
					}
				}, 20);
			}
			return true;
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
			return true;
		}
	}
}
