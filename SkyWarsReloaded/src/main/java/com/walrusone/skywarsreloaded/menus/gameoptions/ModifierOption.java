package com.walrusone.skywarsreloaded.menus.gameoptions;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public class ModifierOption extends GameOption {

	public ModifierOption(GameMap gameMap, String key) {
		itemList = new ArrayList<>(Arrays.asList("modifierrandom", "modifierspeed", "modifierjump", "modifierstrength", "modifiernone"));
		voteList = new ArrayList<>(Arrays.asList(Vote.MODIFIERRANDOM, Vote.MODIFIERSPEED, Vote.MODIFIERJUMP, Vote.MODIFIERSTRENGTH, Vote.MODIFIERNONE));
		createMenu(key, new Messaging.MessageFormatter().format("menu.modifier-voting-menu"));	
		this.gameMap = gameMap;
	}
	
	@Override
	protected void doSlotNine(Player player) {
		Vote cVote = Vote.MODIFIERRANDOM;;
		String type = new Messaging.MessageFormatter().format("items.modifier-random");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotEleven(Player player) {
		Vote cVote = Vote.MODIFIERSPEED;
		String type = new Messaging.MessageFormatter().format("items.modifier-speed");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotThriteen(Player player) {
		Vote cVote = Vote.MODIFIERJUMP;
		String type = new Messaging.MessageFormatter().format("items.modifier-jump");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotFifteen(Player player) {
		Vote cVote = Vote.MODIFIERSTRENGTH;
		String type = new Messaging.MessageFormatter().format("items.modifier-strength");
		finishEvent(player, cVote, type);
	}

	@Override
	protected void doSlotSeventeen(Player player) {
		Vote cVote = Vote.MODIFIERNONE;
		String type = new Messaging.MessageFormatter().format("items.modifier-none");
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
					.setVariable("mod", type).format("game.votemodifier"));
		}
	}

	@Override
	public void setCard(PlayerCard pCard, Vote vote) {
		pCard.setModifier(vote);
	}

	@Override
	public Vote getVote(PlayerCard pCard) {
		return pCard.getVote("modifier");
	}

	@Override
	public Vote getRandomVote() {
		return Vote.getRandom("modifier");
	}
	
	@Override
	protected void updateScoreboard() {
		gameMap.setCurrentModifier(getVoteString(getVoted()));
		gameMap.updateScoreboard();
	}

	@Override
	protected Vote getDefault() {
		return Vote.MODIFIERNONE;	
	}

	@Override
	public void completeOption() {
		Vote modifier = gameMap.getModifierOption().getVoted();
		if (modifier == Vote.MODIFIERSPEED) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getSpeed(), true, false));
	    	}
		} else if (modifier ==  Vote.MODIFIERJUMP) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getJump(), true, false));
	    	}
		} else if (modifier ==  Vote.MODIFIERSTRENGTH) {
	    	for (Player player: gameMap.getAlivePlayers()) {
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, SkyWarsReloaded.getCfg().getStrength(), true, false));
	    	}
		} 
	}

}
