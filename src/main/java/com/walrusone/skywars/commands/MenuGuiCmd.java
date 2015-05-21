package com.walrusone.skywars.commands;

import org.bukkit.Location;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.LobbyMainMenu;
import com.walrusone.skywars.menus.MainMenu;
import com.walrusone.skywars.utilities.Messaging;

public class MenuGuiCmd extends BaseCmd { 

	public MenuGuiCmd() {
		forcePlayer = true;
		cmdName = "menu";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Opens the lobby menu";

	}

	@Override
	public boolean run() {
		boolean enabled = (SkyWarsReloaded.getCfg().lobbyMenuEnabled() || SkyWarsReloaded.getCfg().optionsMenuEnabled());
		if (enabled) {
			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
			if (spawn != null) {
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
				if (gPlayer.inGame()) {
					if (SkyWarsReloaded.getCfg().optionsMenuEnabled()) {
						new MainMenu(gPlayer);
					} else {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.menu-disabled"));
						return true;
					}
					return true;
				} else {
					String world = player.getWorld().getName();
					String lobbyWorld = spawn.getWorld().getName();
					if (SkyWarsReloaded.getCfg().lobbyMenuEnabled()) {
						if (world.equalsIgnoreCase(lobbyWorld)) {
							new LobbyMainMenu(gPlayer);
						} else {
							sender.sendMessage(new Messaging.MessageFormatter().format("error.lobby-only-command"));	
						}
					} else {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.menu-disabled"));
					}
					return true;
				}
			} else {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.no-spawn-set"));
				return true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.menu-disabled"));
			return true;
		}
	}

}
