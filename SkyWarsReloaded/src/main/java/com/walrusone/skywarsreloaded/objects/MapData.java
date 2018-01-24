package com.walrusone.skywarsreloaded.objects;

import java.util.List;

import javax.annotation.Nullable;

public class MapData {
	private String name;
	private String displayName;
	private int minPlayers;
	private String creator;
	private List<String> signs;
	
	public MapData(String n, String dn, int mp, String c, @Nullable List<String> signs) {
		name = n;
		displayName = dn;
		minPlayers = mp;
		creator = c;
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
	
}