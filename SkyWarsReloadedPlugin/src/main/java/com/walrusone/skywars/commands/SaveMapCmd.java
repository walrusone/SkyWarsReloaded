package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class SaveMapCmd extends BaseCmd { 
	
	public SaveMapCmd() {
		forcePlayer = false;
		cmdName = "save";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Saves a map named <mapname>";

	}

	@Override
	public boolean run() {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn == null) {
			sender.sendMessage(ChatColor.RED + "YOU MUST SET SPAWN BEFORE YOU CAN SAVE A MAP");
			return true;
		}
		String worldName = args[1].toLowerCase();
		for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
				for (Player player: editWorld.getPlayers()) {
					player.teleport(spawn, TeleportCause.PLUGIN);
				}
				editWorld.save();
				SkyWarsReloaded.getWC().unloadWorld(worldName);
				File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
				File target = new File (dataDirectory, worldName);
				SkyWarsReloaded.getWC().deleteWorld(target);
				File source = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
				SkyWarsReloaded.getWC().copyWorld(source, target);
				SkyWarsReloaded.getWC().deleteWorld(source);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.saved"));
				if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
					sender.sendMessage(new Messaging.MessageFormatter().format("maps.register-reminder"));
				}
				return true;
			}
		}
		sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("error.map-not-in-edit"));
		return true;
	}

}
