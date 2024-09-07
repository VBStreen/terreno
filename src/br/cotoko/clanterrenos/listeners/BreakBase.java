package br.cotoko.clanterrenos.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import br.cotoko.clanterrenos.api.ComprarTerreno;
import net.streen.lib.api.TitleAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class BreakBase implements Listener {

    private static final String MUNDO_TERRENOS = "TerrenoClan";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals(MUNDO_TERRENOS)) {
            return;
        }

        UUID playerUUID = event.getPlayer().getUniqueId();
        Location blockLocation = event.getBlock().getLocation();
        Location playerTerrain = ComprarTerreno.getPlayerTerrains().values().stream()
                .filter(terrainLocation -> isWithinInteractableArea(blockLocation, terrainLocation, 
                        ComprarTerreno.getPlayerTerrainInteractableSizes().getOrDefault(playerUUID, 0)))
                .findFirst().orElse(null);

        if (playerTerrain == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê não pode quebrar este bloco!");
            return;
        }
        ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(event.getPlayer());
        if (clanPlayer == null || !clanPlayer.isTrusted()) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cSomente membros confiáveis podem quebrar blocos aqui!");
            return;
        }
        String terrainOwnerUUID = ComprarTerreno.getPlayerTerrains().entrySet().stream()
                .filter(entry -> entry.getValue().equals(playerTerrain))
                .map(entry -> entry.getKey())
                .findFirst().orElse(null);

        if (terrainOwnerUUID == null) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cErro ao encontrar o dono do terreno.");
            return;
        }

        Clan terrainOwnerClan = SimpleClans.getInstance().getClanManager().getClanByPlayerName(terrainOwnerUUID);
        if (terrainOwnerClan == null || !terrainOwnerClan.isMember(event.getPlayer())) {
            event.setCancelled(true);
            TitleAPI.get().sendTitle(event.getPlayer(), "§c§lSTREEN BASE", "§cVocê não é membro deste clã!");
            return;
        }
    }

    public static boolean isWithinInteractableArea(Location blockLocation, Location terrainLocation, int interactableSize) {
        int centerX = terrainLocation.getBlockX() + ComprarTerreno.TAMANHO_TERRENO / 2;
        int centerZ = terrainLocation.getBlockZ() + ComprarTerreno.TAMANHO_TERRENO / 2;
        int halfInteractableSize = interactableSize / 2;

        int blockX = blockLocation.getBlockX();
        int blockZ = blockLocation.getBlockZ();

        return blockX >= centerX - halfInteractableSize && blockX <= centerX + halfInteractableSize &&
               blockZ >= centerZ - halfInteractableSize && blockZ <= centerZ + halfInteractableSize;
    }
}
