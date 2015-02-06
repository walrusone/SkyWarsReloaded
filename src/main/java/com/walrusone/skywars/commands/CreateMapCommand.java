package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class CreateMapCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			hasPerm = true;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.maps")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
		}
		if (hasPerm) {
			if (args.length == 2) {
				String worldName = args[1].toLowerCase();
				if (!SkyWarsReloaded.getMC().mapExists(worldName.toLowerCase())) {
					World newWorld = SkyWarsReloaded.getWC().createEmptyWorld(worldName);
					SkyWarsReloaded.getMC().addEditMap(worldName);
					sender.sendMessage("Map " + worldName + " has been Created!" );
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.teleport(new Location(newWorld, 0, 21, 0));
					}
				} else {
					sender.sendMessage(ChatColor.RED + "There is already a map with that name! Use /swr edit to edit that map!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr create <map name>");
			}
		} 
		return true;
	}

}
