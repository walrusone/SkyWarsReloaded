package com.walrusone.skywarsreloaded.menus;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
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

import java.util.ArrayList;
import java.util.List;

public class TeamSelectionMenu {

    private static int menuSize = 27;
    private static final String menuName = new Messaging.MessageFormatter().format("menu.teamselection-menu-title");

    public TeamSelectionMenu(String key, GameMap gMap) {
    	Inventory menu = Bukkit.createInventory(null, menuSize + 9, menuName);
    	ArrayList<Inventory> invs = new ArrayList<>();
    	invs.add(menu);
    	
    	Runnable update = () -> {
            if ((SkyWarsReloaded.getIC().hasViewers("jointeammenu") || SkyWarsReloaded.getIC().hasViewers("spectateteammenu"))) {
                ArrayList<GameMap> games = GameMap.getPlayableArenas(GameType.TEAM);
                ArrayList<Inventory> invs1 = SkyWarsReloaded.getIC().getMenu("jointeammenu").getInventories();

                for (Inventory inv: invs1) {
                    for (int i = 0; i < menuSize; i++) {
                        inv.setItem(i, new ItemStack(Material.AIR, 1));
                    }
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
				return;
			}

			if (player.hasPermission("sw.join")) {
			    if (event.getClick() == ClickType.RIGHT) {

                } else {
                    boolean joined;
                    Party party = Party.getParty(player);
                    if (party != null) {
                        if (party.getLeader().equals(player.getUniqueId())) {
                            if (gMap.getMatchState() == MatchState.WAITINGSTART && gMap.canAddParty(party)) {
                                player.closeInventory();
                                joined = gMap.addPlayers(party);
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
                            joined = gMap.addPlayers(player);
                            if (!joined) {
                                player.sendMessage(new Messaging.MessageFormatter().format("error.could-not-join2"));
                            }
                        }
                    }
                }
			}
		});
        SkyWarsReloaded.getIC().getMenu("jointeammenu").setUpdate(update);
    }

}
