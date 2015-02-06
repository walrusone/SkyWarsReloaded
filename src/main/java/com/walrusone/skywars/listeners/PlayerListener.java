package com.walrusone.skywars.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void snowballDamage(EntityDamageByEntityEvent e) {
		 Entity ent = e.getEntity();
		 if(ent instanceof Player) {
			 GamePlayer target = SkyWarsReloaded.getPC().getPlayer((Player) ent);
			 if (target.isPlaying()) {
				 Entity damager = e.getDamager();
				 if (e.getCause().equals(DamageCause.PROJECTILE)) {
					 if (damager instanceof Snowball) {
						 Snowball snowball = (Snowball) damager;
						 if(snowball.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) snowball.getShooter());
							 if (target.getP().getHealth() <= e.getDamage()) {
								 e.setCancelled(true);
								 target.getP().setHealth(20);
								 target.setTagged(killer);
								 target.getGame().onPlayerDeath(target);
								 return;
							 } else {
								 target.setTagged(killer);
								 return;
							 }
						 }
					 } else if (damager instanceof Egg) {
						 Egg egg = (Egg) damager;
						 if(egg.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) egg.getShooter());
							 if (target.getP().getHealth() <= e.getDamage()) {
								 e.setCancelled(true);
								 target.getP().setHealth(20);
								 target.setTagged(killer);
								 target.getGame().onPlayerDeath(target);
								 return;
							 } else {
								 target.setTagged(killer);
								 return;
							 }
						 }
					 } else if (damager instanceof Arrow) {
						 Arrow arrow = (Arrow) damager;
						 if(arrow.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) arrow.getShooter());
							 if (target.getP().getHealth() <= e.getDamage()) {
								 e.setCancelled(true);
								 target.getP().setHealth(20);
								 target.setTagged(killer);
								 target.getGame().onPlayerDeath(target);
								 return;
							 } else {
								 target.setTagged(killer);
								 return;
							 }
						 }
					 } else if (damager instanceof EnderPearl) {
						 EnderPearl ePearl = (EnderPearl) damager;
						 if(ePearl.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) ePearl.getShooter());
							 if (target.getP().getHealth() <= e.getDamage()) {
								 e.setCancelled(true);
								 target.getP().setHealth(20);
								 target.setTagged(killer);
								 target.getGame().onPlayerDeath(target);
								 return;
							 } else {
								 target.setTagged(killer);
								 return;
							 }
						 }
					 }
				 } else if (damager instanceof Player) {
					 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) damager);
					 if (target.getP().getHealth() <= e.getDamage()) {
						 e.setCancelled(true);
						 target.getP().setHealth(20);
						 target.setTagged(killer);
						 target.getGame().onPlayerDeath(target);
						 return;
					 } else {
						 target.setTagged(killer);
						 return;
					 }
				 }
			 }
		 }
	}

	@EventHandler
	public void onPlayerDeath(EntityDamageEvent e) {
		Entity ent = e.getEntity(); 
		if(ent instanceof Player) {
			Player p = (Player) e.getEntity();
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(p);
			if (gPlayer.isPlaying()) {
				if (!e.getCause().equals(DamageCause.PROJECTILE) && !e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
					if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
						e.setCancelled(true);
						return;
					} else if (p.getHealth() <= e.getDamage() || e.getCause().equals(DamageCause.VOID)) {
						e.setCancelled(true);
						p.setHealth(20);
						gPlayer.getGame().onPlayerDeath(gPlayer);
					}
				}
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		SkyWarsReloaded.getPC().addPlayer(e.getPlayer());
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer());
		if (gPlayer.getGame() != null) {
			Game game = gPlayer.getGame();
			gPlayer.getGame().deletePlayer(gPlayer, true);
			if (game.getState() == GameState.PLAYING || game.getState() == GameState.PREGAME) {
				gPlayer.getGame().checkForWinner();
			}
		}
		SkyWarsReloaded.getPC().removePlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onCommandInGame(PlayerCommandPreprocessEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer());
		if (gPlayer.getGame() != null) {
			String command = e.getMessage();
			SkyWarsReloaded.get().getLogger().info(command);
			if (gPlayer.getGame().getState() != GameState.WAITING) {
				if (command.equalsIgnoreCase("/swr quit")) {
					return;
				} else if (command.equalsIgnoreCase("/swr kit")) {
					if (gPlayer.isPlaying()) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + "You cannot use that command in game!");
					}
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You cannot use that command in game!");
				}
			}
		}
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);

        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.STONE_PLATE) {
        	String w = SkyWarsReloaded.get().getConfig().getString("spawn.world");
        	World world = SkyWarsReloaded.get().getServer().getWorld(w); 
            if (gPlayer.getGame() == null && player.getLocation().getWorld().equals(world)) {
                Game game = SkyWarsReloaded.getGC().findGame();
                game.addPlayer(gPlayer);
            }
        }
    }
	
	@EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);

        if (gPlayer.isPlaying()) {
        	gPlayer.setBlocks(gPlayer.getBlocks() + 1);
        }
    }
	
	
	
}
