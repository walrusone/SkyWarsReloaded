package com.walrusone.skywars.runnables;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;

public class CheckForMinPlayers implements Runnable {

	@Override
	public void run() {
		for(Game game: SkyWarsReloaded.getGC().getGames()) {
    		game.prepareForStart();
    	}
	}

}
