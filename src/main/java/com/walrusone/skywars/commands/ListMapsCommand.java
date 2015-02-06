package com.walrusone.skywars.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class ListMapsCommand implements CommandExecutor {
	
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
			if (args.length == 1) {
				sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Maps (Green = registered, Red = unregistered):");
				ArrayList<String> maps = SkyWarsReloaded.getMC().getEditMaps();
				for (String name: maps) {
					if (SkyWarsReloaded.getMC().mapRegistered(name)) {
						sender.sendMessage(ChatColor.GREEN + name);
					} else {
						sender.sendMessage(ChatColor.RED + name);
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr list");
			}
		} 
		return true;
	}

}
