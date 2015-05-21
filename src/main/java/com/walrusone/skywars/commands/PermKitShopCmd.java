package com.walrusone.skywars.commands;

import org.bukkit.Location;

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
		if (SkyWarsReloaded.getCfg().purchasePermanentKitsEnabled()) {
			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
			if (spawn != null) {
				if(SkyWarsReloaded.getCfg().getSpawn().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
					if (!gPlayer.inGame()) {
							new PermKitMenu(gPlayer);
							return true;
					} else {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
						return true;
					}
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.lobby-only-command"));	
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.no-spawn-set"));
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.permanent-kitshop-disabled"));
			return true;
		}
		return true;
	}

}
