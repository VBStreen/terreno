package br.cotoko.clanterrenos.commands;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import pl.merbio.charsapi.objects.CharsBuilder;
import pl.merbio.charsapi.objects.CharsString;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.cotoko.clanterrenos.Main;
import br.cotoko.clanterrenos.api.ComprarTerreno;
import br.cotoko.clanterrenos.sql.BancoDados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class ClaimCommand implements CommandExecutor {

    private final Main plugin;
    CharsBuilder builder = new CharsBuilder();

    public ClaimCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();  // Use playerName instead of playerUUID
            ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player.getPlayer());
            String nameClan = clanPlayer.getClan().getName();
            String tag = clanPlayer.getClan().getColorTag();
            
            if (!clanPlayer.isLeader()) {
                player.sendMessage("Só líder pode usar este comando.");
                return true;
            }
            if (tag == null) {
                player.sendMessage("Tag do clã não pode ser nula.");
                return true;
            }

            List<Location> availableTerrains = ComprarTerreno.getAvailableTerrains();
            if (availableTerrains.isEmpty()) {
                player.sendMessage("Todos os terrenos estão ocupados.");
                return true;
            }

            if (ComprarTerreno.getPlayerTerrains().containsKey(playerName)) {  // Use playerName here
                player.sendMessage("Você já possui um terreno.");
                return true;
            }

            Random random = new Random();
            Location location = availableTerrains.get(random.nextInt(availableTerrains.size()));
            ComprarTerreno.claimTerrain(playerName, location);  // Use playerName here

            Location teleportLocation = location.clone();
            teleportLocation.add(21.5, 2, 50.5);
            teleportLocation.setYaw(180);
            player.teleport(teleportLocation);

            Location spawnClan = location.clone();
            spawnClan.add(20.5, 7, -5);

            CharsString textCS = builder.replace("#c" + tag);
            builder.build(spawnClan, textCS);

            player.sendMessage("Você foi teletransportado e agora é o dono deste terreno!");

            try {
                Connection connection = BancoDados.getDatabaseConnection();
                String insertQuery = "INSERT OR REPLACE INTO terrenos (player_name, terrain_x, terrain_y, terrain_z, interactable_size, upgrade_level, clan_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                    stmt.setString(1, playerName);  // Use playerName here
                    stmt.setInt(2, location.getBlockX());
                    stmt.setInt(3, location.getBlockY());
                    stmt.setInt(4, location.getBlockZ());
                    stmt.setInt(5, ComprarTerreno.AREA_INTERATIVA);
                    stmt.setInt(6, 0);
                    stmt.setString(7, nameClan);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage("Erro ao salvar o terreno no banco de dados.");
                return true;
            }

            plugin.getLogger().info("Terreno reivindicado por " + player.getName() + " em " + location.toString());
        } else {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
        }
        return true;
    }
}
