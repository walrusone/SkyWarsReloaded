package com.walrusone.skywarsreloaded.commands;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class SWJoinCmd extends BaseCmd { 
	
	public SWJoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		alias = new String[]{"j"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		boolean joined = MatchManager.get().joinGame(player);
		int count = 0;
		while (count < 4 && !joined) {
			joined = MatchManager.get().joinGame(player);
			count++;
		}
		if (!joined) {
			player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join"));
		}
		return true;
	}

}
