package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.utilities.Messaging;

public class GamesCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			hasPerm = true;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.admin")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			if (args.length == 1) {
				for (Game game: SkyWarsReloaded.getGC().getGames()) {
					int number = game.getGameNumber();
					String map = game.getMapName();
					int players = game.getPlayers().size();
					String message = number + "   " + map + "   " + players;
					sender.sendMessage(message);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr games");
			}
		
		} 
	return true;
	}
	
}
