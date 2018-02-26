package com.walrusone.skywarsreloaded.commands.maps;


import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
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
    	new BukkitRunnable() {
			@Override
			public void run() {
				GameMap.updateArenasManager();
			}
		}.runTaskLater(SkyWarsReloaded.get(), 2);
		return true;
	}
}
