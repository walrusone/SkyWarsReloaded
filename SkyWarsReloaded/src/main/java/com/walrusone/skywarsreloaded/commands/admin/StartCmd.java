package com.walrusone.skywarsreloaded.commands.admin;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;

public class StartCmd extends BaseCmd { 
	
	public StartCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "start";
		alias = new String[]{"s"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		GameMap gMap = MatchManager.get().getPlayerMap(player);
		if (gMap != null) {
			MatchManager.get().forceStart(player);
		}
		return true;
	}

}
