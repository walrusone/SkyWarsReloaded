package com.walrusone.skywarsreloaded.matchevents;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;

public abstract class MatchEvent {
	protected GameMap gMap;
	protected boolean enabled;
	protected int min;
	protected int max;
	protected int length;
	protected int startTime;
	protected int chance;
	private boolean useThisMatch;
	protected boolean fired;
	protected boolean repeatable;
	protected String title;
	protected String subtitle;
	protected String startMessage;
	protected String endMessage;
	protected boolean announceEvent;
	protected ItemStack material;
	protected int slot;	
	protected String eventName;
	
	public abstract void doEvent();
	public abstract void endEvent(boolean force);
	public abstract void saveEventData();
	
	public void reset() {
		fired = false;
		fireThisMatch();
		setStartTime();
	}
	
	private void fireThisMatch() {
		useThisMatch = false;
		if (enabled) {
			int rand = Util.get().getRandomNum(0, 100);
			if (rand < chance) {
				useThisMatch = true;
			}
		}
	}
	
	public boolean willFire() {
		return useThisMatch;
	}
	
	public void setStartTime() {
		startTime = ThreadLocalRandom.current().nextInt(min,  max + 1);
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	protected void sendTitle() {
		for (final Player player : gMap.getAlivePlayers()) {
    		if (SkyWarsReloaded.getCfg().titlesEnabled()) {
                Util.get().sendTitle(player, 2, 20, 2, ChatColor.translateAlternateColorCodes('&', title), 
                		ChatColor.translateAlternateColorCodes('&', subtitle));
    		}
    		MatchManager.get().message(gMap, ChatColor.translateAlternateColorCodes('&', startMessage));
		}
	}
	
	public void announceTimer() {
		 final int v1 = startTime - gMap.getTimer();
	        String time;
	        if (v1 % 60 == 0) {
	            time = v1 / 60 + " " + ((v1 > 60) ? new Messaging.MessageFormatter().format("timer.minutes") :  new Messaging.MessageFormatter().format("timer.minute"));
	        }
	        else {
	            if (v1 >= 60 || (v1 % 10 != 0 && v1 >= 10) || v1 <= 0) {
	                return;
	            }
	            time = v1 + " " + ((v1 > 1) ? new Messaging.MessageFormatter().format("timer.seconds") :  new Messaging.MessageFormatter().format("timer.second"));
	        }
	        MatchManager.get().message(gMap, new Messaging.MessageFormatter().setVariable("event", title).setVariable("time", time).format("event.announce"));
	}

	public boolean fired() {
		return fired;
	}

	public boolean announceEnabled() {
		return announceEvent;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public String getTitle() {
		return ChatColor.translateAlternateColorCodes('&', title);
	}
	
	public ItemStack getMaterial() {
		return material;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
	}
	public GameMap getGameMap() {
		return gMap;
	}
	public boolean hasFired() {
		return fired;
	}
}
