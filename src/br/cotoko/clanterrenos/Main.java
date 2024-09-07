package br.cotoko.clanterrenos;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import br.cotoko.clanterrenos.api.ComprarTerreno;
import br.cotoko.clanterrenos.api.DispensarTerreno;
import br.cotoko.clanterrenos.commands.BaseCommand;
import br.cotoko.clanterrenos.commands.ClaimCommand;
import br.cotoko.clanterrenos.commands.DispensarCommand;
import br.cotoko.clanterrenos.commands.MenuCommand;
import br.cotoko.clanterrenos.commands.UpgradeCommand;
import br.cotoko.clanterrenos.listeners.BreakBase;
import br.cotoko.clanterrenos.listeners.KickPlayerEvent;
import br.cotoko.clanterrenos.listeners.Place2;
import br.cotoko.clanterrenos.listeners.PlaceBase;
import br.cotoko.clanterrenos.menus.UpgradeMenu;
import br.cotoko.clanterrenos.sql.BancoDados;

public class Main extends JavaPlugin {
	
    public static BancoDados bancoDados;
    
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§c  █▀▀▀█ ▀▀█▀▀ █▀▀█ █▀▀▀ █▀▀▀ █▄░▒█");
        Bukkit.getConsoleSender().sendMessage("§c  ▀▀▀▄▄ ░▒█░░ █▄▄▀ █▀▀▀ █▀▀▀ █▒█▒█");
        Bukkit.getConsoleSender().sendMessage("§c  █▄▄▄█ ░▒█░░ █░▒█ █▄▄▄ █▄▄▄ █░░▀█");
        Bukkit.getConsoleSender().sendMessage("§cStreenBase v0.0.1 iniciando Serviços");
        Bukkit.getConsoleSender().sendMessage("§fDesenvolvido por §cCotoko §fe exclusivo da Streen Games");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§eCarregando Eventos/Comandos....");
        LoadCommands();
        LoadEvents();
        Bukkit.getConsoleSender().sendMessage("§eCarregando Dados...");
        LoadDataBase();
    }
    
    public void LoadDataBase() {
        bancoDados = new BancoDados(this);
        bancoDados.Iniciar();
        bancoDados.CarregarTerrenos();
        Bukkit.getConsoleSender().sendMessage("§aDados carregados com sucesso!");
    }
    
    
    public void LoadCommands() {
        getCommand("base").setExecutor(new BaseCommand(this));
        getCommand("claim").setExecutor(new ClaimCommand(this));
        getCommand("dispensar").setExecutor(new DispensarCommand(this));
        getCommand("menu").setExecutor(new MenuCommand());
        getCommand("upgrade").setExecutor(new UpgradeCommand());
        Bukkit.getConsoleSender().sendMessage("§aComandos carregados com sucesso!");
    }
    
    public void LoadEvents() {
        PluginManager load = getServer().getPluginManager();
        load.registerEvents(new ComprarTerreno(), this);
        load.registerEvents(new DispensarTerreno(), this);
        load.registerEvents(new BreakBase(), this);
        //load.registerEvents(new PlaceBase(), this);
        load.registerEvents(new Place2(), this);
        load.registerEvents(new KickPlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new MenuCommand(), this);
        getServer().getPluginManager().registerEvents(new UpgradeMenu(bancoDados), this);
        CriarMundo();
        Bukkit.getConsoleSender().sendMessage("§aEventos carregados com sucesso!");
    }
    
    @Override
    public void onDisable() {

            bancoDados.SalvarTerrenos();
            bancoDados.FecharDataBase();
        
    }
    
    public void CriarMundo() {
        WorldCreator wc = new WorldCreator("TerrenoClan");
        wc.type(WorldType.FLAT);
        wc.generator(new FlatChunkGenerator());
        World world = wc.createWorld();
        if (world != null) {
        	CriarTerrenos(world);
        } else {
        	Bukkit.getConsoleSender().sendMessage(" §c[BASE] O mundo não pode ser criado!");
        }
    }
	
    public void CriarTerrenos(World world) {
    	for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                int startX = x * (ComprarTerreno.TAMANHO_TERRENO + ComprarTerreno.DISTANCIA_TERRENO);
                int startZ = z * (ComprarTerreno.TAMANHO_TERRENO + ComprarTerreno.DISTANCIA_TERRENO);
                Location location = new Location(world, startX, ComprarTerreno.ALTURA_TERRENO, startZ);
                ComprarTerreno.availableTerrains.add(location);
                ComprarTerreno.CriarBorda(world, startX, startZ, ComprarTerreno.TAMANHO_TERRENO);
            }
        }
    }
    
    public static class FlatChunkGenerator extends ChunkGenerator {
        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
            ChunkData chunk = createChunkData(world);

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    chunk.setBlock(i, 0, j, Material.BEDROCK);
                    chunk.setBlock(i, 1, j, Material.DIRT);
                    chunk.setBlock(i, 2, j, Material.DIRT);
                    chunk.setBlock(i, 3, j, Material.GRASS);
                }
            }
            return chunk;
        }
    }
    
    public BancoDados getBancoDados() {
        return bancoDados;
    }
}
