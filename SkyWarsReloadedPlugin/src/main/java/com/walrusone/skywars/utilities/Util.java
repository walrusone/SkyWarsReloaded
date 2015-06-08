package com.walrusone.skywars.utilities;

import org.bukkit.command.CommandSender;

public class Util {

	public static boolean hp(CommandSender sender, String s) {
		if (sender.hasPermission("swr." + s)) {
			return true;
		}
		return false;
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	
}
