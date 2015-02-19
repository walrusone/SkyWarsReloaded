package com.walrusone.skywars.listeners;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.utilities.Messaging;

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
								 target.setTagged(killer);
								 return;
							 }
					 	} else if (damager instanceof Egg) {
							 Egg egg = (Egg) damager;
							 if(egg.getShooter() instanceof Player) {
								 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) egg.getShooter());
									 target.setTagged(killer);
									 return;
								 }
					 	} else if (damager instanceof Arrow) {
							 Arrow arrow = (Arrow) damager;
							 if(arrow.getShooter() instanceof Player) {
								 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) arrow.getShooter());
									 target.setTagged(killer);
									 return;
							 }
					 	} else if (damager instanceof EnderPearl) {
						 EnderPearl ePearl = (EnderPearl) damager;
						 if(ePearl.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) ePearl.getShooter());
								 target.setTagged(killer);
								 return;
							 }
						 }
				 } else if (damager instanceof Player) {
					 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer((Player) damager);
						 target.setTagged(killer);
						 return;
					}
			 }
		 }
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		SkyWarsReloaded.getPC().addPlayer(e.getPlayer());
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.giveSpectateItem")) {
			if (SkyWarsReloaded.perms.has(e.getPlayer(), "swr.spectate")) {
				ItemStack spec = new ItemStack(Material.COMPASS, 1);
				ItemMeta meta = spec.getItemMeta();
				meta.setDisplayName("Spectate Games");
				spec.setItemMeta(meta);
				spec.addUnsafeEnchantment(Enchantment.LUCK, 1);
				e.getPlayer().getInventory().setItem(0, spec);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating")) {
			SkyWarsReloaded.getSpectate().setSpectating(e.getPlayer(), false, false);
		}
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
		int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
		int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
		float yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
		float pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
		Location spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
		e.getPlayer().teleport(spawn);
	}
	
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer());
		if (gPlayer.getGame() == null) {
			if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.giveSpectateItem")) {
				if (SkyWarsReloaded.perms.has(e.getPlayer(), "swr.spectate")) {
					ItemStack spec = new ItemStack(Material.COMPASS, 1);
					ItemMeta meta = spec.getItemMeta();
					meta.setDisplayName("Spectate Games");
					spec.setItemMeta(meta);
					spec.addUnsafeEnchantment(Enchantment.LUCK, 1);
					e.getPlayer().getInventory().setItem(0, spec);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer());
		if (gPlayer.getGame() != null) {
			Game game = gPlayer.getGame();
			gPlayer.getGame().deletePlayer(gPlayer, true);
			if (game.getState() == GameState.PLAYING || game.getState() == GameState.PREGAME) {
				game.checkForWinner();
			}
		}
		SkyWarsReloaded.getPC().removePlayer(e.getPlayer());
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onCommandInGame(PlayerCommandPreprocessEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer());
		if (gPlayer.getGame() != null) {
			String command = e.getMessage();
			SkyWarsReloaded.get().getLogger().info(command);
			if (gPlayer.getGame().getState() != GameState.WAITING) {
				List<String> whitelist = SkyWarsReloaded.get().getConfig().getStringList("commandWhitelist");
				if (whitelist.contains(command.toLowerCase())) {
					return;
				} else {
					e.setCancelled(true);
					gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.no-perm-in-game"));
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
        	if (w != null) {
            	World world = SkyWarsReloaded.get().getServer().getWorld(w); 
                if (gPlayer.getGame() == null && player.getLocation().getWorld().equals(world)) {
                    Game game = SkyWarsReloaded.getGC().findGame();
                    if (game != null) {
                        game.addPlayer(gPlayer);
                    } else {
                    	SkyWarsReloaded.getGC().addToQueue(gPlayer);
                    	gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.no-game-available"));
                    }
                }
        	} else {
        		SkyWarsReloaded.get().getLogger().info("Spawn must be Set using swr setspawn before pressure plates can be used to join games!");
        	}
        } else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (e.getPlayer().getItemInHand().getData().getItemType() == Material.STORAGE_MINECART)) {
        	if (gPlayer.getGame().getState() == GameState.INLOBBY) {
				if (!gPlayer.hasKitSelected()) {
					SkyWarsReloaded.getKC().openKitMenu(gPlayer);
					e.setCancelled(true);
				} else {
					player.sendMessage(new Messaging.MessageFormatter().format("error.already-has-kit"));
				}
			}
     	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (e.getPlayer().getItemInHand().getData().getItemType() == Material.IRON_DOOR)) {
			if (gPlayer.getGame() != null) {
				if (e.getItem().containsEnchantment(Enchantment.LUCK)) {
					e.setCancelled(true);
					gPlayer.getGame().deletePlayer(gPlayer, true);
				} 
			} else {
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating")) {
					if(SkyWarsReloaded.getSpectate().isSpectator(player)) {
						SkyWarsReloaded.getSpectate().setSpectating(player, false, true);
						gPlayer.setSpectating(false);
						gPlayer.getSpecGame().removeSpectator(gPlayer);
						String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
						int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
						int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
						int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
						Location loc = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z);
						player.teleport(loc);
					}
				}
			}
     	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (e.getPlayer().getItemInHand().getData().getItemType() == Material.COMPASS)) {
			if (gPlayer.getGame() == null) {
				if (e.getItem().containsEnchantment(Enchantment.LUCK)) {
					if (!gPlayer.getSpectating()) {
						e.setCancelled(true);
						SkyWarsReloaded.getGC().openGameMenu(gPlayer);
					} else {
						e.setCancelled(true);
						SkyWarsReloaded.getGC().openPlayerMenu(gPlayer);
					}

				}
			}
     	} 
    }
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		Game game = SkyWarsReloaded.getPC().getPlayer(p).getGame();
		if (game != null) {
			GameState gs = game.getState();
			if (gs == GameState.INLOBBY || gs == GameState.PREGAME) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item != null) {
			if (item.getData().getItemType().equals(Material.CHEST) &&  item.containsEnchantment(Enchantment.LUCK)) {
				e.setCancelled(true);
			} else if (item.getData().getItemType().equals(Material.IRON_DOOR) &&  item.containsEnchantment(Enchantment.LUCK)) {
				e.setCancelled(true);
			} else if (item.getData().getItemType().equals(Material.COMPASS) &&  item.containsEnchantment(Enchantment.LUCK)) {
				e.setCancelled(true);
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
	
	@EventHandler
    public void onFallDamage(EntityDamageEvent e) {
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.noFallDamage")) {
			if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
				Player player = (Player) e.getEntity();
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player);
				if (gPlayer.getGame() != null) {
					e.setCancelled(true);
				}
			}
		}
	}
        
	
	@EventHandler		(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e){
		final Player player = e.getEntity();
		final GamePlayer target = SkyWarsReloaded.getPC().getPlayer(player);
		Entity ent = e.getEntity();
		final DamageCause dCause = ent.getLastDamageCause().getCause();
		if (target.getGame() != null) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
		        public void run() {
		          respawn(player);
		          target.getGame().onPlayerDeath(target, dCause);
		        }
		      }, 1);
		}
	
	}	
	
    public void respawn(Player player) {
        try {
            String path = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object respawnEnum = Class.forName(path + ".EnumClientCommand").getEnumConstants()[0];
            Constructor<?>[] constructors = Class.forName(path + ".PacketPlayInClientCommand").getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] args = constructor.getParameterTypes();
                if (args.length == 1 && args[0] == respawnEnum.getClass()) {
                    Object packet = Class.forName(path + ".PacketPlayInClientCommand").getConstructor(args).newInstance(respawnEnum);
                    Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                    connection.getClass().getMethod("a", packet.getClass()).invoke(connection, packet);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.addScorePrefix")) {
        	GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(event.getPlayer());
        	int score = gPlayer.getScore();
        	if (score < 0) {
                event.setFormat(ChatColor.RED + "(" + gPlayer.getScore() + ")" + ChatColor.RESET + event.getFormat());
        	} else {
                event.setFormat(ChatColor.GREEN + "(+" + gPlayer.getScore() + ")" + ChatColor.RESET + event.getFormat());
        	}
    	}
    }
    
}
