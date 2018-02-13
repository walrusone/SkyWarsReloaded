package com.walrusone.skywarsreloaded.commands;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.skywarsreloaded.commands.maps.ArenaCmd;
import com.walrusone.skywarsreloaded.commands.maps.CreateCmd;
import com.walrusone.skywarsreloaded.commands.maps.CreatorCmd;
import com.walrusone.skywarsreloaded.commands.maps.DeleteCmd;
import com.walrusone.skywarsreloaded.commands.maps.EditCmd;
import com.walrusone.skywarsreloaded.commands.maps.ListCmd;
import com.walrusone.skywarsreloaded.commands.maps.MinimumCmd;
import com.walrusone.skywarsreloaded.commands.maps.NameCmd;
import com.walrusone.skywarsreloaded.commands.maps.RefreshData;
import com.walrusone.skywarsreloaded.commands.maps.RegisterCmd;
import com.walrusone.skywarsreloaded.commands.maps.SaveCmd;
import com.walrusone.skywarsreloaded.commands.maps.UnregisterCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class MapCmdManager implements CommandExecutor {
	private List<BaseCmd> mapcmds = new ArrayList<BaseCmd>();

	//Add New Commands Here
	public MapCmdManager() {
		mapcmds.add(new ListCmd("map"));
		mapcmds.add(new CreateCmd("map"));
		mapcmds.add(new EditCmd("map"));
		mapcmds.add(new RegisterCmd("map"));
		mapcmds.add(new SaveCmd("map"));
		mapcmds.add(new UnregisterCmd("map"));
		mapcmds.add(new RefreshData("map"));
		mapcmds.add(new NameCmd("map"));
		mapcmds.add(new DeleteCmd("map"));
		mapcmds.add(new MinimumCmd("map"));
		mapcmds.add(new CreatorCmd("map"));
		mapcmds.add(new ArenaCmd("map"));
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) { 
		if (args.length == 0 || getCommands(args[0]) == null) {
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.header"));
			sendHelp(mapcmds, s, "1");
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.footer"));
		} else getCommands(args[0]).processCmd(s, args);
		return true;
	}
	
	private void sendHelp(List<BaseCmd> cmds, CommandSender s, String num) {
		int count = 0;
		for (BaseCmd cmd : cmds) {
			if (Util.get().hp(cmd.getType(), s, cmd.cmdName)) {
				count++;
				if (count == 1) {
					s.sendMessage(" ");
					s.sendMessage(new Messaging.MessageFormatter().format("helpList.swmap.header" + num));
				}
				s.sendMessage(new Messaging.MessageFormatter().format("helpList.swmap." + cmd.cmdName));
			}
		}
	}

	private BaseCmd getCommands(String s) {
		BaseCmd cmd = null;
		cmd = getCmd(mapcmds, s);
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

