package com.walrusone.skywarsreloaded.commands.admin;

import org.bukkit.ChatColor;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.LeaderType;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class HoloAddCmd extends BaseCmd { 
	
	public HoloAddCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "hologram";
		alias = new String[]{"h"};
		argLength = 3; //counting cmdName
	}

	@Override
	public boolean run() {
		if (SkyWarsReloaded.getCfg().hologramsEnabled()) {
			LeaderType type = LeaderType.matchType(args[1].toUpperCase());
			if (type == null || !SkyWarsReloaded.getUseable().contains(type.toString())) {
				String types = "";
				for (String add: SkyWarsReloaded.getUseable()) {
					types = types + add + ", ";
				}
				types = types.substring(0, types.length() - 2);
				player.sendMessage(new Messaging.MessageFormatter().setVariable("validtypes", types).format("leaderboard.invalidtype"));
				return false;
			}
			String format = args[2];
			if (SkyWarsReloaded.getHoloManager().getFormats(type).contains(format)) {
				SkyWarsReloaded.getHoloManager().createLeaderHologram(player.getEyeLocation(), type, format);
				return true;
			}
			
			String types = "";
			for (String add: SkyWarsReloaded.getHoloManager().getFormats(type)) {
				types = types + add + ", ";
			}
			types = types.substring(0, types.length() - 2);
			player.sendMessage(new Messaging.MessageFormatter().setVariable("validtypes", types).format("leaderboard.invalidformat"));
			return false;
		}
		player.sendMessage(ChatColor.RED + "Holograms are not enabled!");
		return false;
	}

}
