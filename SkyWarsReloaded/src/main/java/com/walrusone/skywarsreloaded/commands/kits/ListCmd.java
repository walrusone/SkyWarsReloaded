package com.walrusone.skywarsreloaded.commands.kits;

import org.bukkit.ChatColor;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.GameKit;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class ListCmd extends BaseCmd { 
	
	public ListCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "list";
		alias = new String[]{"li"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		if (GameKit.getKits().size() < 1) {
			player.sendMessage(new Messaging.MessageFormatter().format("command.kit-listno"));
			return true;
		}
		player.sendMessage(new Messaging.MessageFormatter().format("command.kit-listheader"));
		player.sendMessage(new Messaging.MessageFormatter().format("command.kit-listheader2"));
		for (GameKit kit: GameKit.getKits()) {
			if (!kit.getName().equalsIgnoreCase(new Messaging.MessageFormatter().format("kit.vote-random")) &&
					!kit.getName().equalsIgnoreCase(new Messaging.MessageFormatter().format("kit.vote-nokit"))) {
				String message;
				if (kit.getEnabled()) {
					message = ChatColor.GREEN + "enabled";
				} else {
					message = ChatColor.RED + "disabled";
				}
				player.sendMessage(new Messaging.MessageFormatter().setVariable("filename", kit.getFilename())
						.setVariable("position", "" + kit.getPosition()).setVariable("status", message).format("command.kit-list"));
			}

		}
		return true;
	}

}
