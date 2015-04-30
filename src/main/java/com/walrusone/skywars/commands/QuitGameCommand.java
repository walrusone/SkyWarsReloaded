package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.utilities.Messaging;

public class QuitGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			if (args.length == 1) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
					if (gPlayer.inGame() && !gPlayer.isSpectating()) {
						Game game = gPlayer.getGame();
						game.deletePlayer(gPlayer, true, false);
					} else if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating")) {
						if (gPlayer.isSpectating()){
							gPlayer.setSpectating(false);
							gPlayer.getSpecGame().removeSpectator(gPlayer);
							}
						}
					}
			}else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr quit");
			}
		}
        return true;
	}

}
