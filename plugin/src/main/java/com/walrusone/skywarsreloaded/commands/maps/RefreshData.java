package com.walrusone.skywarsreloaded.commands.maps;


import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class RefreshData extends BaseCmd {

	public RefreshData(String t) {
		type = t;
		forcePlayer = false;
		cmdName = "refresh";
		alias = new String[]{"ref"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		for (GameMap gMap: GameMap.getMaps()) {
			gMap.loadArenaData();
		}
		sender.sendMessage(new Messaging.MessageFormatter().format("maps.refreshed"));
		return true;
	}
}
