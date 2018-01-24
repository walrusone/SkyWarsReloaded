package com.walrusone.skywarsreloaded.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.database.DataStorage;

public class Leaderboard {

	private static ArrayList<LeaderData> leaders = new ArrayList<LeaderData>();
	private static boolean loaded = false;
	
	public Leaderboard() {
		SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncRepeatingTask(SkyWarsReloaded.get(), new Runnable() {
			@Override
			public void run() {
				DataStorage.get().updateTop();
			}
		}
		, 0, SkyWarsReloaded.getCfg().getUpdateTime() * 20);
	}
	
	public void addLeader(String name, int wins, int loses, int kills, int deaths, int elo, int xp) {
		leaders.add(new LeaderData(name, wins, loses, kills, deaths, elo, xp));
	}
	
	public void resetLeader() {
		loaded = false;
		leaders.clear();
	}
	
	public void finishedLoading() {
		loaded = true;
	}
	
	public boolean loaded() {
		return loaded;
	}
    
    public List<LeaderData> getTop(final int top) {
        final ArrayList<LeaderData> pData = new ArrayList<LeaderData>();
        pData.addAll(leaders);
        Collections.<LeaderData>sort(pData, new RankComparator());
        return pData.subList(0, (top > pData.size()) ? pData.size() : top);
    }
        
    public class RankComparator implements Comparator<LeaderData>
    {
        @Override
        public int compare(final LeaderData f1, final LeaderData f2) {
            return f2.getElo() - f1.getElo();
        }
    }
    
	public class LeaderData {
		private String name;
		private int wins;
		private int loses;
		private int kills;
		private int deaths;
		private int elo;
		private int xp;
		
		public LeaderData(String name, int wins, int loses, int kills, int deaths, int elo, int xp) {
			this.name = name;
			this.wins = wins;
			this.loses = loses;
			this.kills = kills;
			this.deaths = deaths;
			this.elo = elo;
			this.xp = xp;
		}
		
		public String getName() {
			return name;
		}
		
		public int getWins() {
			return wins;
		}
		
		public int getLoses() {
			return loses;
		}
		
		public int getKills() {
			return kills;
		}
		
		public int getDeaths() {
			return deaths;
		}
		
		public int getElo() {
			return elo;
		}
		
		public int getXp() {
			return xp;
		}
	}
}