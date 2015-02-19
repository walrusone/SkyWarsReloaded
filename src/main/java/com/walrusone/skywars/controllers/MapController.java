package com.walrusone.skywars.controllers;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.walrusone.skywars.SkyWarsReloaded;

@SuppressWarnings("deprecation")
public class MapController {

    private final Random random = new Random();
    private final Map<String, CuboidClipboard> schematicMap = Maps.newHashMap();
    private final Map<CuboidClipboard, Map<Integer, Vector>> spawnCache = Maps.newHashMap();
    private final Map<CuboidClipboard, List<Vector>> chestCache = Maps.newHashMap();
    private final Map<CuboidClipboard, Map<Integer, Vector>> signCache = Maps.newHashMap();

    public MapController() {
        File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File schematicsDirectory = new File(dataDirectory, "maps");

        if (!schematicsDirectory.exists() && !schematicsDirectory.mkdirs()) {
            return;
        }

        File[] schematics = schematicsDirectory.listFiles();
        if (schematics == null) {
            return;
        }

        for (File schematic : schematics) {
            if (!schematic.getName().endsWith(".schematic")) {
                continue;
            }

            if (!schematic.isFile()) {
                continue;
            }

            SchematicFormat schematicFormat = SchematicFormat.getFormat(schematic);
            if (schematicFormat == null) {
                continue;
            }

            try {
                registerSchematic(schematic.getName().replace(".schematic", ""), schematicFormat.load(schematic));
            } catch (Exception e) {
            }
        }
    }

    public void registerSchematic(final String name, final CuboidClipboard schematic) {
        Bukkit.getScheduler().runTaskAsynchronously(SkyWarsReloaded.get(), new Runnable() {
            @Override
            public void run() {
                Vector currentPoint;
                int currentBlock;
                int spawnId = 0;
                int signId = 0;

                for (int y = 0; y < schematic.getSize().getBlockY(); ++y) {
                    for (int x = 0; x < schematic.getSize().getBlockX(); ++x) {
                        for (int z = 0; z < schematic.getSize().getBlockZ(); ++z) {
                            currentPoint = new Vector(x, y, z);
                            currentBlock = schematic.getPoint(currentPoint).getType();

                            if (currentBlock == 0) {
                                continue;
                            }

                            if (currentBlock == Material.BEACON.getId()) {
                                cacheSpawn(schematic, spawnId++, currentPoint);
                                schematic.setBlock(currentPoint, new BaseBlock(0));
                            } else if (currentBlock == Material.CHEST.getId()) {
                                cacheChest(schematic, currentPoint);
                            } else if (currentBlock == Material.SIGN_POST.getId() || currentBlock == Material.WALL_SIGN.getId()) {
                            	cacheSign(schematic, signId++, currentPoint);
                            }

                        }
                    }
                }

                schematicMap.put(name, schematic);
            }
        });
    }

    public CuboidClipboard getRandom() {
        List<CuboidClipboard> schematics = Lists.newArrayList(schematicMap.values());
        CuboidClipboard schem = schematics.get(random.nextInt(schematics.size()));
        while (getName(schem).equalsIgnoreCase("lobby")) {
        	schem = schematics.get(random.nextInt(schematics.size()));
        }
        return schem;
    }

    public String getName(CuboidClipboard cuboidClipboard) {
        for (Map.Entry<String, CuboidClipboard> entry : schematicMap.entrySet()) {
            if (entry.getValue().equals(cuboidClipboard)) {
                return entry.getKey();
            }
        }

        return null;
    }
    
    public CuboidClipboard getMap(String name) {
        return schematicMap.get(name);
    }

    public void cacheSpawn(CuboidClipboard schematic, int position, Vector location) {
        Map<Integer, Vector> spawnPlaces;

        if (spawnCache.containsKey(schematic)) {
            spawnPlaces = spawnCache.get(schematic);

        } else {
            spawnPlaces = Maps.newHashMap();
        }

        spawnPlaces.put(position, location);
        spawnCache.put(schematic, spawnPlaces);
    }

    public Map<Integer, Vector> getCachedSpawns(CuboidClipboard schematic) {
        return spawnCache.get(schematic);
    }
    
    private void cacheChest(CuboidClipboard schematic, Vector location) {
        List<Vector> chestList;

        if (chestCache.containsKey(schematic)) {
            chestList = chestCache.get(schematic);
        } else {
            chestList = Lists.newArrayList();
        }

        chestList.add(location);
        chestCache.put(schematic, chestList);
    }
    
    public CopyOnWriteArrayList<String> getMaps() {
    	CopyOnWriteArrayList<String> maps = new CopyOnWriteArrayList<String>();
    	for (String map: schematicMap.keySet()) {
    		maps.add(map);
    	}
    	return maps;
    }
    
    public void cacheSign(CuboidClipboard schematic, int position, Vector location) {
        Map<Integer, Vector> signPlaces;

        if (signCache.containsKey(schematic)) {
            signPlaces = signCache.get(schematic);

        } else {
            signPlaces = Maps.newHashMap();
        }

        signPlaces.put(position, location);
        signCache.put(schematic, signPlaces);
    }

    public Collection<Vector> getCachedChests(CuboidClipboard schematic) {
        return chestCache.get(schematic);
    }
    
    public Map<Integer, Vector> getCachedSigns(CuboidClipboard schematic) {
        return signCache.get(schematic);
    }

    public int size() {
        return schematicMap.size();
    }

    public void remove(String schematic) {
        schematicMap.remove(schematic);
    }

}