package br.cotoko.clanterrenos.commands;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.cotoko.clanterrenos.Main;
import br.cotoko.clanterrenos.api.ComprarTerreno;
import br.cotoko.clanterrenos.api.DispensarTerreno;
import br.cotoko.clanterrenos.sql.BancoDados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DispensarCommand implements CommandExecutor {

    private final Main plugin;

    public DispensarCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName(); // Use playerName instead of playerUUID
            ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player.getPlayer());

            if (clanPlayer == null || clanPlayer.getClan() == null) {
                player.sendMessage("Você não pertence a nenhum clã.");
                return true;
            }
            if (ComprarTerreno.getPlayerTerrains().containsKey(playerName)) { // Use playerName here
                String clanName = clanPlayer.getClan().getName();
                removeSpawnClanBlocks(clanName);

                Location location = ComprarTerreno.getPlayerTerrains().get(playerName); // Use playerName here
                DispensarTerreno.ResetarTerreno(location);
                DispensarTerreno.LiberarTerreno(location);
                try (Connection connection = BancoDados.getDatabaseConnection()) {
                    String deleteQuery = "DELETE FROM terrenos WHERE player_name = ?"; // Changed to player_name
                    try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                        stmt.setString(1, playerName); // Use playerName here
                        stmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage("Erro ao remover o terreno do banco de dados.");
                    return true;
                }

                player.sendMessage("Clã dispensado e blocos removidos.");
                plugin.getLogger().info("Clã dispensado por " + player.getName() + ".");
            } else {
                player.sendMessage("Você não possui um terreno.");
            }
        } else {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
        }
        return true;
    }

    private void removeSpawnClanBlocks(String clanName) {
        Location spawnLocation = getSpawnClanLocation(clanName);

        if (spawnLocation != null) {
            int radius = 7;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location loc = spawnLocation.clone().add(x + 20.5, y + 15, z - 5);
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    private Location getSpawnClanLocation(String clanName) {
        try (Connection connection = BancoDados.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT terrain_x, terrain_y, terrain_z FROM terrenos WHERE clan_name = ?"
             )) {
            statement.setString(1, clanName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int x = resultSet.getInt("terrain_x");
                int y = resultSet.getInt("terrain_y");
                int z = resultSet.getInt("terrain_z");
                return new Location(Bukkit.getWorld("TerrenoClan"), x, y, z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
