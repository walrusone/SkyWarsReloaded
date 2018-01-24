package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitPermission extends BaseCmd { 
	
	public KitPermission() {
		forcePlayer = true;
		cmdName = "kitperm";
		alias = new String[]{"kp", "kperm"};
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
		if (kit.needPermission()) {
			kit.setNeedPermission(false);
			message = "no longer";
		} else {
			kit.setNeedPermission(true);
			message = "now";
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).setVariable("state", message).format("command.kit-perm"));
		return true;
	}

}
