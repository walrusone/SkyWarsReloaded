package com.walrusone.skywars.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface NMS {

	public void respawnPlayer(Player player);
	public void updateChunks(World world, List<Chunk> chunks);
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount);
}
