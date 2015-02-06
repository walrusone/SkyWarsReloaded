package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class RegisterMapCommand implements CommandExecutor {

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
					File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
					File newMap = new File (dataDirectory, worldName);
					if (newMap.isDirectory()) {
						SkyWarsReloaded.getMC().registerMap(worldName);
					} else {
						sender.sendMessage(ChatColor.RED + "The map does not exist in the Maps directory! Have you saved the map?");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "There is no map by that name! Create the map first!");
				}
			}else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr register <map name>");
			}
		} 
		return true;
	}
}
