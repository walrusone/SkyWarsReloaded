package com.walrusone.skywarsreloaded.commands;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class CmdManager implements CommandExecutor {
	private List<BaseCmd> swcmds = new ArrayList<BaseCmd>();

	//Add New Commands Here
	public CmdManager() {		
		swcmds.add(new ReloadCmd());
		swcmds.add(new SetStatsCmd());
		swcmds.add(new ClearStatsCmd());
		swcmds.add(new SWJoinCmd());
		swcmds.add(new SWStatsCmd());
		swcmds.add(new SWTopCmd());
		swcmds.add(new SetSpawnCmd());
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) { 
		if (args.length == 0 || getCommands(args[0]) == null) {
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.header"));
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.invite"));
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.wager"));
			for (BaseCmd cmd : swcmds) {
				if (Util.get().hp(s, cmd.cmdName)) {
					s.sendMessage(new Messaging.MessageFormatter().format("helpList." + cmd.cmdName));
				}
			}
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.footer"));
		} else getCommands(args[0]).processCmd(s, args);
		return true;
	}

	private BaseCmd getCommands(String s) {
		for (BaseCmd cmd : swcmds) {
			if (cmd.cmdName.equalsIgnoreCase(s)) {
				return cmd;
			}
			for (String alias: cmd.alias) {
				if (alias.equalsIgnoreCase(s))
					return cmd;
			}
		}
		return null;
	}
}

