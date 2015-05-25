package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.menus.KitMenu;
import com.walrusone.skywars.utilities.Messaging;

public class KitCmd extends BaseCmd { 

	public KitCmd() {
		forcePlayer = true;
		cmdName = "kit";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Opens the kit menu";

	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().kitsEnabled()) {
			Player player = (Player) sender;
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (gPlayer.inGame()) {
				if (gPlayer.getGame().getState() == GameState.PREGAME && !gPlayer.hasKitSelected()) {
					new KitMenu(gPlayer);
				}
				return true;
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.not-in-game"));
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Kits are disabled!");
			return true;
		}
	}

}
