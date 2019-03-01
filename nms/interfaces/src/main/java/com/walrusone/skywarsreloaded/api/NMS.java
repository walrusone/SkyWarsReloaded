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
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface NMS {

	public void respawnPlayer(Player player);
	public void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount);
	public FireworkEffect getFireworkEffect(Color one, Color two, Color three, Color four, Color five, Type type);
	public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle);
	public void sendActionBar(Player p, String msg);
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
	public ChunkGenerator getChunkGenerator();
    public boolean checkMaterial(FallingBlock fb, Material mat);
    public Objective getNewObjective(Scoreboard scoreboard, String criteria, String DisplayName);
    public void setGameRule(World world, String rule, String bool);
    public boolean headCheck(Block h1);
	public ItemStack getBlankPlayerHead();
	public int getVersion();
	public ItemStack getMaterial(String item);
    public ItemStack getColorItem(String mat, byte color);
	public void setBlockWithColor(World world, int x, int y, int z, Material mat, byte cByte);
	public void deleteCache();
	public Block getHitBlock(ProjectileHitEvent e);
}
