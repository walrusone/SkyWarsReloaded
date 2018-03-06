package com.walrusone.skywarsreloaded.game;

import java.util.ArrayList;

import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;

public class TeamCard {
	private ArrayList<PlayerCard> players = new ArrayList<PlayerCard>();
	private CoordLoc spawn;
	private GameMap gMap;
	
	public TeamCard(int size, CoordLoc spawn, GameMap gameMap) {
		this.spawn = spawn;
		this.gMap = gameMap;
		for (int i = 0; i < size; i++) {
			players.add(new PlayerCard());
		}
	}
	

}
