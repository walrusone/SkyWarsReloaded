package com.walrusone.skywarsreloaded.commands.kits;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class KitLockedIcon extends BaseCmd { 
	
	public KitLockedIcon() {
		forcePlayer = true;
		cmdName = "kitlockicon";
		alias = new String[]{"kli", "kitli"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit kit = GameKit.getKit(args[1]);
		if (kit == null) {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.no-kit"));
			return true;
		}
		kit.setLIcon(SkyWarsReloaded.getNMS().getMainHandItem(player).clone());
		
		GameKit.saveKit(kit);
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("icon", kit.getIcon().getType().toString()).format("command.kit-locked-icon"));
		return true;
	}

}
