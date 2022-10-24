package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.palmergames.bukkit.towny.object.Town;

import java.io.File;
import java.util.*;

public final class Townyflats extends JavaPlugin {
    public Map<String, LocatorLoc> cache = new HashMap<>();
    //public static Economy econ = null;
    public int flatlim;
    Material wand;
    String incorrect_command;
    String command_claim_true;
    String command_buy_true;
    String command_buy_false;
    String command_buy_nothing;
    String command_sell_true;
    String command_sell_false;
    String command_sell_nothing;
    String command_delete_true;
    String command_delete_nothing;
    String command_info_nothing;
    String command_info_pos1;
    String command_info_pos2;
    String command_info_owner;
    String command_info_sell;
    String command_info_sell_button;
    String command_info_roommates;
    String command_info_roommates_button;
    String command_limit_reached;
    String command_antispam;
    String command_list;
    String hologram_1;
    String hologram_4;
    String already_apartment_here;
    String clear_pos, money_symbol;
    ArrayList<UUID> haveCooldowns = new ArrayList<UUID>();
    public ArrayList<Apartment> flats = new ArrayList<Apartment>();
    public Map<Town,ArrayList<Apartment>> TFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> PFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> CFlats = new HashMap<>();
    public Map<String,ArrayList<Apartment>> AFlats = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        /*if (!setupEconomy() ) {
            Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }*/
        incorrect_command = getConfig().get("incorrect_command").toString();
        command_claim_true = getConfig().get("command_claim_true").toString();
        command_buy_true = getConfig().get("command_buy_true").toString();
        command_buy_false= getConfig().get("command_buy_false").toString();
        command_buy_nothing= getConfig().get("command_buy_nothing").toString();
        command_sell_true= getConfig().get("command_sell_true").toString();
        command_sell_false= getConfig().get("command_sell_false").toString();
        command_sell_nothing= getConfig().get("command_sell_nothing").toString();
        command_delete_true= getConfig().get("command_delete_true").toString();
        command_delete_nothing= getConfig().get("command_delete_nothing").toString();
        command_info_nothing= getConfig().get("command_info_nothing").toString();
        command_info_pos1= getConfig().get("command_info_pos1").toString();
        command_info_pos2= getConfig().get("command_info_pos2").toString();
        command_info_owner= getConfig().get("command_info_owner").toString();
        command_info_sell= getConfig().get("command_info_sell").toString();
        command_info_sell_button= getConfig().get("command_info_sell_button").toString();
        command_info_roommates= getConfig().get("command_info_roommates").toString();
        command_info_roommates_button= getConfig().get("command_info_roommates_button").toString();
        command_limit_reached= getConfig().get("command_limit_reached").toString();
        command_antispam= getConfig().get("command_antispam").toString();
        command_list= getConfig().get("command_list").toString();
        hologram_1= getConfig().get("hologram_1").toString();
        hologram_4= getConfig().get("hologram_4").toString();
        already_apartment_here = getConfig().get("already_apartment_here").toString();
        clear_pos = getConfig().get("clear_pos").toString();
        money_symbol = getConfig().get("money_symbol").toString();

        Load();
        Restart();
        getServer().getPluginCommand("tapp").setExecutor(new Commands(this));
        getServer().getPluginCommand("tapp").setTabCompleter(new TabComplete());
        getServer().getPluginManager().registerEvents(new BreakBlock(this ),this);
    }
    void Restart(){
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        incorrect_command = getConfig().get("incorrect_command").toString();
        command_claim_true = getConfig().get("command_claim_true").toString();
        command_buy_true = getConfig().get("command_buy_true").toString();
        command_buy_false= getConfig().get("command_buy_false").toString();
        command_buy_nothing= getConfig().get("command_buy_nothing").toString();
         command_sell_true= getConfig().get("command_sell_true").toString();
         command_sell_false= getConfig().get("command_sell_false").toString();
         command_sell_nothing= getConfig().get("command_sell_nothing").toString();
         command_delete_true= getConfig().get("command_delete_true").toString();
         command_delete_nothing= getConfig().get("command_delete_nothing").toString();
         command_info_nothing= getConfig().get("command_info_nothing").toString();
        command_info_pos1= getConfig().get("command_info_pos1").toString();
        command_info_pos2= getConfig().get("command_info_pos2").toString();
        command_info_owner= getConfig().get("command_info_owner").toString();
        command_info_sell= getConfig().get("command_info_sell").toString();
        command_info_sell_button= getConfig().get("command_info_sell_button").toString();
        command_info_roommates= getConfig().get("command_info_roommates").toString();
        command_info_roommates_button= getConfig().get("command_info_roommates_button").toString();
        command_limit_reached= getConfig().get("command_limit_reached").toString();
        command_antispam= getConfig().get("command_antispam").toString();
        command_list= getConfig().get("command_list").toString();
        hologram_1= getConfig().get("hologram_1").toString();
        hologram_4= getConfig().get("hologram_4").toString();
        already_apartment_here = getConfig().get("already_apartment_here").toString();
        clear_pos = getConfig().get("clear_pos").toString();
        money_symbol = getConfig().get("money_symbol").toString();

        flatlim = getConfig().getInt("FlatLimitPerChunk");
        wand = Material.valueOf(getConfig().get("WandItem").toString());

        if (getConfig().contains("saved")){
            getConfig().getConfigurationSection("saved").getKeys(false).forEach(key ->{
                getConfig().getConfigurationSection("saved."+key).getKeys(false).forEach(subkey -> {

                    Apartment cacheT = new Apartment(Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".x1").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".x2").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".y1").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".y2").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".z1").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".z2").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".xC").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".zC").toString()),
                            Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".price").toString()),

                            getConfig().get("saved." + key +"."+subkey+ ".owner").toString(),
                            getConfig().get("saved." + key + "." + subkey + ".world").toString(),
                            subkey,TownyUniverse.getInstance().getTown(key),this
                    );
                    if( getConfig().contains("saved." + key +"." +subkey+".xA")&&getConfig().contains("saved." + key +"." +subkey+".yA")&& getConfig().contains("saved." + key +"." +subkey+".zA")) {
                        Location loc = new Location(Bukkit.getWorld(getConfig().get("saved." + key + "." + subkey + ".world").toString()),
                                Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".xA").toString()),
                                Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".yA").toString()),
                                Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".zA").toString()));
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
            for (File file : new File(this.getDataFolder().getAbsolutePath() + File.separator + "userdata").listFiles()) {
                FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                Bukkit.getConsoleSender().sendMessage(file.getName().replace(".yml", ""));
                conf.getConfigurationSection("saved").getKeys(false).forEach(key -> {
                    Apartment cacheT = new Apartment(Integer.parseInt(conf.get("saved." + key + ".x1").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".x2").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".y1").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".y2").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".z1").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".z2").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".xC").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".zC").toString()),
                            Integer.parseInt(conf.get("saved." + key + ".price").toString()),

                            conf.get("saved." + key + ".owner").toString(),
                            conf.get("saved." + key + ".world").toString(),
                            conf.get("saved." + key + ".name").toString(),
                            TownyUniverse.getInstance().getTown(file.getName().replace(".yml", ""))
                            , this);
                    if (conf.contains("saved." + key + ".xA") && conf.contains("saved." + key + ".yA") && conf.contains("saved." + key + ".zA")) {
                        Location loc = new Location(Bukkit.getWorld(conf.get("saved." + key + ".world").toString()),
                                Integer.parseInt(conf.get("saved." + key + ".xA").toString()),
                                Integer.parseInt(conf.get("saved." + key + ".yA").toString()),
                                Integer.parseInt(conf.get("saved." + key + ".zA").toString()));
                        if (cacheT.price >= 0) {
                            cacheT.SetHologram(loc);
                        }
                    }
                    if (conf.contains("saved." + key + ".residents")) {
                        conf.getConfigurationSection("saved." + key + ".residents").getKeys(false).forEach(subkey -> {
                                cacheT.residents.add(conf.get("saved." + key + ".residents." + subkey).toString());

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

    //save data

    }




