package com.walrusone.skywarsreloaded.commands.maps;


import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;

public class ArenaCmd extends BaseCmd {

	public ArenaCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "arenas";
		alias = new String[]{"a"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		GameMap.openArenasManager(player);
		GameMap.updateArenasManager();
		return true;
	}
}
