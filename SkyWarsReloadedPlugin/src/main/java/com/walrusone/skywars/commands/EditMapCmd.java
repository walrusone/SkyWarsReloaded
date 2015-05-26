package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

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
					player.teleport(new Location(editWorld, 0, 21, 0), TeleportCause.PLUGIN);
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
					SkyWarsReloaded.getWC().deleteWorld(worldName);
				}
				SkyWarsReloaded.getWC().copyWorld(source, target);
				boolean loaded = SkyWarsReloaded.getWC().loadWorld(worldName);
				if (loaded) {
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						public void run() {
							World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
							player.teleport(new Location(editWorld, 0, 21, 0), TeleportCause.PLUGIN);
						}
					}, 20);
				} else {
					player.sendMessage(ChatColor.RED + "WORLD FAILED TO LOADED");
				}
			}
			return true;
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
			return true;
		}
	}
}
