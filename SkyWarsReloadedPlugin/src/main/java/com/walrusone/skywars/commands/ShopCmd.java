package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.menus.ShopMenu;
import com.walrusone.skywars.menus.SpecShopMenu;
import com.walrusone.skywars.utilities.Messaging;

public class ShopCmd extends BaseCmd { 
	
	public ShopCmd() {
		forcePlayer = true;
		cmdName = "shop";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Opens a in-game shop.";

	}

	@Override
	public boolean run() {
		boolean enabled = (SkyWarsReloaded.getCfg().shopsEnabled() || SkyWarsReloaded.getCfg().spectateShopEnabled());
		if (enabled) {
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (gPlayer.inGame()) {
				if (player.hasPermission("swr.shop") && SkyWarsReloaded.getCfg().shopsEnabled()) {
					if (gPlayer.getGame().getState() == GameState.PREGAME || gPlayer.getGame().getState() == GameState.PLAYING) {
						new ShopMenu(gPlayer);
					}
					return true;
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
					return true;
				}
			} else if (gPlayer.isSpectating() && SkyWarsReloaded.getCfg().spectateShopEnabled()) {
				if (player.hasPermission("swr.spectateshop")) {
					if (gPlayer.getSpecGame().getState() == GameState.PREGAME || gPlayer.getSpecGame().getState() == GameState.PLAYING) {
						new SpecShopMenu(gPlayer);
					}
					return true;
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
					return true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Shops are disabled!");
			return true;
		}
	}

}
