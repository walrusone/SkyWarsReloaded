package com.walrusone.skywars.utilities;

import com.walrusone.skywars.game.GamePlayer;

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
