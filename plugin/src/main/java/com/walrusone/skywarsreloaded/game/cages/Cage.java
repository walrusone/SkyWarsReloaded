package com.walrusone.skywarsreloaded.game.cages;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import com.walrusone.skywarsreloaded.menus.playeroptions.GlassColorOption;
import com.walrusone.skywarsreloaded.utilities.Util;

public abstract class Cage {
	
	protected ArrayList<CoordLoc> bottomCoordOffsets = new ArrayList<>();
    protected ArrayList<CoordLoc> middleCoordOffsets = new ArrayList<>();
    protected ArrayList<CoordLoc> topCoordOffsets = new ArrayList<>();
	protected CageType cageType;
	
	public void createSpawnPlatforms(GameMap gMap) {
		World world = gMap.getCurrentWorld();
		for(TeamCard tCard: gMap.getTeamCards()) {
            int x = tCard.getSpawn().getX();
            int y = tCard.getSpawn().getY();
            int z = tCard.getSpawn().getZ();
            for (CoordLoc loc: bottomCoordOffsets) {
            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.GLASS);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (CoordLoc loc: middleCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.GLASS);
                    }
                }
            }.runTaskLater(SkyWarsReloaded.get(), 7L);
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (CoordLoc loc: topCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.GLASS);
                    }
                }
            }.runTaskLater(SkyWarsReloaded.get(), 14L);
    	}
	}
	
	@SuppressWarnings("deprecation")
	public boolean setGlassColor(GameMap gMap, TeamCard pCard, String color) {
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
		            for (CoordLoc loc: bottomCoordOffsets) {
		            	 world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
		            }
                    for (CoordLoc loc: middleCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
                    }
                    for (CoordLoc loc: topCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
                    }
				} else {
					Material material = Material.STAINED_GLASS;
					GlassColorOption gColor = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(color.toLowerCase());
					if (gColor != null) {
						material = gColor.getItem().getType();
					}
		            for (CoordLoc loc: bottomCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setData(cByte);
                    }
                    for (CoordLoc loc: middleCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(material);
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setData(cByte);
                    }
                    for (CoordLoc loc: topCoordOffsets) {
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
        for(TeamCard tCard: gMap.getTeamCards()) {
            int x = tCard.getSpawn().getX();
            int y = tCard.getSpawn().getY();
            int z = tCard.getSpawn().getZ();
            for (CoordLoc loc: bottomCoordOffsets) {
            	world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.AIR);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (CoordLoc loc: middleCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.AIR);
                    }
                }
            }.runTaskLater(SkyWarsReloaded.get(), 7L);
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (CoordLoc loc: topCoordOffsets) {
                        world.getBlockAt(x + loc.getX(), y + loc.getY(), z + loc.getZ()).setType(Material.AIR);
                    }
                }
            }.runTaskLater(SkyWarsReloaded.get(), 14L);
    	}
	}

	public CageType getType() {
		return cageType;
	}
}
