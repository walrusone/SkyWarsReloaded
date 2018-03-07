package com.walrusone.skywarsreloaded.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface NMS {

	public void respawnPlayer(Player player);
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount);
	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type);
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle);
	public void sendActionBar(Player p, String msg);
	public boolean isOnePointEight();
	public String getItemName(ItemStack item);
	public void playGameSound(Location loc, String sound, float volume, float pitch, boolean customSound);
	public ItemStack getMainHandItem(Player player);
	public ItemStack getOffHandItem(Player player);
	public ItemStack getItemStack(Material material, List<String> lore, String message);
	public ItemStack getItemStack(ItemStack item, List<String> lore, String message);
	public boolean isValueParticle(String string);
	public void updateSkull(Skull skull, UUID uuid);
	public void setMaxHealth(Player player, int health);
	public void spawnDragon(World world, Location loc);
	public Entity spawnFallingBlock(Location loc, Material mat, boolean damage);
	public void playEnderChestAction(Block block, boolean open);
	public void setEntityTarget(Entity ent, Player player);
	public void updateSkull(SkullMeta meta1, Player player);
}
