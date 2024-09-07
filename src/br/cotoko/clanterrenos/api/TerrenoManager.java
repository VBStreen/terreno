package br.cotoko.clanterrenos.api;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TerrenoManager implements Listener {
    private Map<String, Clan> clanTerrains = new HashMap<>(); // Mapeia nome do jogador para o Clã
    private Map<String, ClanPlayer> trustedMembers = new HashMap<>(); // Mapeia nome dos membros confiáveis
    private Map<String, ClanPlayer> clanLeaders = new HashMap<>(); // Mapeia nome dos líderes de clã

    private void carregarClansComTerrenos() {
        // Supomos que você tenha um método para obter todos os terrenos.
        Map<String, Location> terrenos = ComprarTerreno.getPlayerTerrains();
        
        for (String terrainOwnerName : terrenos.keySet()) {
            ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(terrainOwnerName);
            
            if (clanPlayer != null && clanPlayer.getClan() != null) {
                Clan clan = clanPlayer.getClan();
                clanTerrains.put(terrainOwnerName, clan); // Mapeia o nome do dono do terreno para o clã

                // Adiciona o líder do clã
                ClanPlayer leader = (ClanPlayer) clan.getLeaders();
                if (leader != null) {
                    clanLeaders.put(leader.getName(), leader);
                }

                // Adiciona membros confiáveis
                for (ClanPlayer member : clan.getMembers()) {
                    if (member.isTrusted()) {
                        trustedMembers.put(member.getName(), member);
                    }
                }
            }
        }
    }

    // Getter para os HashMaps caso precise usá-los em outras partes do plugin
    public Map<String, Clan> getClanTerrains() {
        return clanTerrains;
    }

    public Map<String, ClanPlayer> getTrustedMembers() {
        return trustedMembers;
    }

    public Map<String, ClanPlayer> getClanLeaders() {
        return clanLeaders;
    }
}
