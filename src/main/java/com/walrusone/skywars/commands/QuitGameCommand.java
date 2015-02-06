package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;

public class QuitGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to play a game!");
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
		}
		if (hasPerm) {
			if (args.length == 1) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
					Game game = gPlayer.getGame();
					if (game != null) {
						game.deletePlayer(gPlayer, true);
						if (game.getState() == GameState.PLAYING || game.getState() == GameState.PLAYING) {
							game.checkForWinner();
						}
					}
				}
			}else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr quit");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
		}
        return true;
	}

}
