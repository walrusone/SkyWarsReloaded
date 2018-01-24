package com.walrusone.skywarsreloaded.commands.maps;

import java.io.File;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class RegisterMapCmd extends BaseCmd { 
	
	public RegisterMapCmd() {
		forcePlayer = false;
		cmdName = "register";
		alias = new String[]{};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
			if (GameMap.mapExists(worldName)) {
				File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
				File newMap = new File (dataDirectory, worldName);
				if (newMap.isDirectory()) {
					GameMap.registerMap(worldName);
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.map-register-is-it-saved"));
					return true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-register-not-exist"));
				return true;
			}
		return false;
	}
}
