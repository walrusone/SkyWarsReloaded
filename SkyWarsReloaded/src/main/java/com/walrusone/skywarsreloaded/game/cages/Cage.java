package com.walrusone.skywarsreloaded.game.cages;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import com.walrusone.skywarsreloaded.menus.playeroptions.GlassColorOption;
import com.walrusone.skywarsreloaded.utilities.Util;

public abstract class Cage {
	
	protected ArrayList<CoordLoc> coordOffsets = new ArrayList<CoordLoc>();
	protected CageType cageType;
	
	public void createSpawnPlatforms(GameMap gMap) {
		World world = gMap.getCurrentWorld();
		for(PlayerCard pCard: gMap.getPlayerCards()) {
            int x = pCard.getSpawn().getX();
            int y = pCard.getSpawn().getY();
            int z = pCard.getSpawn().getZ();
            for (CoordLoc loc: coordOffsets) {
            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.GLASS);
            }
    	}
	}
	
	@SuppressWarnings("deprecation")
	public boolean setGlassColor(GameMap gMap, PlayerCard pCard, String color) {
		if (gMap.getMatchState() == MatchState.WAITINGSTART) {			
			if (pCard != null) {
				World world = gMap.getCurrentWorld();
	            int x = pCard.getSpawn().getX();
	            int y = pCard.getSpawn().getY();
	            int z = pCard.getSpawn().getZ();
	            
				byte cByte = Util.get().getByteFromColor(color.toLowerCase());
				if (cByte <= -1) {
					Material material = Material.GLASS;
					GlassColorOption gColor = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(color.toLowerCase());
					if (gColor != null) {
						material = gColor.getItem().getType();
					}
		            for (CoordLoc loc: coordOffsets) {
		            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
		            }
				} else {
					Material material = Material.STAINED_GLASS;
					GlassColorOption gColor = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(color.toLowerCase());
					if (gColor != null) {
						material = gColor.getItem().getType();
					}
		            for (CoordLoc loc: coordOffsets) {
		            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
		            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setData(cByte);
		            }
				}
				return true;
			}
		}
		return false;
	}
	
	public void removeSpawnHousing(GameMap gMap) {
		World world = gMap.getCurrentWorld();
		gMap.setAllowFallDamage(false);
        new BukkitRunnable() {
			@Override
			public void run() {
				gMap.setAllowFallDamage(SkyWarsReloaded.getCfg().allowFallDamage());
			}
        }.runTaskLater(SkyWarsReloaded.get(), 100L);
    	for(PlayerCard pCard: gMap.getPlayerCards()) {
            int x = pCard.getSpawn().getX();
            int y = pCard.getSpawn().getY();
            int z = pCard.getSpawn().getZ();
            for (CoordLoc loc: coordOffsets) {
            	world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.AIR);
            } 
    	}
	}

	public CageType getType() {
		return cageType;
	}
	
}
