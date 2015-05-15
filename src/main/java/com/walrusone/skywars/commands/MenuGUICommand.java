package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.menus.LobbyMainMenu;
import com.walrusone.skywars.menus.MainMenu;
import com.walrusone.skywars.utilities.Messaging;

public class MenuGUICommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean enabled = (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyMenuEnabled") || SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.optionsMenuEnabled"));
		if (enabled) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (sender instanceof Player) {
						GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
						if (gPlayer.inGame()) {
							if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.optionsMenuEnabled")) {
								new MainMenu(gPlayer);
							}
						} else {
							String world = player.getWorld().getName();
							String lobbyWorld = SkyWarsReloaded.get().getConfig().getString("spawn.world");
							if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.lobbyMenuEnabled") && world.equalsIgnoreCase(lobbyWorld)) {
								new LobbyMainMenu(gPlayer);
							}
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "USAGE: /swr menu");
				}
			} 

		} 
		
    	return true;
    }

}
