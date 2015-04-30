package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.utilities.Messaging;

public class EndGameCommand implements CommandExecutor {
	
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
			if (args.length == 2) {
				String arg = args[1];	
				if (isInteger(arg)) {
					Game game = SkyWarsReloaded.getGC().getGame(Integer.valueOf(arg));
					if (game != null) {
						game.endGame();
					}
				} else if (arg.equalsIgnoreCase("all")) {
					for (Game game: SkyWarsReloaded.getGC().getGames()) {
						game.endGame();
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr endgame <gamenumber>");
			}
		
		} 
	return true;
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
