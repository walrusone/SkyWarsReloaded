package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitSoup extends BaseCmd { 
	
	public KitSoup() {
		forcePlayer = true;
		cmdName = "kitsoup";
		alias = new String[]{"ks", "ksoup"};
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
		if (kit.getSettings().get("souppvp")) {
			kit.getSettings().put("souppvp", (false));
			message = "disabled";
		} else {
			kit.getSettings().put("souppvp", (true));
			message = "enabled";
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).setVariable("state", message).format("command.kit-soup"));
		return true;
	}

}
