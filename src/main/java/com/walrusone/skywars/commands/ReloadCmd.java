package com.walrusone.skywars.commands;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class ReloadCmd extends BaseCmd { 
	
	public ReloadCmd() {
		forcePlayer = false;
		cmdName = "reload";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Reloads the config, chest.yml and kits";

	}

	@Override
	public boolean run() {
		SkyWarsReloaded.get().reload();
		sender.sendMessage(new Messaging.MessageFormatter().withPrefix().format("command.reload"));
		return true;
	}

}
