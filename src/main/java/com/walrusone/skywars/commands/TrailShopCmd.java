package com.walrusone.skywars.commands;

import org.bukkit.Location;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.BuyProjEffectMenu;
import com.walrusone.skywars.utilities.Messaging;

public class TrailShopCmd extends BaseCmd { 
	
	public TrailShopCmd() {
		forcePlayer = true;
		cmdName = "trailshop";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Opens the trail effects shop";

	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().purchaseTrailEffectsEnabled()) {
			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
			if (spawn != null) {
				if(SkyWarsReloaded.getCfg().getSpawn().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
					if (!gPlayer.inGame()) {
							new BuyProjEffectMenu(gPlayer);
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
			sender.sendMessage(new Messaging.MessageFormatter().format("error.trail-shop-disabled"));
			return true;
		}
		return true;
	}

}
