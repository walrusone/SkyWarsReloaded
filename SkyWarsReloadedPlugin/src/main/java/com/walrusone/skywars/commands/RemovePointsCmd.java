package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Util;


public class RemovePointsCmd extends BaseCmd { 
	
	public RemovePointsCmd() {
		forcePlayer = false;
		cmdName = "removepoints";
		argLength = 3; //counting cmdName
		usage = "<player> <points>";
		desc = ":: Removes <points> from the players balance";

	}

	@Override
	public boolean run() {
		String playerName = args[1];
		int points = 0;
		if (Util.isInteger(args[2])) {
			points = Integer.valueOf(args[2]);
		}
		if (points <= 0) {
			sender.sendMessage(ChatColor.RED + "<points> must be a integer greater than 0");
			return true;
		}
		
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayerByName(playerName);
		if (gPlayer != null) {
			gPlayer.setBalance(gPlayer.getBalance() - points);
			sender.sendMessage(ChatColor.GREEN + "" + points +" were removed from " + playerName + "'s balance");
			if (!gPlayer.inGame()) {
				SkyWarsReloaded.getScore().getScoreboard(gPlayer.getP());
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find a player named: " + playerName);
		}
		return true;
	}

}
