package com.walrusone.skywars.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Maps;

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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        	if (args.length == 0) {
            	sender.sendMessage(ChatColor.RED + "USAGE: /wb <subcommand>");
            	sender.sendMessage(ChatColor.RED + "Subcommands: load, delete, copy or unload");
                return true;
            }

            String subCommandName = args[0].toLowerCase();
            if (!subCommandMap.containsKey(subCommandName)) {
            	return true;
            }

            CommandExecutor subCommand = subCommandMap.get(subCommandName);

            return subCommand.onCommand(sender, command, label, args);	
    }

}
