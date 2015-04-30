package com.walrusone.skywars.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class StatsCommand implements CommandExecutor {
	private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			int cooldownLength = SkyWarsReloaded.get().getConfig().getInt("gameVariables.statsCommandCooldown");
			if (args.length == 1) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
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
					} else {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.no-perm-in-game"));
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr join");
			}
		} 
    	return true;
    }

}
