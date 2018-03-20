package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionsSelectionMenu;

public class SWOptionsCmd extends BaseCmd {

	public SWOptionsCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "options";
		alias = new String[]{"o"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionsSelectionMenu(player);
		return true;
	}

}
