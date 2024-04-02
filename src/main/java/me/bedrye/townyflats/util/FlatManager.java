package me.bedrye.townyflats.util;


import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import me.bedrye.townyflats.TownyFlats;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin;

public final class FlatManager {
    private static final ArrayList<UUID> hasCoolDown = new ArrayList<>();
    private final static ArrayList<Apartment> flats = new ArrayList<>();
    private final static Map<Town,ArrayList<Apartment>> TFlats = new HashMap<>();
    private final static Map<String,ArrayList<Apartment>> CFlats = new HashMap<>();
    private static final Map<String, LocatorLoc> cache = new HashMap<>();
    public static boolean hasPlayerCoolDown(UUID a){
        return hasCoolDown.contains(a);

    }
    public static void addPlayerCoolDown(UUID a){
         hasCoolDown.add(a);
    }
    public static void removePlayerCoolDown(UUID a){
        hasCoolDown.remove(a);
    }
    public static ArrayList<Apartment> getApartments(Town a){
        if(TFlats.containsKey(a)) {
            return TFlats.get(a);
        }
        return new ArrayList<>();

    }
    public static ArrayList<Apartment> getApartments(Chunk a){
        if(CFlats.containsKey(a.getX() + "" + a.getZ() + a.getWorld().getName())) {
            return CFlats.get(a.getX() + "" + a.getZ() + a.getWorld().getName());
        }
        return new ArrayList<>();
    }
    public static ArrayList<Apartment> getApartments(TownBlock a){
        if(CFlats.containsKey(a.getX() + "" + a.getZ() + a.getWorld().getName())) {
        return CFlats.get(a.getX()+""+a.getZ()+a.getWorld().getName());
        }
        return new ArrayList<>();
    }
    public static void addApartment(Apartment ap){
        flats.add(ap);
        if(!TFlats.containsKey(ap.getTown())) {
            TFlats.put(ap.getTown(), new ArrayList<>());
        }
        TFlats.get(ap.getTown()).add(ap);
        if(!CFlats.containsKey(ap.getChunkStringID())) {
            CFlats.put(ap.getChunkStringID(), new ArrayList<>());
        }
        CFlats.get(ap.getChunkStringID()).add(ap);

    }
    public static void removeApartment(Apartment ap){
        flats.remove(ap);
        if(TFlats.get(ap.getTown()).size()==1){
            TFlats.remove(ap.getTown());
            File fl = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+ap.getTown().getName()+".yml");
            fl.delete();        }
        else {
            TFlats.get(ap.getTown()).remove(ap);
        }
        if(CFlats.get(ap.getChunkStringID()).size()==1) {
            CFlats.remove(ap.getChunkStringID());
        }else {
            CFlats.get(ap.getChunkStringID()).remove(ap);
        }
    }




    public static void save(){
        for(Apartment g: flats) {
            g.SaveFile();
            g.RemoveHologram();
        }
    }
    public static void addTemporary(String s,LocatorLoc l){
        cache.put(s,l);
    }
    public static void removeTemporary(String s){
        cache.remove(s);
    }
    public static boolean hasTemporary(String s){
        return  cache.containsKey(s);
    }
    public static LocatorLoc getTemporary(String s){
        return  cache.get(s);
    }


    public static void ClaimApartment(Player pl, Block selectedBl1, boolean rightClick, Location loc){

        if (cache.containsKey(pl.getName())) {
            if (!cache.get(pl.getName()).getChunk().equals(selectedBl1.getLocation().getChunk())) {
                cache.remove(pl.getName());
                pl.sendMessage(TownyFlats.getLang().tapp + TownyFlats.getLang().clear_pos);
                return;
            }
        }

        else {
            cache.put(pl.getName(), new LocatorLoc(selectedBl1.getChunk()));
        }
        if (rightClick) cache.get(pl.getName()).setLocation1(loc);
        else cache.get(pl.getName()).setLocation2(loc);
        pl.sendMessage(TownyFlats.getLang().tapp  + " pos "+rightClick+" (x: " + loc.getBlockX() + "; y: " + loc.getBlockZ() + "; z: " + loc.getBlockZ()+ ")");
        pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
    }
    public static void putNew(Town town){
        if (putNewTown(town)) {
            new File(getProvidingPlugin(TownyFlats.class).getDataFolder() + File.separator + "userdata" + File.separator + town.getName() + ".yml");
        }
    }
    public static void putNew(TownBlock chunk) {
        try {
            if(putNewChunk(chunk.getX()+""+chunk.getZ()+chunk.getWorld().getName())) {
                File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder() + File.separator + "userdata" + File.separator + chunk.getTown().getName() + ".yml");
                FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
                userconfig.set("saved." + chunk.getX() + "" + chunk.getZ() + chunk.getWorld().getName(), "1");
                userconfig.save(userfile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean putNewTown(Town town){
        if (!TFlats.containsKey(town)) {
            TFlats.put(town, new ArrayList<>());
            return true;
        }
        return  false;
    }
    public static boolean putNewChunk(String s){
        if (!CFlats.containsKey(s)) {
            CFlats.put(s, new ArrayList<>());
            return true;
        }
        return  false;
    }
    public static Apartment GetApartmentOrNull(Player pl){
        for (Apartment apartment:FlatManager.getApartments(pl.getLocation().getChunk())) {
            if (apartment.testIfInApartment(pl.getLocation()))
            {
                return apartment;
            }
        }
        return null;
    }
    public static Apartment GetApartmentOrNull(Location loc){
        for (Apartment apartment:FlatManager.getApartments(loc.getChunk())) {
            if (apartment.testIfInApartment(loc))
            {
                return apartment;
            }
        }
        return null;
    }

}
