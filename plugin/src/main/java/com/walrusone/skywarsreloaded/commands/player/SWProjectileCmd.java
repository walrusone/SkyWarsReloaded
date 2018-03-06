package com.walrusone.skywarsreloaded.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.PlayerOptions;
import com.walrusone.skywarsreloaded.menus.playeroptions.OptionSelectionMenu;

public class SWProjectileCmd extends BaseCmd { 
	
	public SWProjectileCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "projectile";
		alias = new String[]{"proj"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		new OptionSelectionMenu(player, PlayerOptions.PROJECTILEEFFECT, true);
		return true;
	}

}
