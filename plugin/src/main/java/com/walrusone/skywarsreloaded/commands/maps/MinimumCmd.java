package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class MinimumCmd extends BaseCmd { 
	
	public MinimumCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "minimum";
		alias = new String[]{"min"};
		argLength = 3; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1];
			if (!Util.get().isInteger(args[2])) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-min-be-int"));
				return false;
			}
			
			int min = Integer.valueOf(args[2]);
			GameMap map = GameMap.getMap(worldName);
			if (map != null) {
				map.setMinTeams(min);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).setVariable("min", args[2]).format("maps.minplayer"));
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
	}
}
