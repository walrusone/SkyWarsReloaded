package com.walrusone.skywars.commands;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.Messaging;

public class StartCmd extends BaseCmd { 
	
	public StartCmd() {
		forcePlayer = true;
		cmdName = "start";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Force start a game";

	}

	@Override
	public boolean run() {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
		if (gPlayer.inGame()) {
			Game game = gPlayer.getGame();
			if (game.getState() == GameState.PREGAME) {
				game.startGame();
			}
			return true;
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.not-in-game"));
			return true;
		}
	}

}
