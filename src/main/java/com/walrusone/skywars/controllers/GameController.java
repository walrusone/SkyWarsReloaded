package com.walrusone.skywars.controllers;

import java.util.concurrent.CopyOnWriteArrayList;

import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;

public class GameController {

private CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<Game>();

	int gameNumber = 0;

	public Game findGame() {
		for (int i = 0; i < games.size(); i++) {
			if (!games.get(i).isFull() && (games.get(i).getState() == GameState.WAITING || games.get(i).getState() == GameState.INLOBBY)) {
				return games.get(i);
			}
		}
		return createGame();
	}
	
	public Game createGame() {
		gameNumber++;
		Game game = new Game(gameNumber);
		games.add(game);
		return game;
	}
	
	public void deleteGame(Game game) {
		if (game.getState() == GameState.ENDING || game.getMap() != null) {
			game.deleteMap();
		} 
		games.remove(game);
	}
	
	 public void shutdown() {
		 for (Game game : games) {
			 game.endGame();
	     }
	 }

	 public CopyOnWriteArrayList<Game> getGames() {
	        return games;
	 }
	 
	 public boolean inGame(GamePlayer gPlayer) {
		 for (Game game: games) {
			 for (GamePlayer gamePlayer: game.getPlayers()) {
				 if (gamePlayer == gPlayer) {
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	
}
