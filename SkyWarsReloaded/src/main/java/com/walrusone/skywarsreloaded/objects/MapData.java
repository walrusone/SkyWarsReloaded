package com.walrusone.skywarsreloaded.objects;

import java.util.List;

import javax.annotation.Nullable;

public class MapData {
	private String name;
	private String displayName;
	private int minPlayers;
	private String creator;
	private List<String> signs;
	private boolean registered;
	
	public MapData(String n, String dn, int mp, String c, @Nullable List<String> signs, boolean registered) {
		name = n;
		displayName = dn;
		minPlayers = mp;
		creator = c;
		this.registered = registered;
		this.signs = signs;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public List<String> getSigns() {
		return signs;
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
}