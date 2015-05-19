package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.PermKitMenu;
import com.walrusone.skywars.utilities.Messaging;

public class PermKitShopCmd extends BaseCmd { 
	
	public PermKitShopCmd() {
		forcePlayer = true;
		cmdName = "permkitshop";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Menu for purchasing permanent kits";

	}

	@Override
	public boolean run() {
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchasePermanentKitsEnabled");
		if (enabled) {
			Player player = (Player) sender;
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (!gPlayer.inGame()) {
					new PermKitMenu(gPlayer);
					return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Permanent Kit Shop is disabled!");
			return true;
		}
	}

}
