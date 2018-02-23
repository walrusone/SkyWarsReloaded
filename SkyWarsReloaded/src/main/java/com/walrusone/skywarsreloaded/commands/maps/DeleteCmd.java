package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class DeleteCmd extends BaseCmd { 
	
	public DeleteCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "delete";
		alias = new String[]{"d", "remove"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
			GameMap map = GameMap.getMap(worldName);
			if (map != null) {
				map.removeMap();
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.deleted"));
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
	}
}
