package com.walrusone.skywarsreloaded.commands.party;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;

public class DisbandCmd extends BaseCmd { 
	
	public DisbandCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "disband";
		alias = new String[]{"dis"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		Party party = Party.getParty(player);
		if (party == null) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.notinaparty"));
			return false;
		}
		
		if (!party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.mustbepartyleader"));
			return false;
		}
		
		Party.removeParty(party);
		return true;
	}

}
