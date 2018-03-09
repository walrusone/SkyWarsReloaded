package com.walrusone.skywarsreloaded.database;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;

public class Database {
	
	    private final String connectionUri;
	    private final String username;
	    private final String password;
	    private Connection connection;

	    public Database() throws ClassNotFoundException, SQLException {
	        final String hostname = SkyWarsReloaded.get().getConfig().getString("sqldatabase.hostname");
	        final int port = SkyWarsReloaded.get().getConfig().getInt("sqldatabase.port");
	        final String database = SkyWarsReloaded.get().getConfig().getString("sqldatabase.database");

	        connectionUri = String.format("jdbc:mysql://%s:%d/%s", hostname, port, database);
	        username = SkyWarsReloaded.get().getConfig().getString("sqldatabase.username");
	        password = SkyWarsReloaded.get().getConfig().getString("sqldatabase.password");

	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	            connect();

	        } catch (SQLException sqlException) {
	            close();
	            throw sqlException;
	        }
	    }

	    private void connect() throws SQLException {
	        if (connection != null) {
	            try {
	                connection.createStatement().execute("SELECT 1;");

	            } catch (SQLException sqlException) {
	                if (sqlException.getSQLState().equals("08S01")) {
	                    try {
	                        connection.close();

	                    } catch (SQLException ignored) {
	                    }
	                }
	            }
	        }

	        if (connection == null || connection.isClosed()) {
	            connection = DriverManager.getConnection(connectionUri, username, password);
	        }
	    }

	    public Connection getConnection() {
	        return connection;
	    }

	    private void close() {
	        try {
	            if (connection != null && !connection.isClosed()) {
	                connection.close();
	            }

	        } catch (SQLException ignored) {

	        }

	        connection = null;
	    }

	    public boolean checkConnection() {
	        try {
	            connect();
	        } catch (SQLException sqlException) {
	            close();
	            sqlException.printStackTrace();
				return false;
	        }
	        return true;
	    }

	    public void createTables() throws IOException, SQLException {
	        URL resource = Resources.getResource(SkyWarsReloaded.class, "/tables.sql");
	        String[] databaseStructure = Resources.toString(resource, Charsets.UTF_8).split(";");

	        if (databaseStructure.length == 0) {
	            return;
	        }

	        Statement statement = null;

	        try {
	            connection.setAutoCommit(false);
	            statement = connection.createStatement();

	            for (String query : databaseStructure) {
	                query = query.trim();

	                if (query.isEmpty()) {
	                    continue;
	                }

	                statement.execute(query);
	            }

	            connection.commit();

	        } finally {
	            connection.setAutoCommit(true);

	            if (statement != null && !statement.isClosed()) {
	                statement.close();
	            }
	        }
	    }
	    
	    public boolean doesPlayerExist(String fId) {
	        if (!checkConnection()) {
	            return false;
	        }

	        int count = 0;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;

	        try {
	            StringBuilder queryBuilder = new StringBuilder();
	            queryBuilder.append("SELECT Count(`player_id`) ");
	            queryBuilder.append("FROM `sw_player` ");
	            queryBuilder.append("WHERE `uuid` = ? ");
	            queryBuilder.append("LIMIT 1;");

	            preparedStatement = connection.prepareStatement(queryBuilder.toString());
	            preparedStatement.setString(1, fId);
	            resultSet = preparedStatement.executeQuery();

	            if (resultSet.next()) {
	                count = resultSet.getInt(1);
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

	        return count > 0;
	    }

	    public void createNewPlayer(String fId, String name) {
	        if (!checkConnection()) {
	            return;
	        }

	        PreparedStatement preparedStatement = null;

	        try {
	            StringBuilder queryBuilder = new StringBuilder();
	            queryBuilder.append("INSERT INTO `sw_player` ");
	            queryBuilder.append("(`player_id`, `uuid`, `player_name`, `wins`, `losses`, `kills`, `deaths`, `elo`, `xp`, `pareffect`, `proeffect`, `glasscolor`, `killsound`, `winsound`, `taunt`) ");
	            queryBuilder.append("VALUES ");
	            queryBuilder.append("(NULL, ?, ?, 0, 0, 0, 0, 1500, 0, ?, ?, ?, ?, ?, ?);");
	            preparedStatement = connection.prepareStatement(queryBuilder.toString());
	            preparedStatement.setString(1, fId);
	            preparedStatement.setString(2, name);
	            preparedStatement.setString(3, "none");
	            preparedStatement.setString(4, "none");
	            preparedStatement.setString(5, "none");
	            preparedStatement.setString(6, "none");
	            preparedStatement.setString(7, "none");
	            preparedStatement.setString(8, "none");
	            
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
	    }
	    
}
