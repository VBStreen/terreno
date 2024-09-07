package br.cotoko.clanterrenos.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;

public class DispensarTerreno implements Listener {

    public static void LiberarTerreno(Location location) {
        String playerName = ComprarTerreno.claimedTerrains.remove(location);
        if (playerName != null) {
            ComprarTerreno.playerTerrains.remove(playerName);
            ComprarTerreno.playerTerrainInteractableSizes.remove(playerName);
            ComprarTerreno.availableTerrains.add(location);
            RemoverMarcacaoUpgrade(location, ComprarTerreno.TAMANHO_TERRENO);
            ComprarTerreno.CriarBorda(location.getWorld(), location.getBlockX(), location.getBlockZ(), ComprarTerreno.TAMANHO_TERRENO);
        }
    }

    public static void RemoverMarcacaoUpgrade(Location location, int size) {
        World world = location.getWorld();
        int startX = location.getBlockX() + (ComprarTerreno.TAMANHO_TERRENO - size) / 2;
        int startZ = location.getBlockZ() + (ComprarTerreno.TAMANHO_TERRENO - size) / 2;
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                if (x == startX || x == startX + size - 1 || z == startZ || z == startZ + size - 1) {
                    world.getBlockAt(x, ComprarTerreno.ALTURA_TERRENO, z).setType(Material.GRASS);
                }
            }
        }
    }

    public static void ResetarTerreno(Location location) {
        World world = location.getWorld();
        int startX = location.getBlockX();
        int startY = 0;
        int startZ = location.getBlockZ();
        int size = ComprarTerreno.TAMANHO_TERRENO;

        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                for (int y = 0; y < 256; y++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                world.getBlockAt(x, startY, z).setType(Material.BEDROCK);
            }
        }
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                for (int y = startY + 1; y <= startY + 2; y++) {
                    world.getBlockAt(x, y, z).setType(Material.DIRT);
                }
            }
        }
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                world.getBlockAt(x, startY + 3, z).setType(Material.GRASS);
            }
        }
        ComprarTerreno.CriarBordaLiberar(world, startX, startZ, size, startY);
    }
}
