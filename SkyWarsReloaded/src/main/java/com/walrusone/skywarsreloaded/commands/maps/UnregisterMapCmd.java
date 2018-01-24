package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class UnregisterMapCmd extends BaseCmd { 
	
	public UnregisterMapCmd() {
		forcePlayer = false;
		cmdName = "unregister";
		alias = new String[]{};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
			if (GameMap.mapExists(worldName)) {
				if (GameMap.mapRegistered(worldName)) {
					GameMap.removeMap(worldName);
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.unregistered"));
					return true;
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.unregistered"));
					return true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-register-not-exist"));
				return true;
			}
	}
}
