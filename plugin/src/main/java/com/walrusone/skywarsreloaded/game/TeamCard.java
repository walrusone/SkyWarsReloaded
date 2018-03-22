package com.walrusone.skywarsreloaded.game;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;

public class TeamCard {
	private ArrayList<PlayerCard> players = new ArrayList<>();
    private ArrayList<UUID> dead = new ArrayList<>();
	private CoordLoc spawn;
	private GameMap gMap;
	private int place;
	private String prefix;
	private Team team;
	
	TeamCard(int size, CoordLoc spawn, GameMap gameMap, String prefix) {
		this.spawn = spawn;
		this.gMap = gameMap;
		this.place = 1;
		this.prefix = prefix;
		for (int i = 0; i < size; i++) {
			players.add(new PlayerCard(this, null, -1));
		}
	}

	public void updateCard(int size) {
		if (size >  players.size()) {
			for (int i = players.size(); i < size; i++) {
				players.add(new PlayerCard(this, null, -1));
			}
		} else {
			while (size < players.size()) {
				players.remove(players.size()-1);
			}
		}
	}
	
	public int getSize() {
		return players.size();
	}
	
	public int getFullCount() {
		int x = 0;
		for (PlayerCard pCard: players) {
			if (pCard.getUUID() == null) {
				x++;
			}
		}
		return x;
	}
	
	public int getPlace() {
		return this.place;
	}
	
	public void setPlace(int x) {
		this.place = x;
	}
	
	public CoordLoc getSpawn() {
		return this.spawn;
	}

	public GameMap getGameMap() {
		return gMap;
	}

	public ArrayList<PlayerCard> getPlayerCards() {
		return players;
	}

	public TeamCard sendReservation(Player player, PlayerStat ps) {
		if (player != null && ps != null && ps.isInitialized()) {
			for (PlayerCard pCard: players) {
				if (pCard.getUUID() == null && spawn != null) {
					pCard.setPlayer(player);
					pCard.setPreElo(ps.getElo());
					boolean glassReader = gMap.getCage().setGlassColor(gMap, this);
					if(glassReader) {
						return this;
					}
				}
			}
		}
		return null;
	}
	
	public boolean joinGame(Player player) {
		for (PlayerCard pCard: players) {
			if (pCard.getUUID().equals(player.getUniqueId())) {
				team.addEntry(player.getName());
				gMap.getJoinQueue().add(pCard);
				if (SkyWarsReloaded.getCfg().kitVotingEnabled()) {
					gMap.getKitVoteOption().updateKitVotes();
				}
				gMap.setTimer(SkyWarsReloaded.getCfg().getWaitTimer());
				return true;
			}
		}
		return false;
	}

	public boolean removePlayer(UUID uuid) {
		PlayerCard pCard = containsPlayer(uuid);
		if (pCard != null && team != null) {
			team.removeEntry(SkyWarsReloaded.get().getServer().getOfflinePlayer(uuid).getName());
			pCard.reset();
			return true;
		}
		return false;
	}

	public void reset() {
		this.place = 1;
		for (PlayerCard pCard: players) {
			pCard.reset();
		}
		this.dead.clear();
	}

	public ArrayList<UUID> getDead() {
		return dead;
	}

	public PlayerCard containsPlayer(UUID uuid) {
		for (PlayerCard pCard: players) {
    		if (uuid != null) {
    			if (pCard.getUUID() != null) {
    				if(pCard.getUUID().equals(uuid)) {
            			return pCard;
            		}
    			}
    		}
    	}
		return null;
	}
	
	public boolean isFull() {
		return (getFullCount() == 0);
	}

	public int getPlayersSize() {
		int count = 0;
		for (PlayerCard pCard: players) {
			if (pCard.getUUID() != null) {
				count++;
			}
		}
		return count;
	}

	public boolean isElmininated() {
		int num = getPlayersSize();
		return (num == 0 || num == dead.size());
	}

	public String getName() {
		StringBuilder name = new StringBuilder();
		for (PlayerCard pCard: players) {
			if (pCard.getPlayer() != null) {
				name.append(pCard.getPlayer().getDisplayName());
				name.append(", ");
			}
		}
		return name.substring(0, name.length() -2);
	}

	public String getPrefix() {
		return prefix;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}

