package com.walrusone.skywars.commands;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;


public class EndGameCmd extends BaseCmd { 

	public EndGameCmd() {
		forcePlayer = false;
		cmdName = "endgame";
		argLength = 2; //counting cmdName
		usage = "<gamenumber>";
		desc = ":: Ends the selected game";

	}

	@Override
	public boolean run() {
		String arg = args[1];	
		if (isInteger(arg)) {
			Game game = SkyWarsReloaded.getGC().getGame(Integer.valueOf(arg));
			if (game != null) {
				game.endGame();
				return true;
			}
		} else if (arg.equalsIgnoreCase("all")) {
			for (Game game: SkyWarsReloaded.getGC().getGames()) {
				game.endGame();
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
}
