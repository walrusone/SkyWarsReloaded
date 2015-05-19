package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
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
		boolean enabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.purchaseTrailEffectsEnabled");
		if (enabled) {
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (!gPlayer.inGame()) {
					new BuyProjEffectMenu(gPlayer);
					return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.shop-not-available"));
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Trial Effects Shop is disabled!");
			return true;
		}
	}

}
