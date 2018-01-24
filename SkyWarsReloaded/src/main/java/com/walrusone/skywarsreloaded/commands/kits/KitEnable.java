package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitEnable extends BaseCmd { 
	
	public KitEnable() {
		forcePlayer = true;
		cmdName = "kitenable";
		alias = new String[]{"ke", "kite"};
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
		if (kit.getEnabled()) {
			kit.setEnabled(false);
			message = "disabled";
		} else {
			kit.setEnabled(true);
			message = "enabled";
		}
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", kit.getColorName()).setVariable("state", message).format("command.kit-enable"));
		return true;
	}

}
