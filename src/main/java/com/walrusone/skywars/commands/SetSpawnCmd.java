package com.walrusone.skywars.commands;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class SetSpawnCmd extends BaseCmd { 
	
	public SetSpawnCmd() {
		forcePlayer = true;
		cmdName = "setspawn";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Sets the spawn return point";

	}

	@Override
	public boolean run() {
		SkyWarsReloaded.get().getConfig().set("spawn.world", player.getLocation().getWorld().getName().toString());
		SkyWarsReloaded.get().getConfig().set("spawn.x", player.getLocation().getBlockX());
		SkyWarsReloaded.get().getConfig().set("spawn.y", player.getLocation().getBlockY());
		SkyWarsReloaded.get().getConfig().set("spawn.z", player.getLocation().getBlockZ());
		SkyWarsReloaded.get().getConfig().set("spawn.yaw", player.getLocation().getYaw());
		SkyWarsReloaded.get().getConfig().set("spawn.pitch", player.getLocation().getPitch());
		SkyWarsReloaded.get().saveConfig();
		sender.sendMessage(new Messaging.MessageFormatter().format("command.spawnset"));
		return true;
	}

}
