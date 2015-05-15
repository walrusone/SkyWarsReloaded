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
        subCommandMap.put("create", new CreateMapCommand());
        subCommandMap.put("edit", new EditMapCommand());
        subCommandMap.put("save", new SaveMapCommand());
        subCommandMap.put("list", new ListMapsCommand());
        subCommandMap.put("register", new RegisterMapCommand());
        subCommandMap.put("setspawn", new SetSpawnCommand());
        subCommandMap.put("reload", new ReloadCommand());
        subCommandMap.put("kit", new KitCommand());
        subCommandMap.put("stats", new StatsCommand());
        subCommandMap.put("delete", new DeleteMapCommand());
        subCommandMap.put("unregister", new UnregisterMapCommand());
        subCommandMap.put("spectate", new SpectateCommand());
        subCommandMap.put("shop", new ShopCommand());
        subCommandMap.put("start", new StartCommand());
        subCommandMap.put("games", new GamesCommand());
        subCommandMap.put("endgame", new EndGameCommand());
        subCommandMap.put("glassshop", new GlassShopCommand());
        subCommandMap.put("permkitshop", new PermKitShopCommand());
        subCommandMap.put("particleshop", new ParticleShopCommand());
        subCommandMap.put("trailshop", new TrailShopCommand());
        subCommandMap.put("menu", new MenuGUICommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        	if (args.length == 0) {
            	sender.sendMessage(ChatColor.RED + "USAGE: /swr <subcommand>");
            	String commands = getSubCommandList(sender);
            	sender.sendMessage(ChatColor.RED + commands);
       			return true;
        	} else {
                String subCommandName = args[0].toLowerCase();
                if (!subCommandMap.containsKey(subCommandName)) {
                	sender.sendMessage(ChatColor.RED + "USAGE: /swr <subcommand>");
                	String commands = getSubCommandList(sender);
                	sender.sendMessage(ChatColor.RED + commands);
           			return true;
                }

                CommandExecutor subCommand = subCommandMap.get(subCommandName);

                return subCommand.onCommand(sender, command, label, args);
        	}	
	
    }
    
    private String getSubCommandList(CommandSender sender) {
    	String players = "join, quit, kit, stats, menu";
    	String start = ", start";
    	String shop = ", shop";
    	String colorshop = ", glassshop";
    	String permKitShop = ", permkitshop";
    	String particleShop = ", particleshop";
    	String trailShop = ", trailshop";
    	String spectate = ", spectate";
    	String admin = ", setspawn, reload, list, games, endgame";
    	String maps = ", create, edit, save, delete, register, unregister";
    	String commands = "";
			if (!(sender instanceof Player)) {
			commands = players + admin;
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SkyWarsReloaded.perms.has(player, "swr.play")) {
				commands = commands + players;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.start")) {
				commands = commands + start;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.shop") || SkyWarsReloaded.perms.has(player,  "swr.spectateshop")) {
				commands = commands + shop;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.colorshop")) {
				commands = commands + colorshop;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.permkits")) {
				commands = commands + permKitShop;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.effectshop")) {
				commands = commands + particleShop;
			}
			if (SkyWarsReloaded.perms.has(player, "swr.projeffectshop")) {
				commands = commands + trailShop;
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
			commands = commands + ".";
		}
			return commands;
    }

}
