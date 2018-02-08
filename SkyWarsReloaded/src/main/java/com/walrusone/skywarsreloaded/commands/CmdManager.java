package com.walrusone.skywarsreloaded.commands;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.skywarsreloaded.commands.admin.ChestAddCmd;
import com.walrusone.skywarsreloaded.commands.admin.ClearStatsCmd;
import com.walrusone.skywarsreloaded.commands.admin.ReloadCmd;
import com.walrusone.skywarsreloaded.commands.admin.SWUpdateTopCmd;
import com.walrusone.skywarsreloaded.commands.admin.SetSpawnCmd;
import com.walrusone.skywarsreloaded.commands.admin.SetStatsCmd;
import com.walrusone.skywarsreloaded.commands.admin.StartCmd;
import com.walrusone.skywarsreloaded.commands.kits.KitCreate;
import com.walrusone.skywarsreloaded.commands.kits.KitEnable;
import com.walrusone.skywarsreloaded.commands.kits.KitIcon;
import com.walrusone.skywarsreloaded.commands.kits.KitList;
import com.walrusone.skywarsreloaded.commands.kits.KitLoad;
import com.walrusone.skywarsreloaded.commands.kits.KitLockedIcon;
import com.walrusone.skywarsreloaded.commands.kits.KitLore;
import com.walrusone.skywarsreloaded.commands.kits.KitName;
import com.walrusone.skywarsreloaded.commands.kits.KitPermission;
import com.walrusone.skywarsreloaded.commands.kits.KitPosition;
import com.walrusone.skywarsreloaded.commands.kits.KitUpdate;
import com.walrusone.skywarsreloaded.commands.maps.CreateMapCmd;
import com.walrusone.skywarsreloaded.commands.maps.EditMapCmd;
import com.walrusone.skywarsreloaded.commands.maps.ListMapCmd;
import com.walrusone.skywarsreloaded.commands.maps.RefreshMapData;
import com.walrusone.skywarsreloaded.commands.maps.RegisterMapCmd;
import com.walrusone.skywarsreloaded.commands.maps.SaveMapCmd;
import com.walrusone.skywarsreloaded.commands.maps.UnregisterMapCmd;
import com.walrusone.skywarsreloaded.commands.player.SWJoinCmd;
import com.walrusone.skywarsreloaded.commands.player.SWQuitCmd;
import com.walrusone.skywarsreloaded.commands.player.SWStatsCmd;
import com.walrusone.skywarsreloaded.commands.player.SWTopCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class CmdManager implements CommandExecutor {
	private List<BaseCmd> mapcmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> kitcmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> admincmds = new ArrayList<BaseCmd>();
	private List<BaseCmd> pcmds = new ArrayList<BaseCmd>();

	//Add New Commands Here
	public CmdManager() {
		mapcmds.add(new ListMapCmd());
		mapcmds.add(new CreateMapCmd());
		mapcmds.add(new EditMapCmd());
		mapcmds.add(new RegisterMapCmd());
		mapcmds.add(new SaveMapCmd());
		mapcmds.add(new UnregisterMapCmd());
		mapcmds.add(new RefreshMapData());
		
		kitcmds.add(new KitCreate());
		kitcmds.add(new KitEnable());
		kitcmds.add(new KitIcon());
		kitcmds.add(new KitLockedIcon());
		kitcmds.add(new KitLoad());
		kitcmds.add(new KitLore());
		kitcmds.add(new KitName());
		kitcmds.add(new KitPosition());
		kitcmds.add(new KitPermission());
		kitcmds.add(new KitUpdate());
		kitcmds.add(new KitList());
		
		admincmds.add(new ReloadCmd());
		admincmds.add(new ChestAddCmd());
		admincmds.add(new SetStatsCmd());
		admincmds.add(new ClearStatsCmd());
		admincmds.add(new SetSpawnCmd());
		admincmds.add(new StartCmd());
		admincmds.add(new SWUpdateTopCmd());
		
		pcmds.add(new SWJoinCmd());
		pcmds.add(new SWQuitCmd());
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

