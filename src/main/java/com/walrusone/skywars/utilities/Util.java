package com.walrusone.skywars.utilities;

import org.bukkit.command.CommandSender;

public class Util {

	public static boolean hp(CommandSender sender, String s) {
		if (sender.hasPermission("swr." + s)) {
			return true;
		}
		return false;
	}
	
	
}
