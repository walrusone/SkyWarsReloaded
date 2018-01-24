package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitName extends BaseCmd { 
	
	public KitName() {
		forcePlayer = true;
		cmdName = "kitname";
		alias = new String[]{"kn", "kname"};
		argLength = 3; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit kit = GameKit.getKit(args[1]);
		if (kit == null) {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.no-kit"));
			return true;
		}
		
		String message = "";
		for (int i = 2; i < args.length; i++) {
		    message = message + args[i] + " ";
		}
		message = message.trim();
		
		kit.setName(message);
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).format("command.kit-name"));
		return true;
	}

}
