package com.walrusone.skywarsreloaded.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.utilities.Messaging;

public class Party {
	
	private static ArrayList<Party> parties = new ArrayList<Party>();
	private UUID leader;
	private ArrayList<UUID> members;

	public Party(Player player) {
		leader = player.getUniqueId();
		members = new ArrayList<UUID>();
		members.add(player.getUniqueId());
		parties.add(this);
	}
	
	public static void removeParty(Player player) {
		Party party = getParty(player);
		party.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.disbanded"));
		parties.remove(party);
	}
	
	public static Party getParty(Player player) {
		for (Party party: parties) {
			if (party.getMembers().contains(player.getUniqueId())) {
				return party;
			}
		}
		return null;
	}
	
	public boolean isLeader(Player player) {
		return player.getUniqueId().equals(this.leader);
	}
	
	public void addMember(Player player) {
		if (!members.contains(player.getUniqueId())) {
			members.add(player.getUniqueId());
			this.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.joined"));
		}
	}
	
	public void removeMember(Player player) {
		if (!members.contains(player.getUniqueId())) {
			members.remove(player.getUniqueId());
			this.sendPartyMessage(new Messaging.MessageFormatter().setVariable("player", player.getName()).format("party.left"));
		}
	}
	
	private void sendPartyMessage(String message) {
		for (UUID uuid: members) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}
	
	public UUID getLeader() {
		return leader;
	}
	
	public ArrayList<UUID> getMembers() {
		return members;
	}
	
	public int getSize() {
		return members.size();
	}
}
