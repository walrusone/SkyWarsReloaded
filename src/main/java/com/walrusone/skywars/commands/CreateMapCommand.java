package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class CreateMapCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			hasPerm = true;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.maps")) {
				SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).setRespawn(player.getLocation());
				hasPerm = true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			if (args.length == 2) {
				String worldName = args[1].toLowerCase();
				if (!SkyWarsReloaded.getMC().mapExists(worldName.toLowerCase())) {
					World newWorld = SkyWarsReloaded.getWC().createEmptyWorld(worldName);
					if (newWorld == null) {
						sender.sendMessage(new Messaging.MessageFormatter().format("error.map.world.exists"));
						return true;
					}
					SkyWarsReloaded.getMC().addEditMap(worldName);
					sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.created"));
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.teleport(new Location(newWorld, 0, 21, 0));
					}
				} else {
					sender.sendMessage(new Messaging.MessageFormatter().format("error.map-exists"));
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr create <map name>");
			}
		} 
		return true;
	}

}
