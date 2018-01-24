package com.walrusone.skywarsreloaded.commands;

import java.util.List;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.objects.Leaderboard.LeaderData;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class SWTopCmd extends BaseCmd { 
	
	public SWTopCmd() {
		forcePlayer = true;
		cmdName = "top";
		alias = new String[]{"leaderboard"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		if (!SkyWarsReloaded.getLB().loaded()) {
			player.sendMessage(new Messaging.MessageFormatter().format("leaderboard.updating"));
			return true;
		}
		player.sendMessage(new Messaging.MessageFormatter().format("leaderboard.header"));
        final List<LeaderData> top = SkyWarsReloaded.getLB().getTop(5);
        player.sendMessage(new Messaging.MessageFormatter().format("leaderboard.header2"));
        if (top.size() == 0) {
        	player.sendMessage(new Messaging.MessageFormatter().format("leaderboard.no-data"));
        }
        for (int i = 0; i < top.size(); ++i) {
            final LeaderData playerData = top.get(i);
            player.sendMessage(new Messaging.MessageFormatter().setVariable("rank", "" + (i + 1)).
            		setVariable("player", playerData.getName()).
            		setVariable("elo", "" + playerData.getElo()).
            		setVariable("wins", "" + playerData.getWins()).
            		setVariable("losses", "" + playerData.getLoses()).
            		setVariable("kills", "" + playerData.getKills()).
            		setVariable("deaths", "" + playerData.getDeaths()).
            		setVariable("xp", "" + playerData.getXp()).
            		format("leaderboard.player-data"));
        }
        player.sendMessage(new Messaging.MessageFormatter().format("leaderboard.footer"));
		return true;
	}

}
