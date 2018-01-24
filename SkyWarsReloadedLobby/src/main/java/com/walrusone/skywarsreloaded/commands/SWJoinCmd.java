package com.walrusone.skywarsreloaded.commands;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameSign;
import com.walrusone.skywarsreloaded.objects.SWRServer;

public class SWJoinCmd extends BaseCmd { 
	
	public SWJoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		alias = new String[]{"j"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		SWRServer server = SWRServer.getAvailableServer();
		if (server != null) {
			server.setPlayerCount(server.getPlayerCount() + 1);
	    	GameSign.updateSigns();
			SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
		}
		return true;
	}

}
