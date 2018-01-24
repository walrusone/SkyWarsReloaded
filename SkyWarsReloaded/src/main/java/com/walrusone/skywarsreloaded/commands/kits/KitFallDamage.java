package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitFallDamage extends BaseCmd { 
	
	public KitFallDamage() {
		forcePlayer = true;
		cmdName = "kitfall";
		alias = new String[]{"kf", "kitf"};
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
		if (kit.getSettings().get("nofalldamage")) {
			kit.getSettings().put("nofalldamage", (false));
			message = "enabled";
		} else {
			kit.getSettings().put("nofalldamage", (true));
			message = "disabled";
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).setVariable("state", message).format("command.kit-fall"));
		return true;
	}

}
