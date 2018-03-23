package com.walrusone.skywarsreloaded.menus;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeamSelectionMenu {

    private static int menuSize = 27;

    public TeamSelectionMenu(String key, GameMap gMap) {
        String menuName = new Messaging.MessageFormatter().setVariable("mapname", gMap.getDisplayName()).format("menu.teamselection-menu-title");
        if (SkyWarsReloaded.getNMS().isOnePointEight()) {
            if (menuName.length() > 32) {
                menuName = menuName.substring(0, 31);
            }
        }
        Inventory menu = Bukkit.createInventory(null, menuSize + 9, menuName);
    	ArrayList<Inventory> invs = new ArrayList<>();
    	invs.add(menu);
    	
    	Runnable update = () -> {
            if ((SkyWarsReloaded.getIC().hasViewers(key))) {
                ArrayList<Inventory> invs1 = SkyWarsReloaded.getIC().getMenu(key).getInventories();
                for (Inventory inv: invs1) {
                    for (int i = 0; i < menuSize; i++) {
                        inv.setItem(i, new ItemStack(Material.AIR, 1));
                    }
                }
                Material mat = SkyWarsReloaded.getCfg().getTeamMaterial();
                List<String> lores = new ArrayList<>();
                for (TeamCard tCard: gMap.getTeamCards()) {
                    String name = tCard.getTeamName();
                    byte color = tCard.getByte();
                    ItemStack item = new ItemStack(mat, 1, (short) color);
                    lores.clear();
                    lores.add((new Messaging.MessageFormatter().setVariable("playercount", "" + tCard.getPlayersSize())
                            .setVariable("maxplayers", "" + tCard.getSize()).format("signs.line4")));
                    for (PlayerCard pCard: tCard.getPlayerCards()) {
                        Player p = pCard.getPlayer();
                        if (p != null) {
                            lores.add(ChatColor.GREEN + p.getName());
                        }
                    }
                    invs1.get(0).setItem(tCard.getPosition(), SkyWarsReloaded.getNMS().getItemStack(item, lores, name));
                }
            }
        };
  
        SkyWarsReloaded.getIC().create(key, invs, event -> {
			Player player = event.getPlayer();

			String name = event.getName();
			if (name.equalsIgnoreCase(SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("exitMenuItem")))) {
				player.closeInventory();
				return;
			}

			if (gMap.getMatchState() != MatchState.WAITINGSTART) {
				Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getErrorSound(), 1, 1);
                if (!SkyWarsReloaded.getIC().hasViewers("jointeammenu")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SkyWarsReloaded.getIC().getMenu("jointeammenu").update();
                        }
                    }.runTaskLater(SkyWarsReloaded.get(), 5);
                }
                SkyWarsReloaded.getIC().show(player, "jointeammenu");
				return;
			}

			TeamCard tCard = gMap.getTeamCardFromName(name);

			if (tCard == null) {
			    return;
            }

			if (player.hasPermission("sw.join")) {
                boolean joined;
                Party party = Party.getParty(player);
                if (party != null) {
                    if (party.getLeader().equals(player.getUniqueId())) {
                        if (gMap.getMatchState() == MatchState.WAITINGSTART && gMap.canAddParty(party)) {
                            player.closeInventory();
                            joined = gMap.addPlayers(tCard, party);
                            if (!joined) {
                                player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join2"));
                            }
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(new Messaging.MessageFormatter().format("party.onlyleader"));
                    }
                } else {
                    if (gMap.getMatchState() == MatchState.WAITINGSTART && gMap.canAddPlayer()) {
                        player.closeInventory();
                        joined = gMap.addPlayers(tCard, player);
                        if (!joined) {
                            player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join2"));
                        }
                    }
                }
			}
		});
        SkyWarsReloaded.getIC().getMenu(key).setUpdate(update);
    }

}
