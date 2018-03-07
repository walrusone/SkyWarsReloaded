package com.walrusone.skywarsreloaded.game;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class GameQueue {

	private Queue<PlayerCard> queue = new LinkedList<PlayerCard>();
	private GameMap map;
	private boolean running = false;
	
	public GameQueue(GameMap g) {
		map = g;
	}
	
	public void add(PlayerCard pCard) {
		queue.add(pCard);
		if (!running) {
			sendToGame();
		}
	}
	
	public void sendToGame() {
		if (!queue.isEmpty()) {
			running = true;
			if (SkyWarsReloaded.get().isEnabled()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						MatchManager.get().teleportToArena(map, queue.poll());
						sendToGame();
					}	
				}.runTaskLater(SkyWarsReloaded.get(), 2L);
			}
		} else {
			running = false;
		}
	}
}
