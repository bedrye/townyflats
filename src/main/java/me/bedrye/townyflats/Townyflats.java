package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.palmergames.bukkit.towny.object.Town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Townyflats extends JavaPlugin {
    public Map<String, Apartment> cache = new HashMap<String, Apartment>();
    public static Economy econ = null;
    FileConfiguration config = getConfig();
    public int flatlim;
    public Map< Town, ArrayList<Apartment>> flats = new HashMap<Town, ArrayList<Apartment>>();

    static class Apartment {
        int x1,x2,y1,y2,z1,z2,xC,zC,price,xA,zA;
        int yA = -100;
        String owner,world;
        ArmorStand[] entitys= new ArmorStand[4] ;
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
        void setLocation1(Location loc)
        {
            x1 = loc.getBlockX();
            y1 = loc.getBlockY();
            z1 = loc.getBlockZ();
        }
        void setLocation2(Location loc)
        {
            x2 = loc.getBlockX();
            y2 = loc.getBlockY();
            z2 = loc.getBlockZ();
        }
        void setOwner(Player pl){
            owner = pl.getUniqueId().toString();
            price = -1;
        }
        boolean testIfInApartment(Location loc){
             return (((x1 >= loc.getBlockX()  && x2 <= loc.getBlockX())
            || (x1 <= loc.getBlockX() && x2 >= loc.getBlockX()))
            && ((y1 >= loc.getBlockY() && y2 <= loc.getBlockY())
                    || (y1 <= loc.getBlockY() && y2 >= loc.getBlockY()))
                    && ((z1 >= loc.getBlockZ() && z2 <= loc.getBlockZ())
                    || (z1 <= loc.getBlockZ() && z2 >= loc.getBlockZ())));
        }
        void SetHologram(Location loc) {
            world = loc.getWorld().getName();
            xA = loc.getBlockX();
            zA = loc.getBlockZ();
            yA = loc.getBlockY();
            entitys[0] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.3, 0), EntityType.ARMOR_STAND);
            entitys[1] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
            entitys[2] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
            entitys[3] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.40, 0), EntityType.ARMOR_STAND);
            entitys[3].setCustomName("§3FOR SALE");
            entitys[2].setCustomName("Owner: §l" + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName());
            entitys[1].setCustomName("Price §l" + price + "$");
            entitys[0].setCustomName("§6§LRight-Click to buy");
            for (ArmorStand ent : entitys) {
                ent.setGravity(false);
                ent.setCanPickupItems(false);
                ent.setCustomNameVisible(true);
                ent.setVisible(false);
                ent.setSmall(true);
                ent.setInvisible(true);

            }

            loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 2004);
        }
            void RemoveHologram(){
            if (yA!=-100){
                for (ArmorStand ent:entitys) {
                    yA=-100;
                    ent.remove();
                }
                yA=-100;
            }}
            void BuyPlot(Player pl){
                econ.withdrawPlayer(pl,price );
                OfflinePlayer plOff = Bukkit.getOfflinePlayer(UUID.fromString(owner));
                econ.depositPlayer(plOff,price );
                owner = ""+pl.getUniqueId();
                pl.sendMessage("§L§0[§4TAPP§L§0]§f You have bought this property for §L"+price + "$");
                price = -1;
                RemoveHologram();
                pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES,1,1);
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

           ArrayList<Apartment> b = new ArrayList<>();
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

                                getConfig().get("saved." + key +"."+subkey+ ".owner").toString()
                                );
                   if( getConfig().contains("saved." + key +"." +subkey+".xA")&&getConfig().contains("saved." + key +"." +subkey+".yA")&& getConfig().contains("saved." + key +"." +subkey+".zA")) {
                       Location loc = new Location(Bukkit.getWorld(getConfig().get("saved." + key + "." + subkey + ".world").toString()),
                               Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".xA").toString()),
                               Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".yA").toString()),
                               Integer.parseInt(getConfig().get("saved." + key + "." + subkey + ".zA").toString()));
                       cacheT.SetHologram(loc);
                   }
                    b.add(cacheT);
                });

        Town t = TownyUniverse.getInstance().getTown(key);
            flats.put( t , b);
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

        for (Map.Entry<Town,ArrayList<Apartment>> entry : flats.entrySet()){
            int k= 0;
            for(Apartment g: entry.getValue()) {
                getConfig().set("saved." + entry.getKey() + "." + k + ".x1", g.x1);
                getConfig().set("saved." + entry.getKey() + "." + k + ".x2", g.x2);
                getConfig().set("saved." + entry.getKey() + "." + k + ".y1", g.y1);
                getConfig().set("saved." + entry.getKey() + "." + k + ".y2", g.y2);
                getConfig().set("saved." + entry.getKey() + "." + k + ".z1", g.z1);
                getConfig().set("saved." + entry.getKey() + "." + k + ".z2", g.z2);
                getConfig().set("saved." + entry.getKey() + "." + k + ".xC", g.xC);
                getConfig().set("saved." + entry.getKey() + "." + k + ".zC", g.zC);
                getConfig().set("saved." + entry.getKey() + "." + k + ".owner", g.owner);
                getConfig().set("saved." + entry.getKey() + "." + k + ".price", g.price);
                if (g.yA!=-100) {
                    getConfig().set("saved." + entry.getKey() + "." + k + ".world", g.world);
                    getConfig().set("saved." + entry.getKey() + "." + k + ".xA", g.xA);
                    getConfig().set("saved." + entry.getKey() + "." + k + ".yA", g.yA);
                    getConfig().set("saved." + entry.getKey() + "." + k + ".zA", g.zA);
                    g.RemoveHologram();
                }
                k++;
            }
        }
        saveConfig();
    }

    //save data
public FileConfiguration getConfigFile(){
        return config;
}
    }




