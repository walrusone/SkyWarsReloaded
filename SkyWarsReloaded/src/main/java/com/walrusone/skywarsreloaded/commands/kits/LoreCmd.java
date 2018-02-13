package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class LoreCmd extends BaseCmd { 
	
	public LoreCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "lore";
		alias = new String[]{"l"};
		argLength = 4; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit kit = GameKit.getKit(args[1]);
		if (kit == null) {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.no-kit"));
			return true;
		}
		String message = "";
		for (int i = 3; i < args.length; i++) {
		    message = message + args[i] + " ";
		}
		message = message.trim();
		
		if (Util.get().isInteger(args[2])) {
			kit.setLoreLine(Integer.valueOf(args[2]), message);
		} else if (args[2].equalsIgnoreCase("locked")) {
			kit.setLockedLore(args[2]);
		} else {
			player.sendMessage(new Messaging.MessageFormatter().format("command.kit-loreerror"));
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("line", args[2]).setVariable("kit", kit.getColorName()).format("command.kit-lore"));
		return true;
	}

}
