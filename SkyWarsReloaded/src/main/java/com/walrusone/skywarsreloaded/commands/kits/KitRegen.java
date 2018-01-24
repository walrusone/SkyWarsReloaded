package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitRegen extends BaseCmd { 
	
	public KitRegen() {
		forcePlayer = true;
		cmdName = "kitregen";
		alias = new String[]{"kr", "kitr"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit kit = GameKit.getKit(args[1]);
		if (kit == null) {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.no-kit"));
			return true;
		}
		String message;
		if (kit.getSettings().get("noregen")) {
			kit.getSettings().put("noregen", (false));
			message = "enabled";
		} else {
			kit.getSettings().put("noregen", (true));
			message = "disabled";
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).setVariable("state", message).format("command.kit-regen"));
		return true;
	}

}
