package com.walrusone.skywarsreloaded.commands.admin;

import org.bukkit.ChatColor;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;

public class HoloRemoveCmd extends BaseCmd { 
	
	public HoloRemoveCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "holoremove";
		alias = new String[]{"hr"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().hologramsEnabled()) {
			boolean result = SkyWarsReloaded.getHoloManager().removeHologram(player.getLocation());
			if (result) {
				player.sendMessage(ChatColor.GREEN + "The closest hologram was removed");
				return true;
			}
			player.sendMessage(ChatColor.RED + "No holograms were found");
			return false;
		}
		player.sendMessage(ChatColor.RED + "Holograms are not enabled!");
		return false;
	}

}
