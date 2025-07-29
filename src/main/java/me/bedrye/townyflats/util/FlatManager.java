package me.bedrye.townyflats.util;


import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import me.bedrye.townyflats.TownyFlats;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin;

public final class FlatManager {
    private static FlatManager flatmanager;
    private FlatManager(){

    }
    public static FlatManager Instance(){
        if(flatmanager==null){ flatmanager = new FlatManager();}
        return flatmanager;

    }
    private final ArrayList<UUID> hasCoolDown = new ArrayList<>();
    private final ArrayList<Apartment> flats = new ArrayList<>();
    private final Map<Town,ArrayList<Apartment>> TFlats = new HashMap<>();
    private final Map<String,ArrayList<Apartment>> CFlats = new HashMap<>();
    private final Map<String, LocatorLoc> cache = new HashMap<>();
    public boolean hasPlayerCoolDown(UUID a){
        return hasCoolDown.contains(a);

    }
    public void addPlayerCoolDown(UUID a){
         hasCoolDown.add(a);
    }
    public void removePlayerCoolDown(UUID a){
        hasCoolDown.remove(a);
    }
    public ArrayList<Apartment> getApartments(Town a){
        if(TFlats.containsKey(a)) {
            return TFlats.get(a);
        }
        return new ArrayList<>();

    }
    public ArrayList<Apartment> getApartments(Chunk a){
        if(CFlats.containsKey(a.getX() + "" + a.getZ() + a.getWorld().getName())) {
            return CFlats.get(a.getX() + "" + a.getZ() + a.getWorld().getName());
        }
        return new ArrayList<>();
    }
    public ArrayList<Apartment> getApartments(TownBlock a){
        if(CFlats.containsKey(a.getX() + "" + a.getZ() + a.getWorld().getName())) {
        return CFlats.get(a.getX()+""+a.getZ()+a.getWorld().getName());
        }
        return new ArrayList<>();
    }
    public void addApartment(Apartment ap){
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
    public void removeApartment(Apartment ap){
        flats.remove(ap);
        TFlats.get(ap.getTown()).remove(ap);
        CFlats.get(ap.getChunkStringID()).remove(ap);
        if(TFlats.get(ap.getTown()).size()==0){
            File fl = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+ap.getTown().getName()+".yml");
            fl.delete();        }

        if(CFlats.get(ap.getChunkStringID()).size()==0) {
            CFlats.remove(ap.getChunkStringID());
        }
    }




    public void save(){
        for(Apartment g: flats) {
            g.SaveFile();
            g.RemoveHologram();
        }
    }
    public void addTemporary(String s,LocatorLoc l){
        cache.put(s,l);
    }
    public void removeTemporary(String s){
        cache.remove(s);
    }
    public boolean hasTemporary(String s){
        return  cache.containsKey(s);
    }
    public LocatorLoc getTemporary(String s){
        return  cache.get(s);
    }


    public void ClaimApartment(Player pl, Block selectedBl1, boolean rightClick, Location loc){

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
        int myInt = rightClick ? 2 : 1;
        pl.sendMessage(TownyFlats.getLang().tapp  + " pos "+myInt+" (x: " + loc.getBlockX() + "; y: " + loc.getBlockZ() + "; z: " + loc.getBlockZ()+ ")");
        pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
    }
    public void putNew(Town town){
        putNewTown(town) ;
    }
    public void putNew(TownBlock chunk) {
        putNewChunk(chunk.getX()+""+chunk.getZ()+chunk.getWorld().getName());
    }
    public boolean putNewTown(Town town){
        if (!TFlats.containsKey(town)) {
            TFlats.put(town, new ArrayList<>());
            return true;
        }
        return  false;
    }
    public boolean putNewChunk(String s){
        if (!CFlats.containsKey(s)) {
            CFlats.put(s, new ArrayList<>());
            return true;
        }
        return  false;
    }
    public Apartment GetApartmentOrNull(Player pl){
        for (Apartment apartment:getApartments(pl.getLocation().getChunk())) {
            if (apartment.testIfInApartment(pl.getLocation()))
            {
                return apartment;
            }
        }
        return null;
    }
    public Apartment GetApartmentOrNull(Location loc){
        for (Apartment apartment:getApartments(loc.getChunk())) {
            if (apartment.testIfInApartment(loc))
            {
                return apartment;
            }
        }
        return null;
    }

}
