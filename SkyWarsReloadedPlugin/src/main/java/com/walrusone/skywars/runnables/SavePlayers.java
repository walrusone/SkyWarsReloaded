package com.walrusone.skywars.runnables;

import com.walrusone.skywars.SkyWarsReloaded;

public class SavePlayers implements Runnable {

	@Override
	public void run() {
		SkyWarsReloaded.getPC().savePlayersAsync();
	}

}
