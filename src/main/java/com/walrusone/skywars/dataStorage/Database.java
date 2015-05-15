package com.walrusone.skywars.dataStorage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.walrusone.skywars.SkyWarsReloaded;

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

	    public void close() {
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
	    
	    public void addColumn(String name) throws IOException, SQLException {
	        Statement statement = null;
	        if (name.equalsIgnoreCase("playername")) {
	        	try {
		            connection.setAutoCommit(false);
		            statement = connection.createStatement();

	                String query = "ALTER TABLE " + "swreloaded_player" + " ADD " + name + " VARCHAR(60) AFTER uuid";
	                statement.execute(query);

		            connection.commit();

		        } finally {
		            connection.setAutoCommit(true);

		            if (statement != null && !statement.isClosed()) {
		                statement.close();
		            }
		        }
	        }
	        if (name.equalsIgnoreCase("balance")) {
	        	try {
		            connection.setAutoCommit(false);
		            statement = connection.createStatement();

	                String query = "ALTER TABLE " + "swreloaded_player" + " ADD " + name + " INT(6) AFTER score";
	                statement.execute(query);

		            connection.commit();

		        } finally {
		            connection.setAutoCommit(true);

		            if (statement != null && !statement.isClosed()) {
		                statement.close();
		            }
		        }
	        }
	        if (name.equalsIgnoreCase("glasscolor")) {
	        	try {
		            connection.setAutoCommit(false);
		            statement = connection.createStatement();

	                String query = "ALTER TABLE " + "swreloaded_player" + " ADD " + name + " VARCHAR(60) AFTER blocksplaced";
	                statement.execute(query);

		            connection.commit();

		        } finally {
		            connection.setAutoCommit(true);

		            if (statement != null && !statement.isClosed()) {
		                statement.close();
		            }
		        }
	        }
	        if (name.equalsIgnoreCase("effect")) {
	        	try {
		            connection.setAutoCommit(false);
		            statement = connection.createStatement();

	                String query = "ALTER TABLE " + "swreloaded_player" + " ADD " + name + " VARCHAR(60) AFTER glasscolor";
	                statement.execute(query);

		            connection.commit();

		        } finally {
		            connection.setAutoCommit(true);

		            if (statement != null && !statement.isClosed()) {
		                statement.close();
		            }
		        }
	        }
	        if (name.equalsIgnoreCase("traileffect")) {
	        	try {
		            connection.setAutoCommit(false);
		            statement = connection.createStatement();

	                String query = "ALTER TABLE " + "swreloaded_player" + " ADD " + name + " VARCHAR(60) AFTER effect";
	                statement.execute(query);

		            connection.commit();

		        } finally {
		            connection.setAutoCommit(true);

		            if (statement != null && !statement.isClosed()) {
		                statement.close();
		            }
		        }
	        }
	    }

	    
	    public boolean doesPlayerExist(String uuid) {
	        if (!checkConnection()) {
	            return false;
	        }

	        int count = 0;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;

	        try {
	            StringBuilder queryBuilder = new StringBuilder();
	            queryBuilder.append("SELECT Count(`player_id`) ");
	            queryBuilder.append("FROM `swreloaded_player` ");
	            queryBuilder.append("WHERE `uuid` = ? ");
	            queryBuilder.append("LIMIT 1;");

	            preparedStatement = connection.prepareStatement(queryBuilder.toString());
	            preparedStatement.setString(1, uuid);
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

	    public void createNewPlayer(String uid) {
	        if (!checkConnection()) {
	            return;
	        }

	        UUID uuid = UUID.fromString(uid);
	        PreparedStatement preparedStatement = null;

	        try {
	            StringBuilder queryBuilder = new StringBuilder();
	            queryBuilder.append("INSERT INTO `swreloaded_player` ");
	            queryBuilder.append("(`player_id`, `uuid`, `playername`, `first_seen`, `last_seen`) ");
	            queryBuilder.append("VALUES ");
	            queryBuilder.append("(NULL, ?, ?, NOW(), NOW());");

	            preparedStatement = connection.prepareStatement(queryBuilder.toString());
	            preparedStatement.setString(1, uid);
	            preparedStatement.setString(2, SkyWarsReloaded.get().getServer().getPlayer(uuid).getName());
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
