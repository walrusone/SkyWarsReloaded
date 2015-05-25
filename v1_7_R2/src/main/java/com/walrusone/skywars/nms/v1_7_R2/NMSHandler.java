package com.walrusone.skywars.nms.v1_7_R2;

import java.util.List;

import net.minecraft.server.v1_7_R2.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R2.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.walrusone.skywars.api.NMS;

public class NMSHandler implements NMS {
	
	public void respawnPlayer(Player player) {
		((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
	}
	
	@SuppressWarnings("unchecked")
	public void updateChunks(World world, List<Chunk> chunks) {
		for (Chunk currentChunk: chunks) {
			net.minecraft.server.v1_7_R2.World mcWorld = ((CraftChunk) currentChunk).getHandle().world;
	        for (EntityPlayer ep : (List<EntityPlayer>) mcWorld.players) {
	                ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(currentChunk.getX(), currentChunk.getZ()));
	        }
		}
	}
	
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles("SPELL_WITCH", x, y, z, offsetX, offsetY, offsetZ, data, amount);
		for (Player player: world.getPlayers()) {
			CraftPlayer start = (CraftPlayer) player; //Replace player with your player.
			EntityPlayer target = start.getHandle();
			PlayerConnection connect = target.playerConnection;
			connect.sendPacket(particles);
		}
	}
}
