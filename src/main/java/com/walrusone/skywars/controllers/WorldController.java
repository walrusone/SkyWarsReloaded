package com.walrusone.skywars.controllers;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.utilities.WEUtils;

@SuppressWarnings("deprecation")
public class WorldController {

    private static final int PASTE_HEIGHT = 75;
    private static WorldController worldController;
    private World islandWorld;
    private Queue<int[]> freeIslands = Lists.newLinkedList();
    private int nextId;
    private int mapSize;
    private int mapsPerWorld;

    public WorldController() {
        mapSize = SkyWarsReloaded.get().getConfig().getInt("gameVariables.maxMapSize") + 100;
        mapsPerWorld = SkyWarsReloaded.get().getConfig().getInt("gameVariables.mapsPerWorld");
    	generateIslandCoordinates();
        islandWorld = createWorld();

    }

    private void generateIslandCoordinates() {
        for (int xxx = 0; xxx < mapsPerWorld; xxx++) {
            for (int zzz = 0; zzz < mapsPerWorld; zzz++) {
                int[] coordinates = new int[] { xxx, zzz };

                if (!freeIslands.contains(coordinates)) {
                    freeIslands.add(coordinates);
                }
            }
        }
    }

    public void unloadMap(Game game) {
        int[] mapCoordinates = game.getMapCoordinates();
        int mapX = mapCoordinates[0];
        int mapZ = mapCoordinates[1];

        int minX = (mapX * mapSize) >> 4;
        int minZ = (mapZ * mapSize) >> 4;
        int maxX = (mapX * mapSize + mapSize) >> 4;
        int maxZ = (mapZ * mapSize + mapSize) >> 4;

        for (int xxx = minX; xxx < maxX; xxx++) {
            for (int zzz = minZ; zzz < maxZ; zzz++) {
                Chunk chunk = game.getMapWorld().getChunkAt(xxx, zzz);

                if (chunk.isLoaded()) {
                    for (Entity entity : chunk.getEntities()) {
                        if (!(entity instanceof Player)) {
                            entity.remove();
                        }
                    }
                    chunk.unload(false);
                }
            }
        }
    }
    
    public void unloadLobby(Game game) {       
        int[] lobbyCoordinates = game.getLobbyCoordinates();
        int lobbyX = lobbyCoordinates[0];
        int lobbyZ = lobbyCoordinates[1];

        int minX = (lobbyX * mapSize) >> 4;
        int minZ = (lobbyZ * mapSize) >> 4;
        int maxX = (lobbyX * mapSize + mapSize) >> 4;
        int maxZ = (lobbyZ * mapSize + mapSize) >> 4;

        for (int xxx = minX; xxx < maxX; xxx++) {
            for (int zzz = minZ; zzz < maxZ; zzz++) {
                Chunk chunk = game.getMapWorld().getChunkAt(xxx, zzz);

                if (chunk.isLoaded()) {
                    for (Entity entity : chunk.getEntities()) {
                        if (!(entity instanceof Player)) {
                            entity.remove();
                        }
                    }
                    chunk.unload(false);
                }
            }
        }
    }

    public World createMap(Game game, CuboidClipboard schematic) {
        if (freeIslands.size() == 0) {
            generateIslandCoordinates();
            islandWorld = createWorld();
        }

        int[] islandCoordinates = freeIslands.poll();
        game.setMapCoordinates(islandCoordinates);

        int islandX = islandCoordinates[0];
        int islandZ = islandCoordinates[1];

        int midX = islandX * mapSize + mapSize / 2;
        int midZ = islandZ * mapSize + mapSize / 2;

        WEUtils.pasteSchematic(new Location(islandWorld, midX, PASTE_HEIGHT, midZ), schematic);

        Map<Integer, Vector> spawns = SkyWarsReloaded.getMC().getCachedSpawns(schematic);
        Vector isleLocation = new Vector(midX, PASTE_HEIGHT, midZ);

        for (Map.Entry<Integer, Vector> entry : spawns.entrySet()) {
            Vector spawn = entry.getValue().add(isleLocation).add(schematic.getOffset());
            Location location = new Location(islandWorld, spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

            game.addSpawn(entry.getKey(), location);
        }

        Collection<Vector> chests = SkyWarsReloaded.getMC().getCachedChests(schematic);

        if (chests != null) {
            for (Vector location : chests) {
                Vector spawn = location.add(isleLocation).add(schematic.getOffset());
                Location chest = new Location(islandWorld, spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
                game.addChest(chest);
            }
        }
        
        return islandWorld;
    }
    
    public World createLobby(Game game, CuboidClipboard schematic) {
        if (freeIslands.size() == 0) {

            generateIslandCoordinates();
            islandWorld = createWorld();
        }

        int[] islandCoordinates = freeIslands.poll();
        game.setLobbyCoordinates(islandCoordinates);

        int islandX = islandCoordinates[0];
        int islandZ = islandCoordinates[1];

        int midX = islandX * mapSize + mapSize / 2;
        int midZ = islandZ * mapSize + mapSize / 2;

        WEUtils.pasteSchematic(new Location(islandWorld, midX, PASTE_HEIGHT, midZ), schematic);

        Map<Integer, Vector> spawns = SkyWarsReloaded.getMC().getCachedSpawns(schematic);
        Vector isleLocation = new Vector(midX, PASTE_HEIGHT, midZ);

        for (Map.Entry<Integer, Vector> entry : spawns.entrySet()) {
            Vector spawn = entry.getValue().add(isleLocation).add(schematic.getOffset());
            Location location = new Location(islandWorld, spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

            game.addLobbySpawn(entry.getKey(), location);
        }

        Map<Integer, Vector> signs = SkyWarsReloaded.getMC().getCachedSigns(schematic);

        if (signs != null) {
            for (Map.Entry<Integer, Vector> entry : signs.entrySet()) {
                Vector sign = entry.getValue().add(isleLocation).add(schematic.getOffset());
                Location location = new Location(islandWorld, sign.getBlockX(), sign.getBlockY(), sign.getBlockZ());

                game.addLobbySign(entry.getKey(), location);
            }
        }
        return islandWorld;
    }

    private World createWorld() {
        WorldCreator worldCreator = new WorldCreator("island-" + getNextId());
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generateStructures(false);
        worldCreator.generator(new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                if (!world.isChunkLoaded(0, 0)) {
                    world.loadChunk(0, 0);
                }

                return new Location(world, 0.0D, 64.0D, 0.0D);
            }
        });

        World world = worldCreator.createWorld();
        world.setDifficulty(Difficulty.NORMAL);
        world.setSpawnFlags(false, false);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setTicksPerAnimalSpawns(0);
        world.setTicksPerMonsterSpawns(0);

        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");

        return world;
    }

    private int getNextId() {
        int id = nextId++;

        if (nextId == Integer.MAX_VALUE) {
            nextId = 0;
        }

        return id;
    }

    public static WorldController get() {
        if (worldController == null) {
            worldController = new WorldController();
        }

        return worldController;
    }

}
