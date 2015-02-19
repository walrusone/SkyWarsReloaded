package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
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
					GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
					Game game = gPlayer.getGame();
					if (game != null) {
						game.deletePlayer(gPlayer, true);
						if (game.getState() == GameState.PREGAME || game.getState() == GameState.PLAYING) {
							game.checkForWinner();
						}
					} else {
						if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating")) {
							if(SkyWarsReloaded.getSpectate().isSpectator(player)) {
								SkyWarsReloaded.getSpectate().setSpectating(player, false, true);
								gPlayer.setSpectating(false);
								gPlayer.getSpecGame().removeSpectator(gPlayer);
								String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
								int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
								int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
								int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
								Location loc = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z);
								player.teleport(loc);
							}
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
