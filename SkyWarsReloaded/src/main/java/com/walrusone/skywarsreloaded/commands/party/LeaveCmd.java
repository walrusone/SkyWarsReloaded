package com.walrusone.skywarsreloaded.commands.party;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;

public class LeaveCmd extends BaseCmd { 
	
	public LeaveCmd(String t) {
		type = t;
		forcePlayer = true;
		cmdName = "leave";
		alias = new String[]{"l"};
		argLength = 1; //counting cmdName
	}

	@Override
	public boolean run() {
		Party party = Party.getParty(player);
		if (party == null) {
			player.sendMessage(new Messaging.MessageFormatter().format("party.notinaparty"));
			return false;
		}
		
		party.removeMember(player);
		player.sendMessage(new Messaging.MessageFormatter().setVariable("partyname", party.getPartyName()).format("party.youlefttheparty"));
		return true;
	}

}
