package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;

public class SWKillsoundCmd extends BaseCmd { 
	
	public SWKillsoundCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "killsound";
		alias = new String[]{"ks"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionSelectionMenu(player, PlayerOptions.KILLSOUND, true);
		return true;
	}

}
