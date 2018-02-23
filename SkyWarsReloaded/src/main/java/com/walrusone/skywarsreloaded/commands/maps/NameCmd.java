package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class NameCmd extends BaseCmd { 
	
	public NameCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "name";
		alias = new String[]{"n"};
		argLength = 3; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
			String displayName = "";
			for (int i = 2; i < args.length; i++) {
				displayName = displayName + args[i] + " ";
			}
			displayName = displayName.substring(0, displayName.length() - 1);
			if (displayName.length() == 0) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-name"));
				return false;
			}
			
			GameMap map = GameMap.getMap(worldName);
			if (map != null) {
				map.setDisplayName(displayName);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).setVariable("displayname", args[2]).format("maps.name"));
				
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
	}
}
