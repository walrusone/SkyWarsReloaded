package com.walrusone.skywarsreloaded.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.admin.ChestAddCmd;
import com.walrusone.skywarsreloaded.commands.admin.ClearStatsCmd;
import com.walrusone.skywarsreloaded.commands.admin.HoloAddCmd;
import com.walrusone.skywarsreloaded.commands.admin.HoloRemoveCmd;
import com.walrusone.skywarsreloaded.commands.admin.ReloadCmd;
import com.walrusone.skywarsreloaded.commands.admin.UpdateTopCmd;
import com.walrusone.skywarsreloaded.commands.admin.SetSpawnCmd;
import com.walrusone.skywarsreloaded.commands.admin.SetStatsCmd;
import com.walrusone.skywarsreloaded.commands.admin.StartCmd;
import com.walrusone.skywarsreloaded.commands.player.SWGlassCmd;
import com.walrusone.skywarsreloaded.commands.player.SWJoinCmd;
import com.walrusone.skywarsreloaded.commands.player.SWKillsoundCmd;
import com.walrusone.skywarsreloaded.commands.player.SWParticleCmd;
import com.walrusone.skywarsreloaded.commands.player.SWProjectileCmd;
import com.walrusone.skywarsreloaded.commands.player.SWQuitCmd;
import com.walrusone.skywarsreloaded.commands.player.SWStatsCmd;
import com.walrusone.skywarsreloaded.commands.player.SWTauntCmd;
import com.walrusone.skywarsreloaded.commands.player.SWTopCmd;
import com.walrusone.skywarsreloaded.commands.player.SWWinsoundCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class CmdManager implements CommandExecutor {
	private List<BaseCmd> admincmds = new ArrayList<>();
	private List<BaseCmd> pcmds = new ArrayList<>();

	public CmdManager() {
		admincmds.add(new ReloadCmd("sw"));
		admincmds.add(new ChestAddCmd("sw"));
		admincmds.add(new SetStatsCmd("sw"));
		admincmds.add(new ClearStatsCmd("sw"));
		admincmds.add(new SetSpawnCmd("sw"));
		admincmds.add(new StartCmd("sw"));
		admincmds.add(new UpdateTopCmd("sw"));
		admincmds.add(new HoloAddCmd("sw"));
		admincmds.add(new HoloRemoveCmd("sw"));
		
		pcmds.add(new SWJoinCmd("sw"));
		pcmds.add(new SWQuitCmd("sw"));
		pcmds.add(new SWStatsCmd("sw"));
		pcmds.add(new SWTopCmd("sw"));
		if (SkyWarsReloaded.getCfg().winsoundMenuEnabled()) {
			pcmds.add(new SWWinsoundCmd("sw"));
		}
		if (SkyWarsReloaded.getCfg().killsoundMenuEnabled()) {
			pcmds.add(new SWKillsoundCmd("sw"));
		}
		if (SkyWarsReloaded.getCfg().tauntsMenuEnabled()) {
			pcmds.add(new SWTauntCmd("sw"));
		}
		if (SkyWarsReloaded.getCfg().projectileMenuEnabled()) {
			pcmds.add(new SWProjectileCmd("sw"));
		}
		if (SkyWarsReloaded.getCfg().particleMenuEnabled()) {
			pcmds.add(new SWParticleCmd("sw"));
		}
		if (SkyWarsReloaded.getCfg().glassMenuEnabled()) {
			pcmds.add(new SWGlassCmd("sw"));
		}

	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) { 
		if (args.length == 0 || getCommands(args[0]) == null) {
			s.sendMessage(new Messaging.MessageFormatter().format("helpList.header"));
			sendHelp(admincmds, s, "1");
			sendHelp(pcmds, s, "2");
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
					s.sendMessage(new Messaging.MessageFormatter().format("helpList.sw.header" + num));
				}
				s.sendMessage(new Messaging.MessageFormatter().format("helpList.sw." + cmd.cmdName));
			}
		}
	}

	private BaseCmd getCommands(String s) {
		BaseCmd cmd;
		cmd = getCmd(admincmds, s);
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

