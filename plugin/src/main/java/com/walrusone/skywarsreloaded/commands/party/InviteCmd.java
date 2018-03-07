package com.walrusone.skywarsreloaded.commands.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;

public class InviteCmd extends BaseCmd { 
	
	public InviteCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "invite";
		alias = new String[]{"i"};
		argLength = 2; //counting cmdName
	}

	@Override
	public boolean run() {
		Party party = Party.getParty(player);
		if (party == null) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.mustcreate"));
			return false;
		}
		
		if (!party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.mustbepartyleader"));
			return false;
		}
		
		String invite = args[1];
		Player invited = null;
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(invite)) {
				invited = p;
			}
		}
		
		if (invited != null) {
			Party inv = Party.getPartyOfInvite(invited);
			if (inv != null) {
				player.sendMessage(new Messaging.MessageFormatter().setVariable("player", invited.getName()).format("party.pendingInvite"));
				return true;
			}
			party.invite(invited);
			invited.sendMessage(new Messaging.MessageFormatter().setVariable("leader", player.getName()).setVariable("partyname", party.getPartyName()).format("party.invite"));
			player.sendMessage(new Messaging.MessageFormatter().setVariable("player", invited.getName()).format("party.invited"));
		} else {
			player.sendMessage(new Messaging.MessageFormatter().setVariable("player",  invite).format("party.couldnotfind"));
		}
		return true;
	}

}
