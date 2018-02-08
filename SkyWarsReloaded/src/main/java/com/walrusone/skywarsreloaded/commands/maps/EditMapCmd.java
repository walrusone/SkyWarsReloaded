package com.walrusone.skywarsreloaded.commands.maps;

import java.io.File;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;


public class EditMapCmd extends BaseCmd { 

	public EditMapCmd() {
		forcePlayer = true;
		cmdName = "edit";
		alias = new String[]{};
		argLength = 2; //counting cmdName

	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			final String worldName = args[1].toLowerCase();
			if (GameMap.mapExists(worldName)) {
				boolean alreadyLoaded = false;
				final Player player = (Player) sender;
				for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
					if (world.getName().equalsIgnoreCase(worldName)) {
						alreadyLoaded = true;
						if (GameMap.mapRegistered(worldName)) {
							GameMap.removeMap(worldName);
						}
						World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
						player.teleport(new Location(editWorld, 0, 95, 0), TeleportCause.PLUGIN);
						player.setGameMode(GameMode.CREATIVE);
						player.setAllowFlight(true);
						player.setFlying(true);
					}
				} 
				if (!alreadyLoaded) {
					File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
					File source = new File (dataDirectory, worldName);
					File target = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
					boolean mapExists = false;
					if(target.isDirectory()) {			 
						if(target.list().length > 0) {
				 			mapExists = true;
						}	 
					}
					if (mapExists) {
						SkyWarsReloaded.getWM().deleteWorld(worldName);
					}
					SkyWarsReloaded.getWM().copyWorld(source, target);
					boolean loaded = SkyWarsReloaded.getWM().loadWorld(worldName);
					if (loaded) {
						SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
							public void run() {
								World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
								player.teleport(new Location(editWorld, 0, 95, 0), TeleportCause.PLUGIN);
								player.setGameMode(GameMode.CREATIVE);
								player.setAllowFlight(true);
								player.setFlying(true);
							}
						}, 20);
					} else {
						player.sendMessage(new Messaging.MessageFormatter().format("error.map-fail-load"));
					}
				}
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.nospawn"));
			return false;
		}
	}
}
