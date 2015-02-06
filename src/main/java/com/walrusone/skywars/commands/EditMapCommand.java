package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;

public class EditMapCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPerm = false;
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to edit a map!");
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
				final String worldName = args[1].toLowerCase();
				if (SkyWarsReloaded.getMC().mapExists(worldName)) {
					boolean alreadyLoaded = false;
					if (sender instanceof Player) {
						final Player player = (Player) sender;
						for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
							if (world.getName().equalsIgnoreCase(worldName)) {
								alreadyLoaded = true;
								World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
								player.teleport(new Location(editWorld, 0, 21, 0));
							}
						} 
						if (!alreadyLoaded) {
							File dataDirectory = new File(SkyWarsReloaded.get().getDataFolder(), "maps");
							File source = new File (dataDirectory, worldName);
							File target = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
							SkyWarsReloaded.getWC().copyWorld(source, target);
							SkyWarsReloaded.getWC().loadWorld(worldName);
							SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
								public void run() {
									World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
									player.teleport(new Location(editWorld, 0, 21, 0));
								  }
								}, 20);
						}
					} 
				} else {
					sender.sendMessage(ChatColor.RED + "There is no map by that name! Create the map first!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "USAGE: /swr edit <map name>");
			}
		} 		
		return true;
	}

}
