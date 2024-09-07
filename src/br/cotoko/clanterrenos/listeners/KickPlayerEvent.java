package br.cotoko.clanterrenos.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.cotoko.clanterrenos.Main;
import br.cotoko.clanterrenos.api.ComprarTerreno;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerKickedClanEvent;

public class KickPlayerEvent implements Listener {
    
    public final Main plugin;
    private static final Location SAFE_TELEPORT_LOCATION = new Location(Bukkit.getWorld("world"), 100, 65, 100); // Substitua pelas coordenadas específicas

    public KickPlayerEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKickedFromClan(PlayerKickedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Player player = clanPlayer.toPlayer();
        if (player != null && player.isOnline()) {
            UUID playerUUID = player.getUniqueId();
            Location playerLocation = player.getLocation();
            Location playerTerrain = ComprarTerreno.getPlayerTerrains().get(playerUUID);

            if (playerTerrain != null && isWithinInteractableArea(playerLocation, playerTerrain, ComprarTerreno.getPlayerTerrainInteractableSizes().get(playerUUID))) {
                player.teleport(SAFE_TELEPORT_LOCATION);
                player.closeInventory();
            }
        }
    }


    public static boolean isWithinInteractableArea(Location playerLocation, Location terrainLocation, int interactableSize) {
        int centerX = terrainLocation.getBlockX() + ComprarTerreno.TAMANHO_TERRENO / 2;
        int centerZ = terrainLocation.getBlockZ() + ComprarTerreno.TAMANHO_TERRENO / 2;
        int halfInteractableSize = interactableSize / 2;

        int playerX = playerLocation.getBlockX();
        int playerZ = playerLocation.getBlockZ();

        return playerX >= centerX - halfInteractableSize && playerX <= centerX + halfInteractableSize &&
               playerZ >= centerZ - halfInteractableSize && playerZ <= centerZ + halfInteractableSize;
    }
}
