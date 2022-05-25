package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.palmergames.bukkit.towny.object.Town;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Townyflats extends JavaPlugin {
    public Map<String, String[]> cache = new HashMap<String, String[]>();
    public Map<String, Chunk> cacheCh = new HashMap<String, Chunk>();
    public static Economy econ = null;
    String a;
    FileConfiguration config = getConfig();
    public int flatlim;

    public Map< Town, Apartment[]> flats = new HashMap<Town, Apartment[]>();

    static class Apartment {
        int x1,x2,y1,y2,z1,z2,xC,zC,price;
        String owner;
        public Apartment(int x1,int x2,int y1,int y2,int z1,int z2,int xC, int zC,int price,String owner){
        this.x1 =x1;
            this.x2 =x2 ;
            this.y1 =y1 ;
            this.y2 =y2 ;
            this.z1 =z1 ;
            this.z2 =z2 ;
            this.zC =zC ;
            this.xC =xC ;
            this.price = price;
            this.owner =owner;
        }
        /*void setX1(int pos){
            x1= pos;
        }
        void setX2(int pos){
            x2= pos;
        }
        void setY1(int pos){
            y1= pos;
        }
        void setY2(int pos){
            y2= pos;
        }
        void setZ1(int pos){
            z1= pos;
        }
        void setZ2(int pos){
            z2= pos;
        }
        void setxC(int pos){
            z2= pos;
        }
        void setzC(int pos){
            z2= pos;
        }
        void setPrice(int pos){
            price= pos;
        }
        void setOwner(String pos){
            owner = pos;
        }*/


    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupEconomy() ) {
            Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
        flatlim = getConfig().getInt("FlatLimitPerChunk");
        if (getConfig().contains("saved")){
        getConfig().getConfigurationSection("saved").getKeys(false).forEach(key ->{

            List<Apartment> b = new ArrayList<>();
                getConfig().getConfigurationSection("saved."+key).getKeys(false).forEach(subkey -> {

                    Apartment cacheT = new Apartment(Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".x1").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".x2").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".y1").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".y2").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".z1").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".z2").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".zC").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".zC").toString()),
                                Integer.parseInt(getConfig().get("saved." + key +"." +subkey+".price").toString()),
                                getConfig().get("saved." + key +"."+subkey+ ".owner").toString());
                    b.add(cacheT);
                });

        Town t = TownyUniverse.getInstance().getTown(key);
            flats.put( t ,b.toArray(b.toArray(new Apartment[0])));
        });}


        getServer().getPluginCommand("tapp").setExecutor(new Commands(this));
        getServer().getPluginManager().registerEvents(new BreakBlock(this ),this);
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Saving... !");
        Save();
        Bukkit.getConsoleSender().sendMessage("[TAPP] - Disabled !");

    }

    /* void Load(){
        flats.clear();
        if (getConfig().contains("saved")){

                Town t = TownyUniverse.getInstance().getTown(key);

                flats.put( t ,loader);
            });}
    }*/
    void Save(){
        reloadConfig();
        getConfig().set("saved",null);

        for (Map.Entry<Town,Apartment[]> entry : flats.entrySet()){
            for(int g=0;g<entry.getValue().length;g++) {
                getConfig().set("saved." + entry.getKey() + "." + g + ".x1", entry.getValue()[g].x1);
                getConfig().set("saved." + entry.getKey() + "." + g + ".x2", entry.getValue()[g].x2);
                getConfig().set("saved." + entry.getKey() + "." + g + ".y1", entry.getValue()[g].y1);
                getConfig().set("saved." + entry.getKey() + "." + g + ".y2", entry.getValue()[g].y2);
                getConfig().set("saved." + entry.getKey() + "." + g + ".z1", entry.getValue()[g].z1);
                getConfig().set("saved." + entry.getKey() + "." + g + ".z2", entry.getValue()[g].z2);
                getConfig().set("saved." + entry.getKey() + "." + g + ".xC", entry.getValue()[g].xC);
                getConfig().set("saved." + entry.getKey() + "." + g + ".zC", entry.getValue()[g].zC);
                getConfig().set("saved." + entry.getKey() + "." + g + ".owner", entry.getValue()[g].owner);
                getConfig().set("saved." + entry.getKey() + "." + g + ".price", entry.getValue()[g].price);
            }
        }
        saveConfig();
    }

    //save data
public FileConfiguration getConfigFile(){
        return config;
}
    }




