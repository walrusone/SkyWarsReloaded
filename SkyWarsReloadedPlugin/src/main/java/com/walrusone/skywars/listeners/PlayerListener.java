package com.walrusone.skywars.listeners;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.menus.JoinMenu;
import com.walrusone.skywars.menus.KitMenu;
import com.walrusone.skywars.menus.LobbyMainMenu;
import com.walrusone.skywars.menus.MainMenu;
import com.walrusone.skywars.menus.SpecGameMenu;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.Messaging;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		 Entity ent = e.getEntity();
		if(ent instanceof Player) {
			 GamePlayer hungry = SkyWarsReloaded.getPC().getPlayer(((Player) ent).getUniqueId());
			 if (hungry.inGame()) {
				 Game game = hungry.getGame();
				 if (game != null) {
					 if (game.getState() == GameState.PREGAME) {
						 e.setCancelled(true);
					 }
				 }
			 }
		}
	}
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		 Entity ent = e.getEntity();
		 if(ent instanceof Player) {
			 GamePlayer target = SkyWarsReloaded.getPC().getPlayer(((Player) ent).getUniqueId());
			 if (target.inGame()) {
				Entity damager = e.getDamager();
				if (damager instanceof Projectile) {
					Projectile proj = (Projectile) damager;
					if (proj.getShooter() instanceof Player) {
						GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) proj.getShooter()).getUniqueId());
						target.setTagged(killer);
						return;
					}
				} else if (damager instanceof Player) {
					GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) damager).getUniqueId());
					target.setTagged(killer);
					return;
				}
			 }
		 }
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		SkyWarsReloaded.getPC().addPlayer(e.getPlayer().getUniqueId());
		if (SkyWarsReloaded.getInvC().playerExists(e.getPlayer().getUniqueId().toString())) {
			SkyWarsReloaded.getInvC().restoreInventory(e.getPlayer());
			if (spawn != null) {
				e.getPlayer().teleport(spawn, TeleportCause.PLUGIN);
				final Player player = e.getPlayer();
				SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
					public void run() {
						for (PotionEffect effect : player.getActivePotionEffects()) {
					        player.removePotionEffect(effect.getType());
						}
						player.setFireTicks(0);
						
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}, 5L);
			}
		}
		if (SkyWarsReloaded.getCfg().clearInventoryOnJoin()) {
			e.getPlayer().getInventory().clear();
			e.getPlayer().getInventory().setHelmet(null);
			e.getPlayer().getInventory().setChestplate(null);
			e.getPlayer().getInventory().setLeggings(null);
			e.getPlayer().getInventory().setBoots(null);
		}
		if (SkyWarsReloaded.getCfg().clearXPOnJoin()) {
			e.getPlayer().setExp(0);
		}
		if (SkyWarsReloaded.getCfg().clearPotionEffectsOnJoin()) {
			for (PotionEffect effect : e.getPlayer().getActivePotionEffects()) {
		        e.getPlayer().removePotionEffect(effect.getType());
			}
		}
		if (SkyWarsReloaded.getCfg().sendPlayersToSpawnOnJoin()) {
			e.getPlayer().teleport(spawn, TeleportCause.PLUGIN);
		}
		final Player player = e.getPlayer();
		if (SkyWarsReloaded.getCfg().bungeeEnabled()) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					Game game = SkyWarsReloaded.getGC().getGame(1);
					if (!game.isFull() && game.getState() == GameState.PREGAME) {
						game.addPlayer(SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()));
					} else {
						BungeeUtil.connectToServer(player, SkyWarsReloaded.getCfg().getLobbyServer());
					}
				}
			}, 5L);
		} else  {
			if (inLobbyWorld(player)) {
				SkyWarsReloaded.getNMS().sendTitle(player, 20, 60, 20, SkyWarsReloaded.getMessaging().getMessage("titles.joinServerTitle"), SkyWarsReloaded.getMessaging().getMessage("titles.joinServerSubtitle"));
				givePlayerItems(player);
			} else {
				removePlayerItems(player);
			}
		} 
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		final GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.isSpectating()) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
		        public void run() {
		        	if (gPlayer.getP() != null) {
		        		if (gPlayer.getSpecGame() != null) {
							gPlayer.spectateMode(true, gPlayer.getSpecGame(), gPlayer.getSpecGame().getSpawn(), false);
							gPlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().format("game.spectating"));
		        		}
		        	}
		        }
		      }, 3);
		} else {
			if (inLobbyWorld(player)) {
				givePlayerItems(player);
			} else {
				removePlayerItems(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		final GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.inGame()) {
			Game game = gPlayer.getGame();
			gPlayer.getGame().deletePlayer(gPlayer, true, true);
			if (game.getState() == GameState.PLAYING) {
				game.checkForWinner();
			}
		}
		if (gPlayer.isSpectating()) {
			Game game = gPlayer.getSpecGame();
			game.removeSpectator(gPlayer);
		}
		SkyWarsReloaded.getPC().removePlayer(gPlayer.getUUID());
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCommandInGame(PlayerCommandPreprocessEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.inGame()) {
			String message = e.getMessage();
			boolean cAllow = false;
			for (String comm: SkyWarsReloaded.getCfg().getCommandWhiteList()) {
				if (comm.length() <= message.length()) {
					String command = message.substring(0, comm.length());
					if (command.equalsIgnoreCase(comm)) {
						cAllow = true;
					}
				}
			}
			if (cAllow) {
				return;
			} else {
				e.setCancelled(true);
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.no-perm-in-game"));
			}
		} else if (gPlayer.isSpectating()) {
			String message = e.getMessage();
			boolean cAllow = false;
			for (String comm: SkyWarsReloaded.getCfg().getSpectatorWhiteList()) {
				if (comm.length() <= message.length()) {
					String command = message.substring(0, comm.length());
					if (command.equalsIgnoreCase(comm)) {
						cAllow = true;
					}
				}
			}
			if (cAllow) {
				return;
			} else {
				e.setCancelled(true);
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.no-perm-spectating"));
			}
		}
	}
	
	@EventHandler 
    public void onPlayerInteract(PlayerInteractEvent e) {
		ItemStack item = e.getPlayer().getItemInHand();
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
             
        if (gPlayer.inGame()) {
            if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getEnchantments().keySet()))) {
        		if (gPlayer.getGame().getState() == GameState.PREGAME) {
        			if (!gPlayer.hasKitSelected()) {
        				if (SkyWarsReloaded.getCfg().kitsEnabled()) {
        					new KitMenu(gPlayer);
        				}
        				e.setCancelled(true);
        			} else {
        				player.sendMessage(new Messaging.MessageFormatter().format("error.already-has-kit"));
        			}
        		}
            } else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getExitGameItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getExitGameItem().getEnchantments().keySet()))) {			
            	if (gPlayer.getGame().getState() == GameState.PREGAME) {	
            		e.setCancelled(true);
					gPlayer.getGame().deletePlayer(gPlayer, true, false);
            	}
            } else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getOptionsItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getOptionsItem().getEnchantments().keySet()))) {
				if (gPlayer.getGame().getState() == GameState.PREGAME) {
					e.setCancelled(true);
					if (SkyWarsReloaded.getCfg().optionsMenuEnabled()) {
						new MainMenu(gPlayer);
					}
				}
            } else {
            	if (gPlayer.getGame().getState() == GameState.PREGAME) {
            		e.setCancelled(true);
            	}
            }
        }

        if (!gPlayer.inGame()) {
        	if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.STONE_PLATE) {
        		if (SkyWarsReloaded.getCfg().pressurePlateJoin()) {
        			Location spawn = SkyWarsReloaded.getCfg().getSpawn();
        			if (spawn != null) {
        				if (!SkyWarsReloaded.getCfg().signJoinMode()) {
       	                	World world = spawn.getWorld(); 
       	                    if (!gPlayer.inGame() && player.getLocation().getWorld().equals(world)) {
       	                        Game game = SkyWarsReloaded.getGC().findGame();
       	                        if (game != null) {
       	                            game.addPlayer(gPlayer);
       	                        } else {
       	                        	SkyWarsReloaded.getGC().addToQueue(gPlayer);
       	                        	gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.no-game-available"));
       	                        }
       	                    }
           	    		} else {
           	    			World world = spawn.getWorld();
       	                    if (!gPlayer.inGame() && player.getLocation().getWorld().equals(world)) {
       	                		Game game = findGame();
       	        	    		int i = 0;
       	        	    		while (i < 3) {
       	                    		if (game != null && game.getState() == GameState.PREGAME && !game.isFull()) {
       	            	                game.addPlayer(gPlayer);
       	            	                break;
       	            	    		} else {
       	            	    			i++;
       	            	    			game = findGame();
       	            	    		}
       	        	    		}
       	                    }
           	        	}
        			} else {
           				e.getPlayer().sendMessage(ChatColor.RED + "YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           				SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           			}
             	} 
        	}
                   	
            if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpectateItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpectateItem().getEnchantments().keySet()))) {
         		if (SkyWarsReloaded.getCfg().spectatingEnabled()) {
    					if (!gPlayer.isSpectating()) {
    						e.setCancelled(true);
    						new SpecGameMenu(gPlayer);
    					} 
    			}
         	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getJoinItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getJoinItem().getEnchantments().keySet()))) {
    			if (!gPlayer.isSpectating()) {
    					e.setCancelled(true);
    					new JoinMenu(gPlayer);
    			}
         	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getEnchantments().keySet()))) {
    				e.setCancelled(true);
    				if (SkyWarsReloaded.getCfg().lobbyMenuEnabled()) {
        				new LobbyMainMenu(gPlayer);
    				}
         	}
        }
        

    }
        
    private Game findGame() {
		Game game = null;
		int highest = 0;
		for (Game g: SkyWarsReloaded.getGC().getGames()) {
			if (highest <= g.getPlayers().size() && g.getState() == GameState.PREGAME && !g.isFull()) {
				highest = g.getPlayers().size();
				game = g;
			}
		}
		return game;
    }        
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(p.getUniqueId());
		ItemStack item = e.getItemDrop().getItemStack();
 	    if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getExitGameItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getExitGameItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpectateItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpectateItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getJoinItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getJoinItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
		} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getOptionsItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getOptionsItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
		} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getEnchantments().keySet()))) {
			e.setCancelled(true);
		}
		if (gPlayer.inGame()) {
			Game game = gPlayer.getGame();
			GameState gs = game.getState();
			if (gs == GameState.PREGAME) {
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item != null) {
     	    if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
         	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getExitGameItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getExitGameItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
	     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpectateItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpectateItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
	     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getJoinItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getJoinItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
			} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getOptionsItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getOptionsItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
			} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getEnchantments().keySet()))) {
				e.setCancelled(true);
			}
		}

	}

	@EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());

        if (gPlayer.inGame()) {
        	gPlayer.setBlocks(gPlayer.getBlocks() + 1);
        }
    }
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
		if (SkyWarsReloaded.getCfg().FallDamageDisabled()) {
			if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
				Player player = (Player) e.getEntity();
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
				if (gPlayer.inGame()) {
					e.setCancelled(true);
				}
			}
		} 
		
		if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
			Player player = (Player) e.getEntity();
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (gPlayer.inGame()) {
				Game game = gPlayer.getGame();
				if (game != null) {
					if (game.getState() == GameState.PREGAME) {
						e.setCancelled(true);
					}
				}
			}
		}

		if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.VOID)) {
			e.setDamage(20);
		}
	}
	
	@EventHandler		(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e){
		final Player player = e.getEntity();
		final Location loc = player.getLocation();
		final GamePlayer target = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
		Entity ent = e.getEntity();
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn != null) {
			String world = spawn.getWorld().getName();
			if(player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
				List<ItemStack> drops = e.getDrops();
				ListIterator<ItemStack> litr = drops.listIterator();
				while(litr.hasNext()){
		 	            ItemStack item = litr.next();
		     	    if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getKitMenuItem().getEnchantments().keySet()))) {
						litr.remove();
		         	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getExitGameItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getExitGameItem().getEnchantments().keySet()))) {
		         		litr.remove();
			     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getSpectateItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getSpectateItem().getEnchantments().keySet()))) {
			     		litr.remove();
			     	} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getJoinItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getJoinItem().getEnchantments().keySet()))) {
			     		litr.remove();
					} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getOptionsItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getOptionsItem().getEnchantments().keySet()))) {
						litr.remove();
					} else if ((item.getData().getItemType().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getData().getItemType()) &&  item.getEnchantments().keySet().equals(SkyWarsReloaded.getCfg().getLobbyMenuItem().getEnchantments().keySet()))) {
						litr.remove();
					}
		        }
			}
		}

		DamageCause damageCause = DamageCause.CUSTOM;
		if (ent.getLastDamageCause() != null) {
			damageCause = ent.getLastDamageCause().getCause();
		} 
		final DamageCause dCause = damageCause;
		if (target.inGame()) {
			e.setDeathMessage("");
			Game game = target.getGame();
			if (game.getState() == GameState.PLAYING) {
				target.getGame().onPlayerDeath(target, dCause, loc);
			}
		}
	}	
	
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	GamePlayer gplayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
    	if (gplayer.inGame()) {
    		GameState gs = gplayer.getGame().getState();
        	if (gs == GameState.PREGAME) {
        		e.setCancelled(true);
        	}
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	if (SkyWarsReloaded.getCfg().addScorePrefix()) {
    		if (SkyWarsReloaded.getCfg().useExternalChat()) {
    			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(event.getPlayer().getUniqueId());
    			int scoreValue = gPlayer.getScore();
            	String score;
            	if (scoreValue < 0) {
                    score = ChatColor.RED + "(" + gPlayer.getScore() + ")";
            	} else {
            		score = ChatColor.GREEN + "(+" + gPlayer.getScore() + ")";
            	}
            	String format = event.getFormat();
           		String world = gPlayer.getP().getWorld().getName();
           		for (Iterator<Player> iterator = event.getRecipients().iterator(); iterator.hasNext();) {
           			String world2 = iterator.next().getWorld().getName();

           			if (!world.equals(world2)) {
                        iterator.remove();
                    }
           		}
            	event.setFormat(score + format);
    		} else {
    			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(event.getPlayer().getUniqueId());
            	String name = event.getPlayer().getDisplayName();
            	String prefix = "";
            	if (SkyWarsReloaded.chat != null) {
                	if (SkyWarsReloaded.chat.getPlayerPrefix(event.getPlayer()) != null) {
                    	prefix = SkyWarsReloaded.chat.getPlayerPrefix(event.getPlayer());
                	} 
            	}
            	String colorMessage = ChatColor.translateAlternateColorCodes('&', event.getMessage());
            	String message = "";
            	if (gPlayer.getP().hasPermission("swr.color")) {
                	message = colorMessage;
            	} else {
            		message = ChatColor.stripColor(colorMessage);
            		while (message.contains("&")) {
            			message = ChatColor.translateAlternateColorCodes('&', message);
                		message = ChatColor.stripColor(message);
            		}
            	}
            	int scoreValue = gPlayer.getScore();
            	String score;
            	if (scoreValue < 0) {
                    score = ChatColor.RED + "(" + gPlayer.getScore() + ")";
            	} else {
            		score = ChatColor.GREEN + "(+" + gPlayer.getScore() + ")";
            	}
           		event.setCancelled(true);
           		if (gPlayer != null) {
               		World world = gPlayer.getP().getWorld();
               		if (world != null) {
                   		for (Player p: world.getPlayers()) {
                			p.sendMessage(new Messaging.MessageFormatter()
            				.setVariable("score", score)
            				.setVariable("prefix", prefix)
            				.setVariable("player", name)
            				.setVariable("message", message)
            				.format("chat"));
                   		}
               		}
           		}
    		}
		}
    }
    
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player sender = event.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(sender.getUniqueId());
            if (gPlayer.inGame()) {
            	String effect = gPlayer.getEffect();
            	if (effect == null) {
            	    return;
            	}
            	World world = sender.getWorld();
            	Location location = sender.getLocation();
            	Random random = new Random();
            		switch(effect) {
            		case "flame":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "FLAME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
                        	break;
            		case "smoke":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "SMOKE_LARGE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1);
                        	break;
            		case "portal":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "PORTAL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((140 - 80) + 80) + 1);
                        	break;
            		case "heart":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "HEART", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1);
                        	break;
            		case "critical":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "CRIT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 5) + 5) + 1);
                        	break;
            		case "water":
                        	SkyWarsReloaded.getNMS().sendParticles(world, "WATER_SPLASH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((14 - 8) + 8) + 1);
                        	break;
            		case "redstone":
            			SkyWarsReloaded.getNMS().sendParticles(world, "REDSTONE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1);
            			break;
            		case "sparks":
            			SkyWarsReloaded.getNMS().sendParticles(world, "FIREWORKS_SPARK", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
            			break;
            		case "lava_drip":
            			SkyWarsReloaded.getNMS().sendParticles(world, "DRIP_LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
            			break;
            		case "lava":
            			SkyWarsReloaded.getNMS().sendParticles(world, "LAVA", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 4) + 4) + 1);
            			break;
            		case "alphabet":
            			SkyWarsReloaded.getNMS().sendParticles(world, "ENCHANTMENT_TABLE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1);
            			break;
            		case "happy":
            			SkyWarsReloaded.getNMS().sendParticles(world, "VILLAGER_HAPPY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 4) + 4) + 1);
            			break;
            		case "magic":
            			SkyWarsReloaded.getNMS().sendParticles(world, "SPELL_WITCH", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 7) + 7) + 1);
            			break;
            		case "music":
            			SkyWarsReloaded.getNMS().sendParticles(world, "NOTE", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 4) + 4) + 1);
            			break;
            		case "angry":
            			SkyWarsReloaded.getNMS().sendParticles(world, "VILLAGER_ANGRY", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((5 - 2) + 2) + 1);
            			break;
            		case "clouds":
            			SkyWarsReloaded.getNMS().sendParticles(world, "CLOUD", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
            			break;
            		case "potion":
            			SkyWarsReloaded.getNMS().sendParticles(world, "SPELL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
            			break;
            		case "poison":
            			SkyWarsReloaded.getNMS().sendParticles(world, "SPELL_INSTANT", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
            			break;
            		case "snow":
            			SkyWarsReloaded.getNMS().sendParticles(world, "SNOWBALL", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
            			break;
            		case "slime":
            			SkyWarsReloaded.getNMS().sendParticles(world, "SLIME", (float) location.getX(), (float) location.getY(), (float) location.getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1);
            			break;
            		default: break;
            		}
            }
    }
    
	public void givePlayerItems(Player p) {
		if (SkyWarsReloaded.getCfg().giveSpectateItem()) {
			if (p.hasPermission("swr.spectate")) {
				p.getInventory().setItem(SkyWarsReloaded.getCfg().getSpectateItemSlot(), SkyWarsReloaded.getCfg().getSpectateItem());
			}
		}
		if (SkyWarsReloaded.getCfg().giveJoinMenuItem()) {
				p.getInventory().setItem(SkyWarsReloaded.getCfg().getJoinMenuSlot(), SkyWarsReloaded.getCfg().getJoinItem());
		}
		if (SkyWarsReloaded.getCfg().giveLobbyMenuItem()) {
			p.getInventory().setItem(SkyWarsReloaded.getCfg().getLobbyMenuSlot(), SkyWarsReloaded.getCfg().getLobbyMenuItem());
		}
		SkyWarsReloaded.getScore().getScoreboard(p);
	}
	
	public void removePlayerItems(Player player) {
		if (!SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).inGame()) {
			player.getInventory().remove(SkyWarsReloaded.getCfg().getSpectateItem());
			player.getInventory().remove(SkyWarsReloaded.getCfg().getJoinItem());
			player.getInventory().remove(SkyWarsReloaded.getCfg().getLobbyMenuItem());
			if (!SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).isSpectating()) {
				player.setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
			}
		}
	}
	
	public boolean inLobbyWorld(Player p) {
		Location spawn = SkyWarsReloaded.getCfg().getSpawn();
		if (spawn != null) {
			if (spawn.getWorld().equals(p.getWorld())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
    
}
