package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class UnregisterMapCommand implements CommandExecutor {

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
				if (SkyWarsReloaded.getMC().mapExists(worldName)) {
					if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
						SkyWarsReloaded.getMC().removeMap(worldName);
						sender.sendMessage(ChatColor.RED + "Map " + worldName + " was unregistered!");
					} else {
						sender.sendMessage(ChatColor.RED + "The map is not registered!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "The map does not Exist!");
				}
			}else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr unregister <map name>");
			}
		} 
		return true;
	}
}
