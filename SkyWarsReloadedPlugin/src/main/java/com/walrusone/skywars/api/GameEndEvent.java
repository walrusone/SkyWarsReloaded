package com.walrusone.skywars.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event{

	private Player winner;
	private String mapName;
	
	public GameEndEvent(Player winner, String mapName) {
		this.winner = winner;
		this.mapName = mapName;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public String getMap() {
		return mapName;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
