package com.walrusone.skywarsreloaded.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public abstract class BaseCmd {

	public BaseCmd() {
		
	}
	
	
	public CommandSender sender;
	public String[] args;
	public String[] alias;
	public String cmdName;
	public int argLength = 0;
	public boolean forcePlayer = true;
	public Player player;

	public boolean processCmd(CommandSender s, String[] arg) {
		sender = s;
		args = arg;

		if (forcePlayer) {
			if (!(s instanceof Player))  {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
				return false;
			} else {
				player = (Player) s;
			}
		}
		
		if (!Util.get().hp(sender, cmdName))
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		else if (argLength > arg.length)
			s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + new Messaging.MessageFormatter().format("helpList." + cmdName));
		else return run();
		return true;
	}

	public abstract boolean run();

}
