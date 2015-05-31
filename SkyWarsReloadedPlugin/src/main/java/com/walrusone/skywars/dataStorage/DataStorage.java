package com.walrusone.skywars.dataStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.GamePlayer;

public class DataStorage {

	public void savePlayerSync(final GamePlayer player) {
		boolean sqlEnabled = SkyWarsReloaded.get().getConfig().getBoolean("sqldatabase.enabled");
		if (!sqlEnabled) {
			try {
	            File dataDirectory = SkyWarsReloaded.get().getDataFolder();
	            File playerDataDirectory = new File(dataDirectory, "player_data");

	            if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
	                System.out.println("Failed to load player " + player.getName() + ": Could not create player_data directory.");
	                return;
	            }

	            File playerFile = new File(playerDataDirectory, player.getUUID().toString() + ".yml");
	            if (!playerFile.exists() && !playerFile.createNewFile()) {
	                System.out.println("Failed to load player " + player.getName() + ": Could not create player file.");
	                return;
	            }

	            copyDefaults(playerFile);
	            FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
	            fc.set("name", player.getName());
	            fc.set("wins", player.getWins());
	            fc.set("kills", player.getKills());
	            fc.set("deaths", player.getDeaths());
	            fc.set("gamesPlayed", player.getGamesPlayed());
	            fc.set("score", player.getScore());
	            fc.set("balance", player.getBalance());
	            double killDeath = 0;
	            if (player.getDeaths() != 0) {
	                killDeath = ((double) player.getKills())/player.getDeaths();
	            }
	            fc.set("killDeath", killDeath);
	            fc.set("blocksPlaced", player.getBlocks());
	            fc.set("permissions", player.getPerms());
	            fc.set("glasscolor", player.getGlass());
	            fc.set("effect", player.getEffect());
	            fc.set("traileffect", player.getProjEffect());
	            fc.save(playerFile);
	            
	        } catch (IOException ioException) {
	            System.out.println("Failed to load player " + player + ": " + ioException.getMessage());
	        }
		} else {
            Database database = SkyWarsReloaded.getDB();
            int playerId = 0;

            if (!database.checkConnection()) {
                return;
            }

            Connection connection = database.getConnection();
            PreparedStatement preparedStatement = null;

            try {
            	 StringBuilder queryBuilder = new StringBuilder();
                 queryBuilder.append("UPDATE `swreloaded_player` SET ");
                 queryBuilder.append("`playername` = ?, `score` = ?, `games_played` = ?, ");
                 queryBuilder.append("`games_won` = ?, `kills` = ?, ");
                 queryBuilder.append("`deaths` = ?, `killdeath` = ?, `blocksplaced` = ?, `last_seen` = NOW(), `balance` = ?, `glasscolor` = ?, `effect` = ?, `traileffect` = ? ");
                 queryBuilder.append("WHERE `uuid` = ?;");

                 preparedStatement = connection.prepareStatement(queryBuilder.toString());
                 preparedStatement.setString(1, player.getName());
                 preparedStatement.setInt(2, player.getScore());
                 preparedStatement.setInt(3, player.getGamesPlayed());
                 preparedStatement.setInt(4, player.getWins());
                 preparedStatement.setInt(5, player.getKills());
                 preparedStatement.setInt(6, player.getDeaths());
                 double killDeath = 0;
                 if (player.getDeaths() != 0) {
                 	killDeath = ((double) player.getKills())/player.getDeaths();
                 }
                 preparedStatement.setDouble(7, killDeath);
                 preparedStatement.setInt(8, player.getBlocks());
                 preparedStatement.setInt(9, player.getBalance());
                 preparedStatement.setString(10, player.getGlass());
                 preparedStatement.setString(11, player.getEffect());
                 preparedStatement.setString(12, player.getProjEffect());
                 preparedStatement.setString(13, player.getUUID().toString());
                 preparedStatement.executeUpdate();

            } catch (final SQLException sqlException) {
                sqlException.printStackTrace();

            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (final SQLException ignored) {
                    }
                }
            }
            
            connection = database.getConnection();
            preparedStatement = null;
            ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `player_id` ");
                    queryBuilder.append("FROM `swreloaded_player` ");
                    queryBuilder.append("WHERE `uuid` = ? ");
                    queryBuilder.append("LIMIT 1;");

                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    preparedStatement.setString(1, player.getP().getUniqueId().toString());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet != null && resultSet.next()) {
                        playerId = resultSet.getInt("player_id");
                    }
                } catch (final SQLException sqlException) {
                    sqlException.printStackTrace();
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (final SQLException ignored) {
                        }
                    }
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (final SQLException ignored) {
                        }
                    }
                }
        		if (player.getPerms().size() > 0) {
        			connection = database.getConnection();
                    preparedStatement = null;

                    try {
                    	if (player.getNewPerms().size() >= 1) {
                        	for (String perm: player.getNewPerms()) {
                        		StringBuilder queryBuilder = new StringBuilder();
                                queryBuilder.append("INSERT INTO `swreloaded_permissions` ");
                                queryBuilder.append("(`id`, `player_id`, `uuid`, `playername`, `permissions`) ");
                                queryBuilder.append("VALUES (NULL, ?, ?, ?, ?) ");
                                
                                preparedStatement = connection.prepareStatement(queryBuilder.toString());
                                preparedStatement.setInt(1, playerId);
                                preparedStatement.setString(2, player.getUUID().toString());
                                preparedStatement.setString(3, player.getName());
                                preparedStatement.setString(4, perm);
                                preparedStatement.executeUpdate();
                        	}
                        	player.clearNewPerms();
                    	}
                    } catch (final SQLException sqlException) {
                        sqlException.printStackTrace();

                    } finally {
                        if (preparedStatement != null) {
                            try {
                                preparedStatement.close();
                            } catch (final SQLException ignored) {
                            }
                        }
                    }
        		}
            }
        }

	public void savePlayerAsync(final UUID uuid, final String name, final int wins, final int kills, final int deaths, final int gamesPlayed, final int score, final int balance, final int blocks, final List<String> permissions, final List<String> newPermissions, final String glassColor, final String effect, final String trailEffect) {
		Bukkit.getScheduler().runTaskAsynchronously(SkyWarsReloaded.get(), new Runnable() {
            @Override
            public void run() {
        		boolean sqlEnabled = SkyWarsReloaded.get().getConfig().getBoolean("sqldatabase.enabled");
        		if (!sqlEnabled) {
        			try {
        	            File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        	            File playerDataDirectory = new File(dataDirectory, "player_data");

        	            if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
        	                System.out.println("Failed to load player " + name + ": Could not create player_data directory.");
        	                return;
        	            }

        	            File playerFile = new File(playerDataDirectory, uuid.toString() + ".yml");
        	            if (!playerFile.exists() && !playerFile.createNewFile()) {
        	                System.out.println("Failed to load player " + name + ": Could not create player file.");
        	                return;
        	            }

        	            copyDefaults(playerFile);
        	            FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
        	            fc.set("name", name);
        	            fc.set("wins", wins);
        	            fc.set("kills", kills);
        	            fc.set("deaths", deaths);
        	            fc.set("gamesPlayed", gamesPlayed);
        	            fc.set("score", score);
        	            fc.set("balance", balance);
        	            double killDeath = 0;
        	            if (deaths != 0) {
        	                killDeath = ((double) kills/deaths);
        	            }
        	            fc.set("killDeath", killDeath);
        	            fc.set("blocksPlaced", blocks);
        	            fc.set("permissions", permissions);
        	            fc.set("glasscolor", glassColor);
        	            fc.set("effect", effect);
        	            fc.set("traileffect", trailEffect);
        	            fc.save(playerFile);
        	            
        	        } catch (IOException ioException) {
        	            System.out.println("Failed to load player " + name + ": " + ioException.getMessage());
        	        }
        		} else {
                    Database database = SkyWarsReloaded.getDB();
                    int playerId = 0;
                    
                    if (!database.checkConnection()) {
                        return;
                    }

                    Connection connection = database.getConnection();
                    PreparedStatement preparedStatement = null;

                    try {
                   	 StringBuilder queryBuilder = new StringBuilder();
                     queryBuilder.append("UPDATE `swreloaded_player` SET ");
                     queryBuilder.append("`playername` = ?, `score` = ?, `games_played` = ?, ");
                     queryBuilder.append("`games_won` = ?, `kills` = ?, ");
                     queryBuilder.append("`deaths` = ?, `killdeath` = ?, `blocksplaced` = ?, `last_seen` = NOW(), `balance` = ?, `glasscolor` = ?, `effect` = ?, `traileffect` = ? ");
                     queryBuilder.append("WHERE `uuid` = ?;");

                     preparedStatement = connection.prepareStatement(queryBuilder.toString());
                     preparedStatement.setString(1, name);
                     preparedStatement.setInt(2, score);
                     preparedStatement.setInt(3, gamesPlayed);
                     preparedStatement.setInt(4, wins);
                     preparedStatement.setInt(5, kills);
                     preparedStatement.setInt(6, deaths);
                     double killDeath = 0;
                     if (deaths != 0) {
                     	killDeath = ((double) kills/deaths);
                     }
                     preparedStatement.setDouble(7, killDeath);
                     preparedStatement.setInt(8, blocks);
                     preparedStatement.setInt(9, balance);
                     preparedStatement.setString(10, glassColor);
                     preparedStatement.setString(11, effect);
                     preparedStatement.setString(12, trailEffect);
                     preparedStatement.setString(13, uuid.toString());
                     preparedStatement.executeUpdate();
                    } catch (final SQLException sqlException) {
                        sqlException.printStackTrace();

                    } finally {
                        if (preparedStatement != null) {
                            try {
                                preparedStatement.close();
                            } catch (final SQLException ignored) {
                            }
                        }
                    }
                    
                    connection = database.getConnection();
                    preparedStatement = null;
                    ResultSet resultSet = null;
                        try {
                            StringBuilder queryBuilder = new StringBuilder();
                            queryBuilder.append("SELECT `player_id` ");
                            queryBuilder.append("FROM `swreloaded_player` ");
                            queryBuilder.append("WHERE `uuid` = ? ");
                            queryBuilder.append("LIMIT 1;");

                            preparedStatement = connection.prepareStatement(queryBuilder.toString());
                            preparedStatement.setString(1, uuid.toString());
                            resultSet = preparedStatement.executeQuery();

                            if (resultSet != null && resultSet.next()) {
                                playerId = resultSet.getInt("player_id");
                            }
                        } catch (final SQLException sqlException) {
                            sqlException.printStackTrace();
                        } finally {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                } catch (final SQLException ignored) {
                                }
                            }
                            if (preparedStatement != null) {
                                try {
                                    preparedStatement.close();
                                } catch (final SQLException ignored) {
                                }
                            }
                        }
                		if (newPermissions.size() > 0) {
                			connection = database.getConnection();
                            preparedStatement = null;

                            try {
                            	if (newPermissions.size() >= 1) {
                                	for (String perm: newPermissions) {
                                		StringBuilder queryBuilder = new StringBuilder();
                                        queryBuilder.append("INSERT INTO `swreloaded_permissions` ");
                                        queryBuilder.append("(`id`, `player_id`, `uuid`, `playername`, `permissions`) ");
                                        queryBuilder.append("VALUES (NULL, ?, ?, ?, ?) ");
                                        
                                        preparedStatement = connection.prepareStatement(queryBuilder.toString());
                                        preparedStatement.setInt(1, playerId);
                                        preparedStatement.setString(2, uuid.toString());
                                        preparedStatement.setString(3, name);
                                        preparedStatement.setString(4, perm);
                                        preparedStatement.executeUpdate();
                                	}
                                	GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(uuid);
                                	if (gPlayer != null) {
                                		gPlayer.clearNewPerms();
                                	}
                            	}
                            } catch (final SQLException sqlException) {
                                sqlException.printStackTrace();

                            } finally {
                                if (preparedStatement != null) {
                                    try {
                                        preparedStatement.close();
                                    } catch (final SQLException ignored) {
                                    }
                                }
                            }
                		}
                    }
                }
		});
	}
	
	public void loadPlayer(final GamePlayer player) {
		boolean sqlEnabled = SkyWarsReloaded.get().getConfig().getBoolean("sqldatabase.enabled");
		if (sqlEnabled) {
	        Bukkit.getScheduler().runTaskAsynchronously(SkyWarsReloaded.get(), new Runnable() {
	            @Override
	            public void run() {
	                Database database = SkyWarsReloaded.getDB();

	                if (!database.checkConnection()) {
	                    return;
	                }

	                if (!database.doesPlayerExist(player.getP().getUniqueId().toString())) {
	                    database.createNewPlayer(player.getUUID().toString());

	                } else {
	                    Connection connection = database.getConnection();
	                    PreparedStatement preparedStatement = null;
	                    ResultSet resultSet = null;

	                    try {
	                        StringBuilder queryBuilder = new StringBuilder();
	                        queryBuilder.append("SELECT `score`, `games_played`, `games_won`, `kills`, `deaths`, `blocksplaced`, `balance`, `glasscolor`, `effect`, `traileffect` ");
	                        queryBuilder.append("FROM `swreloaded_player` ");
	                        queryBuilder.append("WHERE `uuid` = ? ");
	                        queryBuilder.append("LIMIT 1;");

	                        preparedStatement = connection.prepareStatement(queryBuilder.toString());
	                        preparedStatement.setString(1, player.getUUID().toString());
	                        resultSet = preparedStatement.executeQuery();

	                        if (resultSet != null && resultSet.next()) {
	                            player.setScore(resultSet.getInt("score"));
	                            player.setGamesPlayed(resultSet.getInt("games_played"));
	                            player.setWins(resultSet.getInt("games_won"));
	                            player.setKills(resultSet.getInt("kills"));
	                            player.setDeaths(resultSet.getInt("deaths"));
	                            player.setBlocks(resultSet.getInt("blocksplaced"));
	                            player.setBalance(resultSet.getInt("balance"));
	                            if (resultSet.getString("glasscolor") != null) {
		                            player.setGlass(resultSet.getString("glasscolor"));
	                            } else {
	                            	player.setGlass("normal");
	                            }
	                            if (resultSet.getString("effect") != null) {
		                            player.setEffect(resultSet.getString("effect"));
	                            } else {
	                            	player.setEffect("normal");
	                            }
	                            if (resultSet.getString("traileffect") != null) {
		                            player.setProjEffect(resultSet.getString("traileffect"));
	                            } else {
	                            	player.setProjEffect("normal");
	                            }  
	                        }

	                    } catch (final SQLException sqlException) {
	                        sqlException.printStackTrace();

	                    } finally {
	                        if (resultSet != null) {
	                            try {
	                                resultSet.close();
	                            } catch (final SQLException ignored) {
	                            }
	                        }

	                        if (preparedStatement != null) {
	                            try {
	                                preparedStatement.close();
	                            } catch (final SQLException ignored) {
	                            }
	                        }
	                    }
	                }
	                
	                database = SkyWarsReloaded.getDB();

	                if (!database.checkConnection()) {
	                    return;
	                }

	                    Connection connection = database.getConnection();
	                    PreparedStatement preparedStatement = null;
	                    ResultSet resultSet = null;

	                    try {
	                        StringBuilder queryBuilder = new StringBuilder();
	                        queryBuilder.append("SELECT `permissions` ");
	                        queryBuilder.append("FROM `swreloaded_permissions` ");
	                        queryBuilder.append("WHERE `uuid` = ?;");

	                        preparedStatement = connection.prepareStatement(queryBuilder.toString());
	                        preparedStatement.setString(1, player.getUUID().toString());
	                        resultSet = preparedStatement.executeQuery();

	                        List<String> perms = new ArrayList<String>();
	                        
	                        while (resultSet != null && resultSet.next()) {
	                            perms.add(resultSet.getString("permissions"));
	                        }
	                        player.setPerms(perms);
	                    } catch (final SQLException sqlException) {
	                        sqlException.printStackTrace();

	                    } finally {
	                        if (resultSet != null) {
	                            try {
	                                resultSet.close();
	                            } catch (final SQLException ignored) {
	                            }
	                        }

	                        if (preparedStatement != null) {
	                            try {
	                                preparedStatement.close();
	                            } catch (final SQLException ignored) {
	                            }
	                        }
	                    }
	                }
	        });
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(SkyWarsReloaded.get(), new Runnable() {
	            @Override
	            public void run() {
	    			try {
	    	            File dataDirectory = SkyWarsReloaded.get().getDataFolder();
	    	            File playerDataDirectory = new File(dataDirectory, "player_data");

	    	            if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
	    	                System.out.println("Failed to load player " + player.getName() + ": Could not create player_data directory.");
	    	                return;
	    	            }

	    	            File playerFile = new File(playerDataDirectory, player.getP().getUniqueId().toString() + ".yml");
	    	            if (!playerFile.exists() && !playerFile.createNewFile()) {
	    	                System.out.println("Failed to load player " + player.getName() + ": Could not create player file.");
	    	                return;
	    	            }

	    	            copyDefaults(playerFile);
	    	            FileConfiguration fc = YamlConfiguration.loadConfiguration(playerFile);
	    	            player.setWins(fc.getInt("wins"));
	    	            player.setKills(fc.getInt("kills"));
	    	            player.setDeaths(fc.getInt("deaths"));
	    	            player.setGamesPlayed(fc.getInt("gamesPlayed"));
	    	            player.setScore(fc.getInt("score"));
	    	            player.setBlocks(fc.getInt("blocksPlaced"));
	    	            player.setBalance(fc.getInt("balance"));
	    	            player.setPerms(fc.getStringList("permissions"));
                        if (fc.getString("glasscolor") != null) {
                            player.setGlass(fc.getString("glasscolor"));
                        } else {
                        	player.setGlass("normal");
                        }
                        if (fc.getString("effect") != null) {
                            player.setEffect(fc.getString("effect"));
                        } else {
                        	player.setEffect("normal");
                        }
                        if (fc.getString("traileffect") != null) {
                            player.setProjEffect(fc.getString("traileffect"));
                        } else {
                        	player.setProjEffect("normal");
                        }
	    	        } catch (IOException ioException) {
	    	            System.out.println("Failed to load player " + player + ": " + ioException.getMessage());
	    	        }
	            }
			});
		}
	}
	
	private void copyDefaults(File playerFile) {
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		Reader defConfigStream = new InputStreamReader(SkyWarsReloaded.get().getResource("playerFile.yml"));
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			playerConfig.options().copyDefaults(true);
			playerConfig.setDefaults(defConfig);
			try {
				playerConfig.save(playerFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
