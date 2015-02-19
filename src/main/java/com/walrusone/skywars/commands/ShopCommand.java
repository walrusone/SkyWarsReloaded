package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.shopsEnabled");
		if (enabled) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				if (SkyWarsReloaded.perms.has(player, "swr.shop")) {
					hasPerm = true;
				}
			} 
			if (hasPerm) {
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
						if (gPlayer.getGame() != null) {
							if (gPlayer.getGame().getState() == GameState.PREGAME || gPlayer.getGame().getState() == GameState.PLAYING) {
								SkyWarsReloaded.getSC().openKitMenu(gPlayer);
							}
						} else {
							sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "USAGE: /swr shop");
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Shops are disabled!");
		}
		
    	return true;
    }

}
