package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.ShopMenu;
import com.walrusone.skywars.menus.SpecShopMenu;
import com.walrusone.skywars.utilities.Messaging;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean enabled = (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.shopsEnabled") || SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.spectateShopEnabled"));
		if (enabled) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} 			
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
						if (gPlayer.inGame()) {
							if (SkyWarsReloaded.perms.has(player, "swr.shop") && SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.shopsEnabled")) {
								if (gPlayer.getGame().getState() == GameState.PREGAME || gPlayer.getGame().getState() == GameState.PLAYING) {
									new ShopMenu(gPlayer);
								}
							} else {
								sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
							}
						} else if (gPlayer.isSpectating() && SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.spectateShopEnabled")) {
							if (SkyWarsReloaded.perms.has(player, "swr.spectateshop")) {
								if (gPlayer.getSpecGame().getState() == GameState.PREGAME || gPlayer.getSpecGame().getState() == GameState.PLAYING) {
									new SpecShopMenu(gPlayer);
								}
							} else {
								sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
							}
						} else {
							sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "USAGE: /swr shop");
				}
		} else {
			sender.sendMessage(ChatColor.RED + "Shops are disabled!");
		}
		
    	return true;
    }

}
