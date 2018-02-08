package com.walrusone.skywarsreloaded.commands.admin;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class StartCmd extends BaseCmd { 
	
	public StartCmd() {
		forcePlayer = true;
		cmdName = "start";
		alias = new String[]{""};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		MatchManager.get().forceStart(player);
		return true;
	}

}
