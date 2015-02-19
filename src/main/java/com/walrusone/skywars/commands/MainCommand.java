package com.walrusone.skywars.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.walrusone.skywars.SkyWarsReloaded;

public class MainCommand implements CommandExecutor {

    private Map<String, CommandExecutor> subCommandMap = Maps.newHashMap();

    public MainCommand() {
        subCommandMap.put("join", new JoinGameCommand());
        subCommandMap.put("quit", new QuitGameCommand());
        subCommandMap.put("list", new ListMapsCommand());
        subCommandMap.put("setspawn", new SetSpawnCommand());
        subCommandMap.put("reload", new ReloadCommand());
        subCommandMap.put("kit", new KitCommand());
        subCommandMap.put("stats", new StatsCommand());
        subCommandMap.put("spectate", new SpectateCommand());
        subCommandMap.put("shop", new ShopCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        	if (args.length == 0) {
            	sender.sendMessage(ChatColor.RED + "USAGE: /wb <subcommand>");
            	String players = "join, quit, kit, stats";
            	String shop = " shop,";
            	String spectate = ", spectate";
            	String admin = ", setspawn, reload";
            	String maps = ", list";
            	String commands = "";
       			if (!(sender instanceof Player)) {
        			commands = players + admin + maps;
        		} else if (sender instanceof Player) {
        			Player player = (Player) sender;
        			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
        				commands = commands + players;
        			}
        			if (SkyWarsReloaded.perms.has(player, "swr.shop")) {
        				commands = commands + shop;
        			}
        			if (SkyWarsReloaded.perms.has(player, "swr.spectate")) {
        				commands = commands + spectate;
        			}
        			if (SkyWarsReloaded.perms.has(player, "swr.admin")) {
        				commands = commands + admin;
        			}
        			if (SkyWarsReloaded.perms.has(player, "swr.maps")) {
        				commands = commands + maps;
        			}
        		}
       			sender.sendMessage(ChatColor.RED + commands);
       			return true;
        	} else {
                String subCommandName = args[0].toLowerCase();
                if (!subCommandMap.containsKey(subCommandName)) {
                	return true;
                }

                CommandExecutor subCommand = subCommandMap.get(subCommandName);

                return subCommand.onCommand(sender, command, label, args);
        	}	
	
    }

}
