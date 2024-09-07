package br.cotoko.clanterrenos.commands;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.streen.lib.api.heads.CustomHeads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import br.cotoko.clanterrenos.sql.BancoDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MenuCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
            return true;
        }

        Player player = (Player) sender;
        ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (clanPlayer == null || !clanPlayer.isLeader()) {
            player.sendMessage("Somente o líder do clã pode usar este comando.");
            return true;
        }

        Clan clan = clanPlayer.getClan();
        String clanName = clanPlayer.getClan().getName();
        Location baseLocation = null;
		try {
			baseLocation = BancoDados.getClanBaseLocation(clanName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (baseLocation == null) {
			player.sendMessage("Seu clã não tem uma base definida.");
            return true;
		}


        Inventory menu = Bukkit.createInventory(null, 54, "Membros do Clã");
        List<ItemStack> heads = new ArrayList<>();
        List<ClanPlayer> sortedMembers = clan.getAllMembers().stream()
                .sorted(Comparator.comparing(ClanPlayer::isLeader).reversed()
                        .thenComparing(ClanPlayer::isTrusted, Comparator.reverseOrder())
                        .thenComparing(ClanPlayer::getName))
                .collect(Collectors.toList());

        for (ClanPlayer member : sortedMembers) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, member.isTrusted() ? 1 : 0, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(member.getName());
            skullMeta.setDisplayName("§f" + member.getName());

            List<String> lore = new ArrayList<>();
            String rank = "Membro";
            String online = Bukkit.getServer().getPlayer(member.getName()) != null ? "§aOnline" : "§cOffline";

            if (member.isLeader()) {
                rank = "Líder";
            } else if (member.isTrusted()) {
                rank = "Oficial";
            }

            lore.add("");
            lore.add(" §7Cargo: §f" + rank);
            lore.add(" §7Status: " + online);
            lore.add("");

            if (!member.isLeader()) {
                if (member.isTrusted()) {
                    lore.add(" §a• Tem acesso a base do clan!");
                    lore.add("");
                    lore.add("  §7Clique aqui para remover o acesso ");
                    lore.add("  §7deste jogador.");
                } else {
                    lore.add(" §c• Não tem acesso a base do clan!");
                    lore.add("");
                    lore.add("  §7Clique aqui para liberar o acesso ");
                    lore.add("  §7deste jogador.");
                }
            } else if (member.isLeader() && member.isTrusted()) {
                lore.add(" §6• Líder do Clã •");
                lore.add("");
                lore.add("  §eO acesso deste jogador(a) não pode ");
                lore.add("  §eser alterado!");
            }


            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);

            heads.add(skull);
        }

        if (heads.size() < 20) {
            int size = heads.size();
            for (int i = 0; i < 20 - size; i++) {
                ItemStack item;
                ItemMeta meta;
                ArrayList<String> lore = new ArrayList<>();

                if (i < 14) {
                    item = new CustomHeads("http://textures.minecraft.net/texture/623d7e655debdd591d099d76ff000d7555be4ae11b1e26b9adf244ae022b29c8").criar();
                    meta = item.getItemMeta();
                    meta.setDisplayName("§cVaga Disponível");
                    lore.add("§7Esta Vaga está disponível para um novo membro.");
                    lore.add("");
                } else if (i < 17) {
                    item = new CustomHeads("http://textures.minecraft.net/texture/ac01f6796eb63d0e8a759281d037f7b3843090f9a456a74f786d049065c914c7").criar();
                    meta = item.getItemMeta();
                    meta.setDisplayName("§cVaga Disponível");
                    lore.add("§7Esta Vaga está disponível para um novo membro.");
                    lore.add("");
                    lore.add("§fEsta vaga pode ser comprada com o saldo");
                    lore.add("§fdo banco do seu clan.");
                } else {
                    item = new CustomHeads("http://textures.minecraft.net/texture/9291622c5104f1cc94a2b4d33ad7b2c9b215ff949501e8c250cfd5a861e6b907").criar();
                    meta = item.getItemMeta();
                    meta.setDisplayName("§cVaga Disponível");
                    lore.add("§7Esta Vaga está disponível para um novo membro.");
                    lore.add("");
                    lore.add("§fEsta vaga pode ser comprada com o §6§lCASH");
                    lore.add("§fde algum líder do clan.");
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                heads.add(item);
            }
        }

        int limitepp = 20;
        int pagina = 1;
        int index = pagina * limitepp - limitepp;
        int endIndex = Math.min(index + limitepp, heads.size());

        int x = 2;
        int h = 1;
        for (; index < endIndex; index++) {
            ItemStack item = heads.get(index);
            menu.setItem(x + 9 * h, item);
            if (++x == 7) {
                x = 2;
                ++h;
            }
        }

        ItemStack voltar = new ItemStack(Material.ARROW);
        ItemMeta meta = voltar.getItemMeta();
        meta.setDisplayName("§aVoltar");
        voltar.setItemMeta(meta);
        menu.setItem(49, voltar);

        player.openInventory(menu);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Membros do Clã")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            if (clickedItem.getType() != Material.SKULL_ITEM) {
                return;
            }

            SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
            String memberName = skullMeta.getOwner();
            @SuppressWarnings("deprecation")
			ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(memberName);

            if (clanPlayer == null) {
                return;
            }
            ClanPlayer clickingPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(player);
            if (clickingPlayer == null || !clickingPlayer.isLeader()) {
                player.sendMessage("Somente líderes podem alterar o status de confiança.");
                return;
            }

            boolean isTrusted = clanPlayer.isTrusted();
            if (clanPlayer.isLeader()) {
                player.sendMessage("Líder não pode alterar o status de confiança.");
            } else {
                if (isTrusted) {
                    clanPlayer.setTrusted(false);
                    player.sendMessage(memberName + " não é mais confiável.");
                    clickedItem.setAmount(0);
                } else {
                    clanPlayer.setTrusted(true);
                    player.sendMessage(memberName + " agora é confiável.");
                    clickedItem.setAmount(1);
                }

                List<String> lore = new ArrayList<>();
                String rank = clanPlayer.isLeader() ? "Líder" : clanPlayer.isTrusted() ? "Oficial" : "Membro";
                String online = Bukkit.getServer().getPlayer(clanPlayer.getName()) != null ? "§aOnline" : "§cOffline";

                lore.add("");
                lore.add(" §7Cargo: §f" + rank);
                lore.add(" §7Status: " + online);
                lore.add("");
                if (!clanPlayer.isLeader()) {
                    if (clanPlayer.isTrusted()) {
                        lore.add("§aTem acesso a base do clan!");
                        lore.add("");
                        lore.add(" §7Clique aqui para remover o acesso ");
                        lore.add(" §7deste jogador.");
                    } else {
                        lore.add("§cNão tem acesso a base do clan!");
                        lore.add("");
                        lore.add(" §7Clique aqui para liberar o acesso ");
                        lore.add(" §7deste jogador.");
                    }
                } else if (clanPlayer.isLeader() && clanPlayer.isTrusted()) {
                    lore.add("§6• Líder do Clã •");
                    lore.add("");
                    lore.add(" §eO acesso deste jogador(a) não pode ");
                    lore.add(" §eser alterado!");
                }

                skullMeta.setLore(lore);
                clickedItem.setItemMeta(skullMeta);
                player.getOpenInventory().getTopInventory().setItem(event.getSlot(), clickedItem);
            }
        }
    }
}
