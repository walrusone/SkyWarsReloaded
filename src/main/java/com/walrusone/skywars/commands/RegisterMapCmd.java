package com.walrusone.skywars.commands;

import java.io.File;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class RegisterMapCmd extends BaseCmd { 
	
	public RegisterMapCmd() {
		forcePlayer = false;
		cmdName = "register";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Attempts to register a map";

	}

	@Override
	public boolean run() {
		String worldName = args[1].toLowerCase();
		if (SkyWarsReloaded.getMC().mapExists(worldName)) {
			File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
			File newMap = new File (dataDirectory, worldName);
			if (newMap.isDirectory()) {
				if (SkyWarsReloaded.getMC().registerMap(worldName)) {
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.registered"));
					return true;
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("error.map-not-registered"));
					return true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error-register-is-it-saved"));
				return true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error-register-not-exist"));
			return true;
		}
	}

}
