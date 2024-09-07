package br.cotoko.clanterrenos.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import br.cotoko.clanterrenos.Main;
import br.cotoko.clanterrenos.api.ComprarTerreno;

public class BancoDados implements Listener {

    public static Connection connection;

    private static Main plugin;

    public BancoDados(Main plugin) {
        BancoDados.plugin = plugin;
    }

    public void Iniciar() {
        try {
            Class.forName("org.sqlite.JDBC");
            Bukkit.getConsoleSender().sendMessage("§a[BASE] Driver SQLite registrado com sucesso.");

            connection = DriverManager.getConnection("jdbc:sqlite:plugins/StreenBaseClans/terrenos.db");
            Bukkit.getConsoleSender().sendMessage("§a[BASE] Conexão com o banco de dados SQLite estabelecida com sucesso.");

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS terrenos (" +
                        "player_name TEXT PRIMARY KEY," +
                        "terrain_x INTEGER," +
                        "terrain_y INTEGER," +
                        "terrain_z INTEGER," +
                        "interactable_size INTEGER," +
                        "upgrade_level INTEGER," +
                        "clan_name TEXT" +
                        ");");
                Bukkit.getConsoleSender().sendMessage("§a[BASE] Tabela 'terrenos' verificada/criada com sucesso.");
            }
        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("§c[BASE] Driver SQLite não encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§c[BASE] Erro ao estabelecer conexão.");
            e.printStackTrace();
        }
    }

    public void CarregarTerrenos() {
        String query = "SELECT player_name, terrain_x, terrain_y, terrain_z, interactable_size, upgrade_level FROM terrenos";

        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String playerName = rs.getString("player_name");
                Location terrainLocation = new Location(Bukkit.getServer().getWorld("TerrenoClan"), rs.getInt("terrain_x"), rs.getInt("terrain_y"), rs.getInt("terrain_z"));
                int interactableSize = rs.getInt("interactable_size");
                int upgradeLevel = rs.getInt("upgrade_level");

                if (terrainLocation == null || terrainLocation.getWorld() == null) {
                    System.out.println("A localização ou o mundo é nulo para o jogador: " + playerName);
                    continue;
                }
                ComprarTerreno.playerTerrains.put(playerName, terrainLocation);
                ComprarTerreno.claimedTerrains.put(terrainLocation, playerName);
                ComprarTerreno.playerTerrainInteractableSizes.put(playerName, interactableSize);
                ComprarTerreno.playerTerrainUpgradeLevels.put(playerName, upgradeLevel);
                ComprarTerreno.marcacaoAreaInterativa(terrainLocation, interactableSize);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void SalvarTerrenos() {
        String insertQuery = "INSERT OR REPLACE INTO terrenos (player_name, terrain_x, terrain_y, terrain_z, interactable_size, upgrade_level, clan_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM terrenos WHERE player_name = ?";

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery); PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                for (String playerName : ComprarTerreno.playerTerrains.keySet()) {
                    Location terrainLocation = ComprarTerreno.playerTerrains.get(playerName);
                    int interactableSize = ComprarTerreno.playerTerrainInteractableSizes.get(playerName);
                    int upgradeLevel = ComprarTerreno.playerTerrainUpgradeLevels.get(playerName);
                    String clanName = getClanNameForPlayer(playerName);
                    insertStmt.setString(1, playerName);
                    insertStmt.setInt(2, terrainLocation.getBlockX());
                    insertStmt.setInt(3, terrainLocation.getBlockY());
                    insertStmt.setInt(4, terrainLocation.getBlockZ());
                    insertStmt.setInt(5, interactableSize);
                    insertStmt.setInt(6, upgradeLevel);
                    insertStmt.setString(7, clanName);
                    insertStmt.addBatch();
                }

                for (String playerName : ComprarTerreno.playerTerrains.keySet()) {
                    if (!ComprarTerreno.playerTerrains.containsKey(playerName)) {
                        deleteStmt.setString(1, playerName);
                        deleteStmt.addBatch();
                    }
                }

                insertStmt.executeBatch();
                deleteStmt.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void AtualizarTamanhoInterativo(String playerName, int novoTamanho) {
        String query = "UPDATE terrenos SET interactable_size = ? WHERE player_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, novoTamanho);
            stmt.setString(2, playerName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Location getClanBaseLocation(String clanName) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getDatabaseConnection();
            if (connection == null) {
                plugin.getLogger().warning("A conexão com o banco de dados não foi estabelecida.");
                return null;
            }
            statement = connection.prepareStatement(
                    "SELECT terrain_x, terrain_y, terrain_z FROM terrenos WHERE clan_name = ?"
            );
            statement.setString(1, clanName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int x = resultSet.getInt("terrain_x");
                int y = resultSet.getInt("terrain_y");
                int z = resultSet.getInt("terrain_z");

                return new Location(Bukkit.getWorld("TerrenoClan"), x, y + 1, z);
            } else {
                plugin.getLogger().info("Não foi possível encontrar a base do clã para o clã: " + clanName);
                return null;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public boolean isSameClan(String playerClanName, String ownerName) {
        String query = "SELECT clan_name FROM terrenos WHERE player_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ownerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ownerClanName = rs.getString("clan_name");
                return playerClanName != null && playerClanName.equals(ownerClanName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static Connection getDatabaseConnection() throws SQLException {
        String url = "jdbc:sqlite:plugins/StreenBaseClans/terrenos.db";
        return DriverManager.getConnection(url);
    }

    public static String getClanNameForPlayer(String playerName) {
        String query = "SELECT clan_name FROM terrenos WHERE player_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("clan_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClanLeader(String clanName) {
        String query = "SELECT player_name FROM terrenos WHERE clan_name = ? AND upgrade_level = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, clanName);
            stmt.setInt(2, 0); // Assumindo que upgrade_level 0 é o líder principal
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("player_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void FecharDataBase() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
