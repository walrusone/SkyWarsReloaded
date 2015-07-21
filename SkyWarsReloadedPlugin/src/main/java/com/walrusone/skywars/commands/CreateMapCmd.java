package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;


public class CreateMapCmd extends BaseCmd {

	public CreateMapCmd() {
		forcePlayer = false;
		cmdName = "create";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = "::  Creates a map named <mapname>";

	}

	@Override
	public boolean run() {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn == null) {
			sender.sendMessage(ChatColor.RED + "YOU MUST SET SPAWN BEFORE YOU CAN CREATE A MAP");
			return true;
		}
		String worldName = args[1].toLowerCase();
		if (!SkyWarsReloaded.getMC().mapExists(worldName.toLowerCase())) {
			World newWorld = SkyWarsReloaded.getWC().createEmptyWorld(worldName);
			if (newWorld == null) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map.world.exists"));
				return true;
			}
			SkyWarsReloaded.getMC().addEditMap(worldName);
			sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.created"));
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.teleport(new Location(newWorld, 0, 21, 0), TeleportCause.PLUGIN);
			}
			return true;
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.map-exists"));
			return true;
		}
	}
}
