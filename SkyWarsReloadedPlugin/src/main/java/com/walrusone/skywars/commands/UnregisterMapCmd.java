package com.walrusone.skywars.commands;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class UnregisterMapCmd extends BaseCmd { 
	
	public UnregisterMapCmd() {
		forcePlayer = false;
		cmdName = "unregister";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Unregisters a map";

	}

	@Override
	public boolean run() {
		String worldName = args[1].toLowerCase();
		if (SkyWarsReloaded.getMC().mapExists(worldName)) {
			if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
				SkyWarsReloaded.getMC().removeMap(worldName);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.unregistered"));
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.unregistered"));
				return true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error-register-not-exist"));
			return true;
		}
	}

}
