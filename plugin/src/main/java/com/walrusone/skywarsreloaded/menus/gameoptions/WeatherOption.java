package com.walrusone.skywarsreloaded.menus.gameoptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.walrusone.skywarsreloaded.enums.ScoreVar;
import org.bukkit.Chunk;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class WeatherOption extends GameOption {

	public WeatherOption(GameMap gameMap, String key) {
		itemList = new ArrayList<>(Arrays.asList("weatherrandom", "weathersunny", "weatherrain", "weatherstorm", "weathersnow"));
		voteList = new ArrayList<>(Arrays.asList(Vote.WEATHERRANDOM, Vote.WEATHERSUN, Vote.WEATHERRAIN, Vote.WEATHERTHUNDER, Vote.WEATHERSNOW));
		createMenu(key, new Messaging.MessageFormatter().format("menu.weather-voting-menu"));	
		this.gameMap = gameMap;
	}
	
	@Override
	protected void doSlotNine(Player player) {
		Vote cVote = Vote.WEATHERRANDOM;
		String type = new Messaging.MessageFormatter().format("items.weather-random");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotEleven(Player player) {
		Vote cVote = Vote.WEATHERSUN;
		String type = new Messaging.MessageFormatter().format("items.weather-sunny");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotThriteen(Player player) {
		Vote cVote = Vote.WEATHERRAIN;
		String type = new Messaging.MessageFormatter().format("items.weather-rain");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotFifteen(Player player) {
		Vote cVote = Vote.WEATHERTHUNDER;
		String type = new Messaging.MessageFormatter().format("items.weather-storm");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotSeventeen(Player player) {
		Vote cVote = Vote.WEATHERSNOW;
		String type = new Messaging.MessageFormatter().format("items.weather-snow");
		finishEvent(player, cVote, type);
	}
	
	private void finishEvent(Player player, Vote vote, String type) {
		if (vote != null) {
			setVote(player, vote);
			updateVotes();
			Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getConfirmeSelctionSound(), 1, 1);
			if (gameMap.getMatchState().equals(MatchState.WAITINGSTART)) {
				new VotingMenu(player);
			}
			MatchManager.get().message(gameMap, new Messaging.MessageFormatter()
					.setVariable("player", player.getName())
					.setVariable("weather", type).format("game.voteweather"));
		}
	}

	@Override
	public void setCard(PlayerCard pCard, Vote vote) {
		pCard.setWeather(vote);
	}

	@Override
	public Vote getVote(PlayerCard pCard) {
		return pCard.getVote("weather");
	}

	@Override
	public Vote getRandomVote() {
		return Vote.getRandom("weather");
	}
	
	@Override
	protected void updateScoreboard() {
		gameMap.setCurrentWeather(getVoteString(getVoted()));
		gameMap.getGameBoard().updateScoreboardVar(ScoreVar.WEATHERVOTE);
	}

	@Override
	protected Vote getDefault() {
		return Vote.WEATHERSUN;	
	}

	@SuppressWarnings("deprecation")
	@Override
	public void completeOption() {
		Vote weather = gameMap.getWeatherOption().getVoted();
		WeatherType w = WeatherType.CLEAR;
		if (weather != Vote.WEATHERSUN) {
			w = WeatherType.DOWNFALL;
		} 
		if (weather == Vote.WEATHERTHUNDER) {
			gameMap.setThunderStorm(true);
			gameMap.setNextStrike(Util.get().getRandomNum(3, 20));
			gameMap.setStrikeCounter(0);
		} else if (weather == Vote.WEATHERSNOW) {
			World world = gameMap.getAlivePlayers().get(0).getWorld();
			for (int x = -200; x < 200; x++) {
				for (int z = -200; z < 200; z++) {
					world.setBiome(x, z, Biome.ICE_MOUNTAINS);
				}
			}
			List<Chunk> chunks = Util.get().getChunks(world);
			for (Chunk chunk: chunks) {
				world.refreshChunk(chunk.getX(), chunk.getZ());
			}
		}
		for (Player player: gameMap.getAllPlayers()) {
			player.setPlayerWeather(w);
		}
	}

}
