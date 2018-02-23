package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;

public class SWWinsoundCmd extends BaseCmd { 
	
	public SWWinsoundCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "winsound";
		alias = new String[]{"ws"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionSelectionMenu(player, PlayerOptions.WINSOUND, true);
		return true;
	}

}
