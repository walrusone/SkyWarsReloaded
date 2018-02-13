package com.walrusone.skywarsreloaded.commands;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class ReloadCmd extends BaseCmd { 
	
	public ReloadCmd() {
		forcePlayer = false;
		cmdName = "reload";
		alias = new String[]{"r"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
        SkyWarsReloaded.get().onDisable();
        SkyWarsReloaded.get().load();
        sender.sendMessage(new Messaging.MessageFormatter().format("command.reload"));
		return true;
	}

}
