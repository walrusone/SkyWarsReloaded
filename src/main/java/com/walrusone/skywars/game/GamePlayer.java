package com.walrusone.skywars.game;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
public class GamePlayer {

	private Player p;
	private GameMode gameMode;
	private boolean spectating = false;
	private boolean isPlaying = false;
	private String name;
	private int lives;
	private String votedFor = "";
	private HashMap<String, ItemStack[]> invMap = new HashMap<String, ItemStack[]>();
	private HashMap<String, ItemStack[]> armMap = new HashMap<String, ItemStack[]>();
	private int wins;
	private int kills;
	private int deaths;
	private int gamesPlayed;
	private int score;
	private int blocksPlaced;
	private boolean hasKitSelected;
	private GameKit selectedKit;
	private Tagged taggedBy;
	private Location respawn;
	private Game game = null;
	
	public GamePlayer (Player player) {
		p = player;
		SkyWarsReloaded.getDS().loadPlayer(this);
		name = player.getName();
		lives = 1;
		setTagged(this);
	}
	
	public void saveInventory() {
		invMap.put(p.getName(), p.getInventory().getContents());
		armMap.put(p.getName(), p.getInventory().getArmorContents());
	}
	
	public void clearInventory() {
		p.getInventory().clear();
		p.getInventory().clear(8);
		p.getInventory().setHelmet(null);
	    p.getInventory().setChestplate(null);
	    p.getInventory().setLeggings(null);
	    p.getInventory().setBoots(null);
	}
	
	public void resetInventory() {
		p.getInventory().setContents(invMap.get(p.getName()));
		p.getInventory().setArmorContents(armMap.get(p.getName()));
	}
	
	public void giveKit() {

	}
	
	public Boolean removeBoost() {
		ItemStack i = p.getInventory().getItem(8);
		if (i == null) {
			p.sendMessage(ChatColor.RED + "No Boost Left!");
			return false;
		} else if (i.getAmount() <=1) {
			p.getInventory().removeItem(i);
			return true;
        } else if(i.getAmount() > 1) {
			i.setAmount(i.getAmount() - 1);
			return true;
        }
		return false;
    }
	
	public Player getP() {
		return p;
	}
	
	public GamePlayer getGamePlayer() {
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLives(int s) {
		lives = s;
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
	
	public int getLives() {
		return lives;
	}

	public void setVoted(String mapName) {
		votedFor = mapName;
	}
	
	public String getVoted() {
		return votedFor;
	}

	public void setSpectating(boolean b) {
		spectating = b;
	}
	
	public boolean getSpectating() {
		return spectating;
	}

	public void setPlaying(boolean b) {
		isPlaying = b;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void resetLives() {
		lives = 1;
	}

	public void saveGameMode() {
		gameMode = p.getGameMode();
	}
	
	public void resetGameMode() {
		p.setGameMode(gameMode);
	}

	public void setTagged(GamePlayer player) {
		taggedBy = new Tagged(player, System.currentTimeMillis());
	}
	
	public Tagged getTagged() {
		return taggedBy;
	}
	
	public void setRespawn(Location loc) {
		respawn = loc;
	}
	
	public Location getRespawn() {
		return respawn;
	}
	
	public class Tagged {
		private GamePlayer player;
		private Long time;
		
		public Tagged(GamePlayer player, Long time) {
			this.player = player;
			this.time = time;
		}
		
		public GamePlayer getPlayer() {
			return player;
		}
		
		public Long getTime() {
			return time;
		}
	}
	
	public class ShotType {
		private String curShot;
		private String curPower;
		private Long time;
		
		public ShotType(String curShot, String curPower, Long time) {
			this.curShot = curShot;
			this.curPower = curPower;
			this.time = time;
		}
		
		public String getCurShot() {
			return curShot;
		}
		
		public String getCurPower() {
			return curPower;
		}
		
		public Long getTime() {
			return time;
		}
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
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
	
}
