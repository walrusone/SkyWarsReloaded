package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class SetSpawnCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to set the spawn!");
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
				if (sender instanceof Player) {
					Player p = (Player) sender;
					SkyWarsReloaded.get().getConfig().set("spawn.world", p.getLocation().getWorld().getName().toString());
					SkyWarsReloaded.get().getConfig().set("spawn.x", p.getLocation().getBlockX());
					SkyWarsReloaded.get().getConfig().set("spawn.y", p.getLocation().getBlockY());
					SkyWarsReloaded.get().getConfig().set("spawn.z", p.getLocation().getBlockZ());
					SkyWarsReloaded.get().saveConfig();
					p.sendMessage(ChatColor.GREEN + "Bounce Wars Spawn Set!");
				}
			
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr setspawn");
			}
		
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
		}
	return true;
	}
	
}
