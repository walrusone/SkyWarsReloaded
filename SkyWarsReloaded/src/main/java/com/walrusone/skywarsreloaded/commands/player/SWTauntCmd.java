package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;

public class SWTauntCmd extends BaseCmd { 
	
	public SWTauntCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "taunt";
		alias = new String[]{"t"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionSelectionMenu(player, PlayerOptions.TAUNT, true);
		return true;
	}

}
