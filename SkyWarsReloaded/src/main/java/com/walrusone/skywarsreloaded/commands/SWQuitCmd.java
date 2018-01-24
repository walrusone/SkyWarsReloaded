package com.walrusone.skywarsreloaded.commands;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.objects.GameMap;

public class SWQuitCmd extends BaseCmd { 
	
	public SWQuitCmd() {
		forcePlayer = true;
		cmdName = "quit";
		alias = new String[]{"q"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		GameMap map = MatchManager.get().getPlayerMap(player);
		if (map == null) {
			return false;
		}
		MatchManager.get().playerLeave(player, DamageCause.CUSTOM, true, true);
		return true;
	}

}
