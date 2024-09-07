package br.cotoko.clanterrenos.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import br.cotoko.clanterrenos.api.ComprarTerreno;
import net.streen.lib.api.TitleAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.util.UUID;

public class PlaceBase implements Listener {

    public static final String MUNDO_TERRENOS = "TerrenoClan";

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getWorld().getName().equals(MUNDO_TERRENOS)) {
            return;
        }

        UUID playerUUID = event.getPlayer().getUniqueId();
        Location blockLocation = event.getBlock().getLocation();
        ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(event.getPlayer());
        
        if (clanPlayer == null || clanPlayer.getClan() == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê não pertence a nenhum clã!");
            return;
        }

        Location playerTerrain = ComprarTerreno.getPlayerTerrains().values().stream()
                .filter(terrainLocation -> isWithinInteractableArea(blockLocation, terrainLocation, 
                        ComprarTerreno.getPlayerTerrainInteractableSizes().getOrDefault(playerUUID, 0)))
                .findFirst().orElse(null);

        if (playerTerrain == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê não pode colocar blocos aqui!");
            return;
        }

        UUID terrainOwnerUUID = ComprarTerreno.getPlayerTerrains().entrySet().stream()
                .filter(entry -> entry.getValue().equals(playerTerrain))
                .map(entry -> UUID.fromString(entry.getKey()))
                .findFirst().orElse(null);

        if (terrainOwnerUUID == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cErro ao encontrar o dono do terreno.");
            return;
        }

        ClanPlayer terrainOwnerClanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(terrainOwnerUUID);
        Clan terrainOwnerClan = SimpleClans.getInstance().getClanManager().getClanByPlayerName(terrainOwnerUUID.toString());

        if (terrainOwnerClan == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cErro ao encontrar o clã do dono do terreno.");
            return;
        }

        // Verifica se o jogador é líder do clã ou membro confiável
        if (!terrainOwnerClanPlayer.isLeader() && !terrainOwnerClanPlayer.isTrusted() && !clanPlayer.getClan().equals(terrainOwnerClan)) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cSomente o líder do clã ou membros confiáveis podem colocar blocos aqui!");
            return;
        }

        // Verifica se o bloco está dentro da área interativa do terreno
        int interactableSize = ComprarTerreno.getPlayerTerrainInteractableSizes().getOrDefault(terrainOwnerUUID, 0);
        if (!isWithinInteractableArea(blockLocation, playerTerrain, interactableSize)) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê só pode colocar blocos na área interativa do terreno!");
            return;
        }

        // Verifica se o bloco está na borda da área interativa
        if (isOnBorder(blockLocation, playerTerrain, interactableSize)) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê não pode colocar blocos na borda do terreno!");
            return;
        }
    }

    private boolean isWithinInteractableArea(Location blockLocation, Location terrainLocation, int interactableSize) {
        int terrainSize = ComprarTerreno.TAMANHO_TERRENO; // Tamanho total do terreno
        int centerX = terrainLocation.getBlockX() + terrainSize / 2;
        int centerZ = terrainLocation.getBlockZ() + terrainSize / 2;
        int halfInteractableSize = interactableSize / 2;

        int blockX = blockLocation.getBlockX();
        int blockZ = blockLocation.getBlockZ();

        // Verifica se o bloco está dentro dos limites do terreno e da área interativa
        boolean withinTerrain = blockX >= terrainLocation.getBlockX() && blockX < terrainLocation.getBlockX() + terrainSize &&
                                blockZ >= terrainLocation.getBlockZ() && blockZ < terrainLocation.getBlockZ() + terrainSize;

        // Ajuste para área interativa
        boolean withinInteractable = blockX >= centerX - halfInteractableSize && blockX < centerX + halfInteractableSize &&
                                      blockZ >= centerZ - halfInteractableSize && blockZ < centerZ + halfInteractableSize;

        return withinTerrain && withinInteractable;
    }

    private boolean isOnBorder(Location blockLocation, Location terrainLocation, int interactableSize) {
        int terrainSize = ComprarTerreno.TAMANHO_TERRENO; // Tamanho total do terreno
        int centerX = terrainLocation.getBlockX() + terrainSize / 2;
        int centerZ = terrainLocation.getBlockZ() + terrainSize / 2;
        int halfInteractableSize = interactableSize / 2;

        int blockX = blockLocation.getBlockX();
        int blockZ = blockLocation.getBlockZ();

        // Verifica se o bloco está na borda da área interativa
        boolean isOnEastWestBorder = blockX == centerX - halfInteractableSize || blockX == centerX + halfInteractableSize - 1;
        boolean isOnNorthSouthBorder = blockZ == centerZ - halfInteractableSize || blockZ == centerZ + halfInteractableSize - 1;

        return isOnEastWestBorder || isOnNorthSouthBorder;
    }
}
