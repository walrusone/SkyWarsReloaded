package com.walrusone.skywarsreloaded.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameMap;

public class ListMapCmd extends BaseCmd { 
	
	public ListMapCmd() {
		forcePlayer = true;
		cmdName = "listmaps";
		alias = new String[]{"lm", "lmaps"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		sender.sendMessage("MAPS");
		for (GameMap map: GameMap.getMaps()) {
			sender.sendMessage("- " + map.getName());
		}
		return true;
	}

}
