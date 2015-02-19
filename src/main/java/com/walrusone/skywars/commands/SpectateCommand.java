package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			boolean hasPerm = false;
			if (!(sender instanceof Player)) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				if (SkyWarsReloaded.perms.has(player, "swr.spectate")) {
					hasPerm = true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
			}
			if (hasPerm) {
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
						if (gPlayer.getGame() == null) {
								SkyWarsReloaded.getGC().openGameMenu(gPlayer);
						} else {
							sender.sendMessage(new Messaging.MessageFormatter().format("error.no-spectate-other-worlds"));
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "USAGE: /swr kit");
				}
			} 
		
    	return true;
    }

}
