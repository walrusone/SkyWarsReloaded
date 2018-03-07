package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;

public class SWGlassCmd extends BaseCmd { 
	
	public SWGlassCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "glass";
		alias = new String[]{"g"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionSelectionMenu(player, PlayerOptions.GLASSCOLOR, true);
		return true;
	}

}
