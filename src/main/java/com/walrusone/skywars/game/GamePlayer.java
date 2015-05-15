package com.walrusone.skywars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.Tagged;
public class GamePlayer {

	private UUID uuid;
	private boolean isSpectating = false;
	private String name;
	private String color;
	private String effect;
	private String projEffect;
	private int wins;
	private int kills;
	private int deaths;
	private int gamesPlayed;
	private int score;
	private int blocksPlaced;
	private int balance;
	private boolean hasKitSelected;
	private GameKit selectedKit;
	private Tagged taggedBy;
	private boolean inGame = false;
	private int game = -1;
	private int specGame = -1;
	private int votedForOP = 0;
	private int timeVote = 0;
	private int jumpVote = 0;
	private int weatherVote = 0;
	private ItemStack exit;
	private ItemStack specShopItem;
	private ItemStack spec;
	private List<String> permissions = new ArrayList<String>();
	private List<String> newPerms = new ArrayList<String>();
	
	public GamePlayer (UUID uuid) {
		this.uuid = uuid;
		SkyWarsReloaded.getDS().loadPlayer(this);
		name = getP().getName();
		setTagged(this);
		String exitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.exitGameItem");
		List<String> exitItemData = new LinkedList<String>(Arrays.asList(exitItem.split(" ")));
		String exitName = "name:" + new Messaging.MessageFormatter().format("menu.returntospawn-item-name");
		exitItemData.add(exitName);
		exit = ItemUtils.parseItem(exitItemData);
		String specShItem = SkyWarsReloaded.get().getConfig().getString("gameItems.specStoreItem");
		List<String> specShopItemData = new LinkedList<String>(Arrays.asList(specShItem.split(" ")));
		String specShopName = "name:" + new Messaging.MessageFormatter().format("menu.spectateshop-item-name");
		specShopItemData.add(specShopName);
		specShopItem = ItemUtils.parseItem(specShopItemData);
		String specItem = SkyWarsReloaded.get().getConfig().getString("gameItems.spectateItem");
		List<String> specItemData = new LinkedList<String>(Arrays.asList(specItem.split(" ")));
		String name = "name:" + new Messaging.MessageFormatter().format("menu.spectate-item-name");
		specItemData.add(name);
		spec = ItemUtils.parseItem(specItemData);
	}
	
	public Player getP() {
		return SkyWarsReloaded.get().getServer().getPlayer(uuid);
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public GamePlayer getGamePlayer() {
		return this;
	}
	
	public void setBalance(int x) {
		balance = x;
	}
	
	public int getBalance() {
		return balance;
	}
	public String getName() {
		if (getP() != null) {
			name = getP().getName();
			return name;
		} else {
			return name;
		}
	}
	
	public void setWins(int w) {
		wins = w;
	}
	
	public void setKills(int k) {
		kills = k;
	}
	
	public void setDeaths(int d) {
		deaths = d;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getKills() {
		return kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void setSpectating(boolean b) {
		isSpectating = b;
	}
	
	public boolean isSpectating() {
		return isSpectating;
	}

	public void setTagged(GamePlayer player) {
		taggedBy = new Tagged(player, System.currentTimeMillis());
	}
	
	public Tagged getTagged() {
		return taggedBy;
	}
	
	public void setGame(int game) {
		this.game = game;
	}
	
	public Game getGame() {
		return SkyWarsReloaded.getGC().getGame(game);
	}
	
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void setGamesPlayed(int gp) {
		gamesPlayed = gp;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int s) {
		score = s;
	}

	public void setKitSelected(boolean b) {
		hasKitSelected = b;
	}
	
	public boolean hasKitSelected() {
		return hasKitSelected;
	}
	
	public GameKit getSelectedKit() {
		return selectedKit;
	}
	
	public void setSelectedKit(GameKit kit) {
		selectedKit = kit;
	}
	
	public int getBlocks() {
		return blocksPlaced;
	}
	
	public void setBlocks(int s) {
		blocksPlaced = s;
	}

	public void setSpecGame(int game) {
		specGame = game;
	}
	
	public Game getSpecGame() {
		return SkyWarsReloaded.getGC().getGame(specGame);
	}
	
	public void setInGame(boolean state) {
		inGame = state;
	}
	
	public boolean inGame() {
		return inGame;
	}

	public void spectateMode(boolean state, Game game, Location location, boolean shutdown) {
		if (state) {
			if (getP() != null) {
				setSpectating(state);
				game.addSpectator(this);
				setSpecGame(game.getGameNumber());
				for (Player target: SkyWarsReloaded.get().getServer().getOnlinePlayers()) {
					target.hidePlayer(getP());
				}
				getP().setGameMode(GameMode.ADVENTURE);
				getP().setFoodLevel(20);
				getP().setScoreboard(game.getScoreboard());
				getP().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
				getP().teleport(location);
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					@Override
					public void run() {
						if (getP() != null) {
							getP().setAllowFlight(true);
							getP().setFlying(true);
							getP().getInventory().clear();
							giveSpectateItems();
						}
					}
				}, 5);

			}
		} else {
			if (getP() != null) {
				setSpectating(state);
				setSpecGame(-1);
				for (Player target: SkyWarsReloaded.get().getServer().getOnlinePlayers()) {
					if (getP() != null) {
						target.showPlayer(getP());
					}
				}
				getP().setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
				for (PotionEffect effect : getP().getActivePotionEffects()) {
			        getP().removePotionEffect(effect.getType());
				}
				getP().setFireTicks(0);
				getP().teleport(location);
				if (!shutdown) {
					SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
						@Override
						public void run() {
							if (getP() != null) {
								getP().setAllowFlight(false);
								getP().setFlying(false);
								SkyWarsReloaded.getInvC().restoreInventory(getP());
							}
						}
					}, 5);
				}
			}
		}
		
	}

	private void giveSpectateItems() {
		if (getP() != null) {
			getP().getInventory().clear();
			getP().getInventory().setItem(8, exit);
			boolean specShop = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.spectateShopEnabled"); 
			if (specShop) {
				if (SkyWarsReloaded.perms.has(getP(), "swr.spectateshop")) {
					getP().getInventory().setItem(4, specShopItem);
				}
			}
			getP().getInventory().setItem(0, spec);
		}
	}

	public boolean gamemodeChangeAllowed() {
		if (isSpectating) {
			return false;
		}
		return true;
	}

	public int getOpVote() {
		return votedForOP;
	}
	
	public void setOpVote(int vote) {
		votedForOP = vote;
	}
	
	public int getJumpVote() {
		return jumpVote;
	}
	
	public void setJumpVote(int vote) {
		jumpVote = vote;
	}
	
	public int getWeatherVote() {
		return weatherVote;
	}
	
	public void setWeatherVote(int vote) {
		weatherVote = vote;
	}

	public void setTimeVote(int i) {
		timeVote = i;		
	}

	public int getTimeVote() {
		return timeVote;
	}

	public void setGlass(String name2) {
		color = name2;
	}
	
	public String getGlass() {
		return color;
	}
	
	public List<String> getPerms() {
		return permissions;
	}
	
	public void addPerm(String perm) {
		newPerms.add(perm);
		permissions.add(perm);
	}
	
	public void clearNewPerms() {
		newPerms = null;
		newPerms = new ArrayList<String>();
	}
	
	public List<String> getNewPerms() {
		return newPerms;
	}
	
	public boolean hasPerm(String perm) {
		if (permissions.contains(perm)) {
			return true;
		}
		return false;
	}
	
	public void setPerms(List<String> perms) {
		for (String perm: perms) {
			permissions.add(perm);
		}
	}

	public String getEffect() {
		return effect;
	}
	
	public void setEffect(String effect) {
		this.effect = effect;
	}
	
	public String getProjEffect() {
		return projEffect;
	}
	
	public void setProjEffect(String effect) {
		this.projEffect = effect;
	}
	
}
