package com.walrusone.skywarsreloaded.commands.party;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.objects.Party;
import com.walrusone.skywarsreloaded.utilities.Messaging;

public class InfoCmd extends BaseCmd { 
	
	public InfoCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "info";
		alias = new String[]{"in"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		Party party = Party.getParty(player);
		if (party == null) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.notinaparty"));
			return false;
		}
		
		player.sendMessage(new Messaging.MessageFormatter().setVariable("partyname", party.getPartyName()).format("party.info1"));
		player.sendMessage(new Messaging.MessageFormatter().setVariable("leader", Bukkit.getPlayer(party.getLeader()).getName()).format("party.info2"));
		String members = "";
		for (UUID uuid: party.getMembers()) {
			members = members + Bukkit.getPlayer(uuid).getName() + ", ";
		}
		members = members.substring(0, members.length()-2);
		player.sendMessage(new Messaging.MessageFormatter().setVariable("members", members).format("party.info3"));
		return true;
	}

}
