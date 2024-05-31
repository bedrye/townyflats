package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import me.bedrye.townyflats.commands.MainCommands;
import me.bedrye.townyflats.commands.MainCommandsTabComp;
import me.bedrye.townyflats.events.FlatsEventManager;
import me.bedrye.townyflats.util.Apartment;
import me.bedrye.townyflats.util.FlatManager;
import me.bedrye.townyflats.util.Lang;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class TownyFlats extends JavaPlugin {
    //public static Economy econ = null;
    public int flatlim;
    public static Material wand;

    private static Lang lang;

    public static Lang getLang(){
        return lang;
    }

    private static TownyFlats instance;

    public static TownyFlats getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        lang = new Lang();
        load();
        restart();
        registerCommands();
        loadListeners();
        registerListeners();
    }

    public void loadListeners(){

    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new FlatsEventManager(),this);
    }

    public void registerCommands(){
        Objects.requireNonNull(getServer().getPluginCommand("tapp")).setExecutor(new MainCommands(this));
        Objects.requireNonNull(getServer().getPluginCommand("tapp")).setTabCompleter(new MainCommandsTabComp());
    }

    public void restart(){
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        flatlim = getConfig().getInt("FlatLimitPerChunk");
        wand = Material.valueOf(Objects.requireNonNull(getConfig().get("WandItem")).toString());

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Saving... !");
        FlatManager.Instance().save();
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled !");
    }

    public void load() {
        if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles()!= null) {
            for (File file : Objects.requireNonNull(new File(this.getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles())) {
                FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                Bukkit.getConsoleSender().sendMessage(file.getName().replace(".yml", ""));
                if( !conf.contains("saved")) continue;
                conf.getConfigurationSection("saved").getKeys(false).forEach(key -> {
                    if (conf.get("saved." + key) != null) {
                        if (!conf.get("saved." + key).toString().equals("1")) {
                            conf.getConfigurationSection("saved." + key).getKeys(false).forEach(key2 -> {
                                Apartment cacheT = new Apartment(
                                        conf.getInt("saved." + key + "." + key2 + ".x1"),
                                        conf.getInt("saved." + key + "." + key2 + ".x2"),
                                        conf.getInt("saved." + key + "." + key2 + ".y1"),
                                        conf.getInt("saved." + key + "." + key2 + ".y2"),
                                        conf.getInt("saved." + key + "." + key2 + ".z1"),
                                        conf.getInt("saved." + key + "." + key2 + ".z2"),
                                        conf.getInt("saved." + key + "." + key2 + ".xC"),
                                        conf.getInt("saved." + key + "." + key2 + ".zC"),
                                        conf.getInt("saved." + key + "." + key2 + ".price"),

                                        TownyUniverse.getInstance().getResident(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".owner")).toString()),
                                        Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".world")).toString(),
                                        Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".name")).toString(),
                                        TownyUniverse.getInstance().getTown(file.getName().replace(".yml", ""))
                                );
                                if (conf.contains("saved." + key + ".xA") && conf.contains("saved." + key + "." + key2 + ".yA") && conf.contains("saved." + key + ".zA")) {
                                    Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".world")).toString()),
                                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".xA")).toString()),
                                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".yA")).toString()),
                                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".zA")).toString()));
                                    if (cacheT.getPrice() >= 0) {
                                        cacheT.SetHologram(loc);
                                    }
                                }
                                if (conf.contains("saved." + key + "." + key2 + ".residents")) {
                                    Objects.requireNonNull(conf.getConfigurationSection("saved." + key + "." + key2 + ".residents")).getKeys(false).forEach(subkey -> {
                                        cacheT.addResident(TownyUniverse.getInstance().getResident(Objects.requireNonNull(conf.get("saved." + key + "." + key2 + ".residents." + subkey)).toString()));

                                    });
                                }
                                cacheT.WriteToMaps();
                                FlatManager.Instance().putNewChunk(key);
                                Bukkit.getConsoleSender().sendMessage(key);
                            });
                        } else {
                            FlatManager.Instance().putNewChunk(key);
                        }
                    }
                });
                FlatManager.Instance().putNewTown(TownyUniverse.getInstance().getTown(file.getName().replace(".yml", "")));
            }
        }
        for (Town t:TownyUniverse.getInstance().getTowns()) {
            FlatManager.Instance().putNew(t);
            for (TownBlock b:t.getTownBlocks()){
                FlatManager.Instance().putNew(b);
            }

        }

    }
}




