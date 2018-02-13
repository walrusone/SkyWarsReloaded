package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class CreatorCmd extends BaseCmd { 
	
	public CreatorCmd(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "creator";
		alias = new String[]{"maker"};
		argLength = 3; //counting cmdName
	}

	@Override
	public boolean run() {
			String worldName = args[1].toLowerCase();
			String creator = args[2];
			for (int i = 2; i < args.length; i++) {
				creator = creator + args[i] + " ";
			}
			creator = creator.substring(0, creator.length() - 1);
			if (creator.length() == 0) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-creator"));
				return false;
			}
			GameMap map = GameMap.getMap(worldName);
			if (map != null) {
				map.setCreator(creator);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).setVariable("creator", args[2]).format("maps.creator"));
				
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.map-does-not-exist"));
				return true;
			}
	}
}
