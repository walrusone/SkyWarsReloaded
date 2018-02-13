package com.walrusone.skywarsreloaded.commands;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class CmdManager implements CommandExecutor {
	private List<BaseCmd> mapcmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> kitcmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> admincmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> pcmds = new ArrayList<BaseCmd>();

	//Add New Commands Here
	public CmdManager() {	
		admincmds.add(new ReloadCmd());
		admincmds.add(new SetStatsCmd());
		admincmds.add(new ClearStatsCmd());
		admincmds.add(new SetSpawnCmd());
		admincmds.add(new SWUpdateTopCmd());
		
		pcmds.add(new SWJoinCmd());
		pcmds.add(new SWStatsCmd());
		pcmds.add(new SWTopCmd());

	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) { 
		if (args.length == 0 || getCommands(args[0]) == null) {
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.header"));
			sendHelp(mapcmds, s, "2");
			sendHelp(kitcmds, s, "3");
			sendHelp(admincmds, s, "4");
			sendHelp(pcmds, s, "5");
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.footer"));
		} else getCommands(args[0]).processCmd(s, args);
		return true;
	}
	
	private void sendHelp(List<BaseCmd> cmds, CommandSender s, String num) {
		int count = 0;
		for (BaseCmd cmd : cmds) {
			if (Util.get().hp(s, cmd.cmdName)) {
				count++;
				if (count == 1) {
					s.sendMessage(" ");
					s.sendMessage(new Messaging.MessageFormatter().format("helpList.header" + num));
				}
				s.sendMessage(new Messaging.MessageFormatter().format("helpList." + cmd.cmdName));
			}
		}
	}

	private BaseCmd getCommands(String s) {
		BaseCmd cmd = null;
		cmd = getCmd(mapcmds, s);
		if (cmd == null) cmd = getCmd(kitcmds, s);
		if (cmd == null) cmd = getCmd(admincmds, s);
		if (cmd == null) cmd = getCmd(pcmds, s);
		return cmd;
	}

	private BaseCmd getCmd(List<BaseCmd> cmds, String s) {
		for (BaseCmd cmd : cmds) {
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

