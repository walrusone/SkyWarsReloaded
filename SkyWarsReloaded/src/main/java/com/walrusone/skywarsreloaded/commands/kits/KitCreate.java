package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitCreate extends BaseCmd { 
	
	public KitCreate() {
		forcePlayer = true;
		cmdName = "kitcreate";
		alias = new String[]{"kc", "kitc"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit.newKit(player, args[1]);
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.kit-create"));
		return true;
	}

}
