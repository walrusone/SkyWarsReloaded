package com.walrusone.skywars.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class StatsCmd extends BaseCmd { 

	public StatsCmd() {
		forcePlayer = true;
		cmdName = "stats";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: View your skywars stats";

	}
	private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
	
	@Override
	public boolean run() {
		int cooldownLength = SkyWarsReloaded.getCfg().getStatsCommandCooldown();
		if(cooldown.containsKey(player)) {
			long secondsLeft = ((cooldown.get(player)/1000) + cooldownLength) - (System.currentTimeMillis()/1000);
			if(secondsLeft>0) {
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("timeleft", "" + secondsLeft).format("command.stats-cooldown"));
				return true;
			} 
		} 
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
		double kd = ((double) gPlayer.getKills())/gPlayer.getDeaths();
		player.sendMessage(" ");
		player.sendMessage(ChatColor.RED + player.getName() + "'s Stats");
		player.sendMessage(ChatColor.RED + "______________________");
		player.sendMessage(ChatColor.GREEN + "Games Played: " + ChatColor.YELLOW + gPlayer.getGamesPlayed());
		player.sendMessage(ChatColor.GREEN + "Score: " + ChatColor.YELLOW + gPlayer.getScore());
		player.sendMessage(ChatColor.GREEN + "Games Won: " + ChatColor.YELLOW + gPlayer.getWins());
		player.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.YELLOW + gPlayer.getKills());
		player.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.YELLOW + gPlayer.getDeaths());
		player.sendMessage(ChatColor.GREEN + "K/D Ratio: " + ChatColor.YELLOW + kd);
		player.sendMessage(ChatColor.GREEN + "Blocks Placed: " + ChatColor.YELLOW + gPlayer.getBlocks());
		cooldown.put(player, System.currentTimeMillis());
		return true;
	}

}
