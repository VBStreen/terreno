package br.cotoko.clanterrenos.commands;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.streen.lib.api.TitleAPI;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import br.cotoko.clanterrenos.Main;
import br.cotoko.clanterrenos.sql.BancoDados;

public class BaseCommand implements CommandExecutor {

    public BaseCommand(Main plugin) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("base")) {
                ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

                if (clanPlayer == null || clanPlayer.getClan() == null) {
                    TitleAPI.get().sendTitle(player, "§c§lSTREEN BASE", "§cVocê não pertence a nenhum clã!");
                    return true;
                }
                if (!clanPlayer.isTrusted()) {
                    TitleAPI.get().sendTitle(player, "§e§lSTREEN BASE", "§eSomente para membros confiáveis!");
                    return true;
                }
                String clanName = clanPlayer.getClan().getName();
                String clanTAG = clanPlayer.getClan().getColorTag();
                Location baseLocation = null;
                try {
                    baseLocation = BancoDados.getClanBaseLocation(clanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseLocation != null) {
                    Location teleportLocation = getTeleportLocation(baseLocation);
                    player.teleport(teleportLocation);
                    TitleAPI.get().sendTitle(player, "§a§lSTREEN BASE", "§fTeleportado para a Base " + clanTAG);
                } else {
                    TitleAPI.get().sendTitle(player, "§c§lSTREEN BASE", "§cSeu clã ainda não possui uma base!");
                }
                return true;
            }
        }
        return false;
    }

    private Location getTeleportLocation(Location baseLocation) {
        Vector direction = baseLocation.getDirection().normalize();
        Vector leftDirection = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Vector offset = direction.multiply(51).add(leftDirection.multiply(-22));
        Location teleportLocation = baseLocation.add(offset);
        teleportLocation.setYaw(180);
        return teleportLocation;
    }
}
