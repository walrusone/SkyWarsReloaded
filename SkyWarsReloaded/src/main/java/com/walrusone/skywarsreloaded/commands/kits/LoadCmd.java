package com.walrusone.skywarsreloaded.commands.kits;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class LoadCmd extends BaseCmd { 
	
	public LoadCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "load";
		alias = new String[]{"lo"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		GameKit kit = GameKit.getKit(args[1]);
		if (kit == null) {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.no-kit"));
			return true;
		}
		GameKit.giveKit(player, kit);
		player.sendMessage(new Messaging.MessageFormatter().setVariable("kit", args[1]).format("command.kit-load"));
		player.sendMessage(new Messaging.MessageFormatter().format("command.kit-loadmsg"));
		return true;
	}

}
