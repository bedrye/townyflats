package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import me.bedrye.townyflats.util.Lang;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.palmergames.bukkit.towny.object.Town;

import java.io.File;
import java.util.*;

public final class Townyflats extends JavaPlugin {
    public Map<String, LocatorLoc> cache = new HashMap<>();
    //public static Economy econ = null;
    public int flatlim;
    Material wand;
    ArrayList<UUID> haveCooldowns = new ArrayList<UUID>();
    public ArrayList<Apartment> flats = new ArrayList<Apartment>();
    public Map<Town,ArrayList<Apartment>> TFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> PFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> CFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> AFlats = new HashMap<>();

    public static Lang lang;

    public static Lang getLang(){
        return lang;
    }

    private static Townyflats instance;

    public static Townyflats getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        /*if (!setupEconomy() ) {
            Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }*/


        Load();
        Restart();
        registerCommands();
        loadListeners();
        registerListeners();
    }

    public void loadListeners(){

    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new BreakBlock(this ),this);
    }

    public void registerCommands(){
        Objects.requireNonNull(getServer().getPluginCommand("tapp")).setExecutor(new Commands(this));
        Objects.requireNonNull(getServer().getPluginCommand("tapp")).setTabCompleter(new TabComplete());
    }

    void Restart(){
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        flatlim = getConfig().getInt("FlatLimitPerChunk");
        wand = Material.valueOf(Objects.requireNonNull(getConfig().get("WandItem")).toString());

        if (getConfig().contains("saved")){
            Objects.requireNonNull(getConfig().getConfigurationSection("saved")).getKeys(false).forEach(key ->{
                Objects.requireNonNull(getConfig().getConfigurationSection("saved." + key)).getKeys(false).forEach(subkey -> {
                    Apartment cacheT = new Apartment(Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".x1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".x2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".y1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".y2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".z1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".z2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".xC")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".zC")).toString()),
                            Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".price")).toString()),

                            Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".owner")).toString(),
                            Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".world")).toString(),
                            subkey,TownyUniverse.getInstance().getTown(key),this
                    );
                    if( getConfig().contains("saved." + key +"." +subkey+".xA")&&getConfig().contains("saved." + key +"." +subkey+".yA")&& getConfig().contains("saved." + key +"." +subkey+".zA")) {
                        Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".world")).toString()),
                                Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".xA")).toString()),
                                Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".yA")).toString()),
                                Integer.parseInt(Objects.requireNonNull(getConfig().get("saved." + key + "." + subkey + ".zA")).toString()));
                        cacheT.SetHologram(loc);
                    }
                    cacheT.WriteToMaps();
                });
            });}
    }
    /*private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }*/

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Saving... !");
        Save();
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled !");
    }

    void Load() {
        if (new File(this.getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles()!= null) {
            for (File file : Objects.requireNonNull(new File(this.getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles())) {
                FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                Bukkit.getConsoleSender().sendMessage(file.getName().replace(".yml", ""));
                Objects.requireNonNull(conf.getConfigurationSection("saved")).getKeys(false).forEach(key -> {
                    Apartment cacheT = new Apartment(Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".x1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".x2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".y1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".y2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".z1")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".z2")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".xC")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".zC")).toString()),
                            Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".price")).toString()),

                            Objects.requireNonNull(conf.get("saved." + key + ".owner")).toString(),
                            Objects.requireNonNull(conf.get("saved." + key + ".world")).toString(),
                            Objects.requireNonNull(conf.get("saved." + key + ".name")).toString(),
                            TownyUniverse.getInstance().getTown(file.getName().replace(".yml", ""))
                            , this);
                    if (conf.contains("saved." + key + ".xA") && conf.contains("saved." + key + ".yA") && conf.contains("saved." + key + ".zA")) {
                        Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(conf.get("saved." + key + ".world")).toString()),
                                Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".xA")).toString()),
                                Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".yA")).toString()),
                                Integer.parseInt(Objects.requireNonNull(conf.get("saved." + key + ".zA")).toString()));
                        if (cacheT.price >= 0) {
                            cacheT.SetHologram(loc);
                        }
                    }
                    if (conf.contains("saved." + key + ".residents")) {
                        Objects.requireNonNull(conf.getConfigurationSection("saved." + key + ".residents")).getKeys(false).forEach(subkey -> {
                                cacheT.residents.add(Objects.requireNonNull(conf.get("saved." + key + ".residents." + subkey)).toString());

                        });
                    }
                    cacheT.WriteToMaps();
                    Bukkit.getConsoleSender().sendMessage(key);
                });
            }
        }

    }
    void Save(){
            for(Apartment g: flats) {
                g.SaveFile();
                g.RemoveHologram();
        }
    }
}




