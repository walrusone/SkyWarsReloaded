package com.walrusone.skywars.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class SetSpawnCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.admin")) {
				hasPerm = true;
			}
		} else {
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		}
		if (hasPerm) {
			if (args.length == 1) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					SkyWarsReloaded.get().getConfig().set("spawn.world", p.getLocation().getWorld().getName().toString());
					SkyWarsReloaded.get().getConfig().set("spawn.x", p.getLocation().getBlockX());
					SkyWarsReloaded.get().getConfig().set("spawn.y", p.getLocation().getBlockY());
					SkyWarsReloaded.get().getConfig().set("spawn.z", p.getLocation().getBlockZ());
					SkyWarsReloaded.get().getConfig().set("spawn.yaw", p.getLocation().getYaw());
					SkyWarsReloaded.get().getConfig().set("spawn.pitch", p.getLocation().getPitch());
					SkyWarsReloaded.get().saveConfig();
					sender.sendMessage(new Messaging.MessageFormatter().format("command.spawnset"));
				}
			
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr setspawn");
			}
		
		} 
	return true;
	}
	
}
