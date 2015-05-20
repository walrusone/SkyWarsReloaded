package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.LobbyMainMenu;
import com.walrusone.skywars.menus.MainMenu;

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
		boolean enabled = (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyMenuEnabled") || SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.optionsMenuEnabled"));
		if (enabled) {
			String lobby = SkyWarsReloaded.get().getConfig().getString("spawn.world");
			if (lobby != null) {
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
				if (gPlayer.inGame()) {
					if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.optionsMenuEnabled")) {
						new MainMenu(gPlayer);
					}
					return true;
				} else {
					String world = player.getWorld().getName();
					String lobbyWorld = SkyWarsReloaded.get().getConfig().getString("spawn.world");
					if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyMenuEnabled") && world.equalsIgnoreCase(lobbyWorld)) {
						new LobbyMainMenu(gPlayer);
					}
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You need to have a lobby spawn set!");
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Menu is disabled!");
			return true;
		}
	}

}
