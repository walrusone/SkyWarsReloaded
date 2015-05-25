package com.walrusone.skywars.controllers;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class ScoreboardController {
 
	public void getScoreboard(Player player) {
		if (player != null && SkyWarsReloaded.getCfg().LobbyScoreboardEnabeld()) {
			try {
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
				DecimalFormat df = new DecimalFormat("#.##");
				String kd;
				if (gPlayer.getDeaths() <= 0) {
					kd = df.format(0.00);
				} else {
					kd = df.format((double) gPlayer.getKills()/gPlayer.getDeaths());
				}
		    	ScoreboardManager manager = SkyWarsReloaded.get().getServer().getScoreboardManager();
		        Scoreboard board = manager.getNewScoreboard();
		        Objective stats = board.registerNewObjective("stats", "dummy");
		        stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		        stats.setDisplayName(new Messaging.MessageFormatter().format("scoreboard.title"));
		        if (SkyWarsReloaded.getCfg().usingExternalEcomony()) {
		            Score moneyScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", df.format((double) SkyWarsReloaded.econ.getBalance(player))).format("scoreboard.money")); 
		            moneyScore.setScore(6);
		        } else {
		            Score moneyScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", df.format((double) gPlayer.getBalance())).format("scoreboard.money")); 
		            moneyScore.setScore(6);
		        }
		        Score scoreScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", "" + gPlayer.getScore()).format("scoreboard.score"));
		        scoreScore.setScore(5);
		        Score winsScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", "" + gPlayer.getWins()).format("scoreboard.wins"));
		        winsScore.setScore(4);
		        Score killsScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", "" + gPlayer.getKills()).format("scoreboard.kills"));
		        killsScore.setScore(3);
		        Score gamesScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", "" + gPlayer.getGamesPlayed()).format("scoreboard.games-played"));
		        gamesScore.setScore(2);
	            Score kdScore = stats.getScore(new Messaging.MessageFormatter().setVariable("value", kd).format("scoreboard.kd")); 
	            kdScore.setScore(1);
	            if (player != null) {
	    	        player.setScoreboard(board); 
	            }
			} catch (NullPointerException e) {
			}
			
		}
    } 
}
