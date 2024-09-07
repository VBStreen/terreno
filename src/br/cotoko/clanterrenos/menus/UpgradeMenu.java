package br.cotoko.clanterrenos.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.cotoko.clanterrenos.api.ComprarTerreno;
import br.cotoko.clanterrenos.api.DispensarTerreno;
import br.cotoko.clanterrenos.sql.BancoDados;
import net.streen.lib.api.heads.CustomHeads;

public class UpgradeMenu implements Listener {

    public final BancoDados bancoDados;

    public UpgradeMenu(BancoDados bancoDados) {
        this.bancoDados = bancoDados;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Escolha o nível do terreno")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (ComprarTerreno.getPlayerTerrains() != null && 
            ComprarTerreno.getPlayerTerrains().containsKey(player.getName())) {

            Integer currentSize = ComprarTerreno.getPlayerTerrainInteractableSizes().get(player.getName());
            Integer currentLevel = ComprarTerreno.getPlayerTerrainUpgradeLevel(player.getName());

            if (currentSize != null && currentLevel != null) {
                if (slot == 16 && clickedItem.getType() == Material.WOOL) {
                    int newSize = Math.min(currentSize + ComprarTerreno.TAMANHO_UPGRADE, ComprarTerreno.TAMANHO_TERRENO_MAX);
                    int newLevel = currentLevel + 1;

                    if (newLevel <= 11) {
                        DispensarTerreno.RemoverMarcacaoUpgrade(ComprarTerreno.getPlayerTerrains().get(player.getName()), currentSize);
                        ComprarTerreno.setPlayerTerrainUpgradeLevel(player.getName(), newLevel);
                        ComprarTerreno.getPlayerTerrainInteractableSizes().put(player.getName(), newSize);
                        ComprarTerreno.marcacaoAreaInterativa(ComprarTerreno.getPlayerTerrains().get(player.getName()), newSize);
                        player.sendMessage("Você atualizou seu terreno para o nível " + newLevel + ".");

                        openMenu(player);
                    } else {
                        player.sendMessage("Você já está no nível máximo.");
                    }
                } else {
                    player.sendMessage("Você deve clicar na lã para atualizar seu terreno.");
                }
            } else {
                player.sendMessage("Erro ao tentar atualizar. Dados do terreno não encontrados.");
            }
        } else {
            player.sendMessage("Você não possui um terreno para atualizar.");
        }
    }

    private static String numeroromano(int number) {
        String[] romanNumerals = {
            "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
        };
        int[] values = {
            1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
        };
        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                roman.append(romanNumerals[i]);
                number -= values[i];
            }
        }

        return roman.toString();
    }
    
    public static void openMenu(Player player) {
        int currentLevel = ComprarTerreno.getPlayerTerrainUpgradeLevel(player.getName());

        Inventory menu = Bukkit.createInventory(null, 9*5, "Escolha o nível do terreno");
       
        Map<Integer, List<String>> lores = new HashMap<>();
        lores.put(1, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d24x24", "   §5┠ §fBônus de McMMO: §d1x", "   §5┗ §fBônus de drops dos Spawners: §d1x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a50.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(2, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d26x26", "   §5┠ §fBônus de McMMO: §d1.1x", "   §5┗ §fBônus de drops dos Spawners: §d1x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a100.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(3, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d28x28", "   §5┠ §fBônus de McMMO: §d1.2x", "   §5┗ §fBônus de drops dos Spawners: §d1.1x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(4, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d30x30", "   §5┠ §fBônus de McMMO: §d1.3x", "   §5┗ §fBônus de drops dos Spawners: §d1.15x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(5, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d32x32", "   §5┠ §fBônus de McMMO: §d1.4x", "   §5┗ §fBônus de drops dos Spawners: §d1.2x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(6, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d34x24", "   §5┠ §fBônus de McMMO: §d1.45x", "   §5┗ §fBônus de drops dos Spawners: §d1.25x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(7, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d36x36", "   §5┠ §fBônus de McMMO: §d1.5x", "   §5┗ §fBônus de drops dos Spawners: §d1.3x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(8, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d38x38", "   §5┠ §fBônus de McMMO: §d1.6x", "   §5┗ §fBônus de drops dos Spawners: §d1.4x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(9, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d40x40", "   §5┠ §fBônus de McMMO: §d1.75x", "   §5┗ §fBônus de drops dos Spawners: §d1.5x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a150.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        lores.put(10, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d42x42", "   §5┠ §fBônus de McMMO: §d1.9x", "   §5┗ §fBônus de drops dos Spawners: §d1.6x", "§a", " §2• §aCusto do upgrade: §6✪ 50 cash", "   §7(Cash será removido do Líder do clan)", ""));
        lores.put(11, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d44x44", "   §5┠ §fBônus de McMMO: §d2x", "   §5┗ §fBônus de drops dos Spawners: §d1.75x", "§a", " §2• §aCusto do upgrade: §6✪ 50 cash", "   §7(Cash será removido do Líder do clan)", ""));
        
        Map<Integer, List<String>> completo = new HashMap<>();
        completo.put(1, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d24x24", "   §5┠ §fBônus de McMMO: §d1x", "   §5┗ §fBônus de drops dos Spawners: §d1x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a50.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        completo.put(2, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d26x26", "   §5┠ §fBônus de McMMO: §d1.1x", "   §5┗ §fBônus de drops dos Spawners: §d1x", "§a", " §2• §aCusto do upgrade: §2§lR$ §a100.000,00 ", "   §7(Dinheiro será removido do banco do clan!)", ""));
        completo.put(3, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d28x28", "   §5┠ §fBônus de McMMO: §d1.2x", "   §5┗ §fBônus de drops dos Spawners: §d1.1x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(4, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d30x30", "   §5┠ §fBônus de McMMO: §d1.3x", "   §5┗ §fBônus de drops dos Spawners: §d1.15x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(5, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d32x32", "   §5┠ §fBônus de McMMO: §d1.4x", "   §5┗ §fBônus de drops dos Spawners: §d1.2x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(6, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d34x24", "   §5┠ §fBônus de McMMO: §d1.45x", "   §5┗ §fBônus de drops dos Spawners: §d1.25x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(7, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d36x36", "   §5┠ §fBônus de McMMO: §d1.5x", "   §5┗ §fBônus de drops dos Spawners: §d1.3x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(8, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d38x38", "   §5┠ §fBônus de McMMO: §d1.6x", "   §5┗ §fBônus de drops dos Spawners: §d1.4x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(9, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d40x40", "   §5┠ §fBônus de McMMO: §d1.75x", "   §5┗ §fBônus de drops dos Spawners: §d1.5x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(10, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d42x42", "   §5┠ §fBônus de McMMO: §d1.9x", "   §5┗ §fBônus de drops dos Spawners: §d1.6x", "§a", " §aEste upgrade já está ativo", ""));
        completo.put(11, Arrays.asList("§a", " §d• §eAqui estão as melhorias da sua base:", "§a", "   §5┏ §fTamanho da base: §d44x44", "   §5┠ §fBônus de McMMO: §d2x", "   §5┗ §fBônus de drops dos Spawners: §d1.75x", "§a", " §aEste upgrade já está ativo", ""));
        
        int[] slots = {10, 11, 12, 13, 19, 20, 21, 22, 28, 29, 30};

        for (int i = 0; i < slots.length; i++) {
            ItemStack item;
            ItemMeta meta;
            String romano = numeroromano(i);
            
        if (slots[i] == 10) {
            // Nível futuro - Bedrock
            item = new CustomHeads("http://textures.minecraft.net/texture/fa619542fdf195a09524737b5ce47c84e3bb7537f9439a92b194199b814028cf").criar();
            meta = item.getItemMeta();
            meta.setDisplayName("§d§lBASE §fNível Inicial");
            List<String> Lore = new ArrayList<String>();
            Lore.add("§7");
            Lore.add(" §d• §eAqui está as melhorias da sua base:");
            Lore.add("");
            Lore.add("   §5┏ §fTamanho da base: §d24x24");
            Lore.add("   §5┠ §fBônus de McMMO: §d1x");
            Lore.add("   §5┗ §fBônus de drops dos Spawners: §d1x");
            Lore.add("");
            Lore.add(" §7* Ao atualizar a Base do seu clan as");
            Lore.add("  §7porcentagem dos multiplicadores serão melhorados!  ");
            Lore.add("");
            meta.setLore(Lore);
            item.setItemMeta(meta);
            
        }else if (i < currentLevel) {
               // completo
                item = new CustomHeads("http://textures.minecraft.net/texture/a7695f96dda626faaa010f4a5f28a53cd66f77de0cc280e7c5825ad65eedc72e").criar();
                meta = item.getItemMeta();
                meta.setDisplayName("§a§lBASE §fNível §a§l" + romano + " §8("+ (i) + ")");
                meta.setLore(completo.get(i + 1));
                item.setAmount(i);
                item.setItemMeta(meta);
            } else if (i == currentLevel) {
            	// proximo
            	item = new CustomHeads("http://textures.minecraft.net/texture/a3370d0b8b57dd2e778acc457853468b4334b5643a66e3e7729d66c33264cc").criar();
                meta = item.getItemMeta();
                meta.setDisplayName("§9§lBASE §fNível §9§l" + romano + " §8("+ (i) + ")");
                meta.setLore(lores.get(i + 1));
                item.setAmount(i);
                item.setItemMeta(meta);
            } else if (slots[i] == 29 || slots[i] == 30) {
                // cash 
                item = new CustomHeads("http://textures.minecraft.net/texture/9291622c5104f1cc94a2b4d33ad7b2c9b215ff949501e8c250cfd5a861e6b907").criar();
                meta = item.getItemMeta();
                meta.setDisplayName("[CASH] Nível " + (i));
                meta.setLore(lores.get(i ));
                item.setAmount(i );
                item.setItemMeta(meta);
            } else {
                // sem comprar
            	item = new CustomHeads("http://textures.minecraft.net/texture/b2554dda80ea64b18bc375b81ce1ed1907fc81aea6b1cf3c4f7ad3144389f64c").criar();
                meta = item.getItemMeta();
                meta.setDisplayName("§c§lBASE §fNível §c§l" + romano + " §8("+ (i) + ")");
                List<String> Lore = new ArrayList<String>();
                Lore.add("§7");
                Lore.add(" §c• Você ainda não pode ver os upgrades  ");
                Lore.add("   §cfuturos da sua base, adquira primeiro  ");
                Lore.add("   §co upgrade disponível para compra! ");
                Lore.add("");
                meta.setLore(Lore);
                item.setAmount(i);
                item.setItemMeta(meta);
            }

            menu.setItem(slots[i], item);
        }

        ItemStack wool = new ItemStack(Material.WOOL, 1, (short) 0);
        ItemMeta woolMeta = wool.getItemMeta();
        if (woolMeta != null) {
            woolMeta.setDisplayName("Evoluir");
            woolMeta.setLore(Arrays.asList("Clique para evoluir seu terreno."));
            wool.setItemMeta(woolMeta);
        }
        menu.setItem(16, wool);

        player.openInventory(menu);
    }
}
