package br.cotoko.clanterrenos.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.cotoko.clanterrenos.api.ComprarTerreno;
import br.cotoko.clanterrenos.menus.UpgradeMenu;

public class UpgradeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (ComprarTerreno.getPlayerTerrains().containsKey(player.getName())) {
                UpgradeMenu.openMenu(player);
            } else {
                player.sendMessage("Você não possui um terreno para atualizar.");
            }
        } else {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
        }
        return true;
    }
    
}
