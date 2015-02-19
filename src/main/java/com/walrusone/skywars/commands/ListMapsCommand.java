package com.walrusone.skywars.commands;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

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
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Maps");
				CopyOnWriteArrayList<String> maps = SkyWarsReloaded.getMC().getMaps();
				for (String name: maps) {
					sender.sendMessage(ChatColor.GREEN + name);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr list");
			}
		} 
		return true;
	}

}
