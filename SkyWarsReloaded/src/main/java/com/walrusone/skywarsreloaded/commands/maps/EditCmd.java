package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;


public class EditCmd extends BaseCmd { 

	public EditCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "edit";
		alias = new String[]{"e"};
		argLength = 2; //counting cmdName

	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().getSpawn() != null) {
			final String worldName = args[1].toLowerCase();
			GameMap gMap = GameMap.getMap(worldName);
			if (gMap == null) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
			GameMap.editMap(gMap, player);
			return true;
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.nospawn"));
			return false;
		}
	}
}
