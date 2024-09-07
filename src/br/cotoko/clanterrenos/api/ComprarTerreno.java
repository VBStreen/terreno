package br.cotoko.clanterrenos.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;

public class ComprarTerreno implements Listener {

    public static final int TAMANHO_TERRENO = 44;
    public static final int AREA_INTERATIVA = 24;
    public static final int TAMANHO_TERRENO_MAX = 44;
    public static final int DISTANCIA_TERRENO = 85;
    public static final int ALTURA_TERRENO = 3;
    public static final int TAMANHO_UPGRADE = 2;

    public static List<Location> availableTerrains = new ArrayList<>();
    public static HashMap<String, Location> playerTerrains = new HashMap<>();
    public static HashMap<Location, String> claimedTerrains = new HashMap<>();
    public static HashMap<String, Integer> playerTerrainInteractableSizes = new HashMap<>();
    public static HashMap<String, Integer> playerTerrainUpgradeLevels = new HashMap<>();

    private HashMap<String, String> clanLeaders = new HashMap<>();

    public static void CriarBorda(World world, int startX, int startZ, int size) {
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                if (x == startX || x == startX + size - 1 || z == startZ || z == startZ + size - 1) {
                    world.getBlockAt(x, ALTURA_TERRENO, z).setType(Material.BEDROCK);
                }
            }
        }
    }

    public static void CriarBordaLiberar(World world, int startX, int startZ, int size, int startY) {
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                if (x == startX || x == startX + size - 1 || z == startZ || z == startZ + size - 1) {
                    world.getBlockAt(x, startY, z).setType(Material.BEDROCK);
                }
            }
        }
    }
    
    public static void claimTerrain(String playerName, Location location) {
        if (availableTerrains.contains(location)) {
            if (playerTerrains.containsKey(playerName)) {
                Bukkit.getPlayer(playerName).sendMessage("Você já possui um terreno!");
                return;
            }
            availableTerrains.remove(location);
            playerTerrains.put(playerName, location);
            claimedTerrains.put(location, playerName);
            playerTerrainInteractableSizes.put(playerName, AREA_INTERATIVA);
            playerTerrainUpgradeLevels.put(playerName, 1);
            marcacaoAreaInterativa(location, AREA_INTERATIVA);
        }
    }

    public static void marcacaoAreaInterativa(Location location, int size) {
        if (location == null || location.getWorld() == null) {
            Bukkit.getConsoleSender().sendMessage("Location ou World é nulo!");
            return;
        }

        World world = location.getWorld();
        int startX = location.getBlockX() + (ComprarTerreno.TAMANHO_TERRENO - size) / 2;
        int startZ = location.getBlockZ() + (ComprarTerreno.TAMANHO_TERRENO - size) / 2;
        for (int x = startX; x < startX + size; x++) {
            for (int z = startZ; z < startZ + size; z++) {
                if (x == startX || x == startX + size - 1 || z == startZ || z == startZ + size - 1) {
                    world.getBlockAt(x, ComprarTerreno.ALTURA_TERRENO, z).setType(Material.MYCEL);
                }
            }
        }
    }

    public static int getPlayerTerrainUpgradeLevel(String playerName) {
        return playerTerrainUpgradeLevels.getOrDefault(playerName, 1);
    }

    public static void setPlayerTerrainUpgradeLevel(String playerName, int level) {
        playerTerrainUpgradeLevels.put(playerName, level);
    }

    public HashMap<String, String> getClanLeaders() {
        return clanLeaders;
    }

    public void addClanLeader(String playerName, String clanName) {
        clanLeaders.put(playerName, clanName);
    }

    public static List<Location> getAvailableTerrains() {
        return availableTerrains;
    }

    public static HashMap<String, Location> getPlayerTerrains() {
        return playerTerrains;
    }

    public static HashMap<Location, String> getClaimedTerrains() {
        return claimedTerrains;
    }

    public static HashMap<String, Integer> getPlayerTerrainInteractableSizes() {
        return playerTerrainInteractableSizes;
    }

}
