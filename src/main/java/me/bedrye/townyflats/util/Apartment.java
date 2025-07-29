package me.bedrye.townyflats.util;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.bedrye.townyflats.TownyFlats;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import static org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin;

public class Apartment {
    private final int x1,x2,y1,y2,z1,z2,xC,zC;
    private int price;
    //private int yA = -100;
    private Resident owner;
    private final String world,ID,name;
    private final Town town;
    private final ArrayList<Resident> residents = new ArrayList<>();
    //private boolean hasHologram =false;
    private Hologram hologram ;
    public Location getLocation1(){
        return new Location(Bukkit.getServer().getWorld("world"),x1,y1,z1);
    }
    public Location getLocation2(){
        return new Location(Bukkit.getServer().getWorld("world"),x2,y2,z2);
    }
    public Town getTown(){
        return town;
    }
    public Resident getOwner(){
        return owner;
    }
    public boolean hasHologram(){return hologram!=null;}
    public String getChunkStringID(){
        return xC+""+zC+world;

    }

    public int getPrice() {
        return price;
    }
    public String getForSalePrice(){

        if(price>=0) return price+"";
        return "NOT FOR SALE";
    }

    public String getID() {
        return ID;
    }

    public Apartment(LocatorLoc loc, int price, Resident owner, String name, Town town){
        if (loc.getX1()>loc.getX2()) {
            x1 = loc.getX1();
            x2 = loc.getX2();
        }
        else {
            x1 = loc.getX2();
            x2 = loc.getX1();
        }
        if (loc.getY1()>loc.getY2()) {
            y1 = loc.getY1();
            y2 = loc.getY2();
        }
        else {
            y1 = loc.getY2();
            y2 = loc.getY1();
        }
        if (loc.getZ1()>loc.getZ2()) {
            z1 = loc.getZ1();
            z2 = loc.getZ2();
        }
        else {
            z1 = loc.getZ2();
            z2 = loc.getZ1();
        }
        xC = loc.getChunk().getX();
        zC = loc.getChunk().getZ();
        this.price = price;
        this.owner = owner ;
        this.world = loc.getChunk().getWorld().getName();
        this.name = name;
        this.town = town;
        ID = x1+""+x2+""+y1+""+y2+""+z1+""+z2+ world;

    }
    public Apartment(int x1, int x2, int y1, int y2, int z1, int z2, int xC, int zC, int price, Resident owner, String world, String name, Town town){
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
        this.world = world;
        this.name = name;
        this.town = town;
        ID = x1+""+x2+""+y1+""+y2+""+z1+""+z2+ world;

    }

    public void setOwner(Resident pl){
        ChangePlayer(pl);
        price = -1;
    }

    public ArrayList<Resident> getResidents() {
        return residents;
    }

    public void addResident(Resident arg){
        if (!residents.contains(arg)&&arg!=owner) {
            residents.add(arg);
            File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
            FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
            userconfig.set("saved." +getChunkStringID()+"."+ID+ ".residents." + (residents.size() - 1), arg.getName());
            try {
                userconfig.save(userfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public boolean hasResident(Resident arg){
    for (Resident bg: residents){
         if (arg.equals(bg.getPlayer().getName())){return true;}
    }
    return false;
    }
    public void removeResident(Resident pl){
        if (residents.contains(pl)) {
            residents.remove(pl);
            File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
            FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
            userconfig.set("saved."+getChunkStringID()+"."+ ID + ".residents", null);
            int d = 0;
            for (Resident pil : residents) {
                userconfig.set("saved."+getChunkStringID()+"."+ ID + ".residents." + d, pil.getPlayer().getName());
                d++;
            }
            try {
                userconfig.save(userfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean testIfInApartment(Location loc){
        return ((x1 >= loc.getBlockX()  && x2 <= loc.getBlockX())
                && (y1 >= loc.getBlockY() && y2 <= loc.getBlockY())
                && (z1 >= loc.getBlockZ() && z2 <= loc.getBlockZ()));
    }
    public void SetHologram(Location loc) {

        if(TownyFlats.getInstance().HologramEnable) hologram = new Hologram(this,loc);
    }
    public void SaveFile(){
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".x1",x1);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".x2",x2);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".y1",y1);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".y2",y2);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".z1",z1);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".z2",z2);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".xC",xC);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".zC",zC);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".owner",owner.getName());
        userconfig.set("saved."+getChunkStringID()+"."+ID+".price",price);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".world",world);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".name",name);
        if (hasHologram()) {
            userconfig.set("saved."+getChunkStringID()+"."+ID+".xA", hologram.getX());
            userconfig.set("saved."+getChunkStringID()+"."+ID+".yA", hologram.getY());
            userconfig.set("saved."+getChunkStringID()+"."+ID+".zA", hologram.getZ());
        }
        int d =0;
        for (Resident pl:residents){
            userconfig.set("saved."+getChunkStringID()+"."+ID+".residents."+d,pl.getName());
            d++;
        }
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void DeleteFile(){
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+getChunkStringID()+"."+ID, null);
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteToMaps(){
        FlatManager.Instance().addApartment(this);
    }
    /*void Deletion(){
        new File(tf.getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml").delete();
    }*/
    public void ChangePlayer(Resident pl){
        residents.clear();
        owner = pl;
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".owner",owner.getPlayer().getName());
        userconfig.set("saved."+getChunkStringID()+"."+ID+".price",-1);
        userconfig.set("saved."+getChunkStringID()+"."+ID+".residents",null);
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void RemoveFromMaps(){
        FlatManager.Instance().removeApartment(this);

    }
    public void HologramInfo(Player pl) {
        new BukkitRunnable() {
            private int i = 0;
            public void run() {
                int a=0;
                int b=0;
                int c=0;
                int d=0;
                int e=0;
                int f=0;
                if(x1>x2){a=1;}else {b=1;}
                if(y1>y2){c=1;}else {d=1;}
                if(z1>z2){e=1;}else {f=1;}
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), (x1 + x2) / 2, y1+c, z1+e), Math.abs(x1 - x2) * 3, Math.abs(x1 - x2) / 2, 0, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), (x1 + x2) / 2, y2+d, z1+e), Math.abs(x1 - x2) * 3, Math.abs(x1 - x2) / 2, 0, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), (x1 + x2) / 2, y2+d, z2+f), Math.abs(x1 - x2) * 3, Math.abs(x1 - x2) / 2, 0, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), (x1 + x2) / 2, y1+c, z2+f), Math.abs(x1 - x2) * 3, Math.abs(x1 - x2) / 2, 0, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x1+a, (y1 + y2) / 2, z1+e), Math.abs(y1 - y2) * 3, 0, Math.abs(y1 - y2) / 2, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x2+b, (y1 + y2) / 2, z1+e), Math.abs(y1 - y2) * 3, 0, Math.abs(y1 - y2) / 2, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x2+b, (y1 + y2) / 2, z2+f), Math.abs(y1 - y2) * 3, 0, Math.abs(y1 - y2) / 2, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x1+a, (y1 + y2) / 2, z2+f), Math.abs(y1 - y2) * 3, 0, Math.abs(y1 - y2) / 2, 0);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x1+a, y1+c, (z1 + z2) / 2), Math.abs(z1 - z2) * 3, 0, 0, Math.abs(z1 - z2) / 2);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x2+b, y1+c, (z1 + z2) / 2), Math.abs(z1 - z2) * 3, 0, 0, Math.abs(z1 - z2) / 2);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x2+b, y2+d, (z1 + z2) / 2), Math.abs(z1 - z2) * 3, 0, 0, Math.abs(z1 - z2) / 2);
                pl.spawnParticle(Particle.VILLAGER_HAPPY, new Location(Bukkit.getWorld(world), x1+a, y2+d, (z1 + z2) / 2), Math.abs(z1 - z2) * 3, 0, 0, Math.abs(z1 - z2) / 2);
                i++;
                if (i == 30) {
                    cancel();
                }
            }
        }.runTaskTimer(getProvidingPlugin(TownyFlats.class), 20L, 20L);
    }

    public void InfoCommand(Player pl) {
        pl.sendMessage("§l§0[§4TAPP§l§0]§f §2"+ TownyFlats.getLang().command_info_pos1 +" §f(x:" + x1 + ";y:" + y1 + ";z:" + z1 + "); \n" +
                "§2"+ TownyFlats.getLang().command_info_pos2+" §f(x:" + x2 + ";y:" + y2 + ";z:" + z2 + ");\n" +
                "§2"+ TownyFlats.getLang().command_info_owner+" §f" + owner + "; \n"
                );
        if (owner.getName().equals(pl.getName())) {
            TextComponent messagepr =new TextComponent(TownyFlats.getLang().command_info_sell);
            TextComponent messagep =new TextComponent(getForSalePrice()+"; ");
            messagep.setColor(ChatColor.WHITE);
            messagepr.setColor(ChatColor.DARK_GREEN);
            TextComponent messageAct =new TextComponent(TownyFlats.getLang().command_info_sell_button);
            messageAct.setColor(ChatColor.GREEN);
            messageAct.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tapp sell"));
            messagepr.addExtra(messagep);
            messagepr.addExtra(messageAct);
            pl.spigot().sendMessage(messagepr);
            pl.sendMessage("§2"+ TownyFlats.getLang().command_info_roommates);
            for (Resident st : residents) {
                TextComponent message = new TextComponent("- "+st.getName());
                message.setColor(ChatColor.WHITE);
                TextComponent message2 = new TextComponent(TownyFlats.getLang().command_info_roommates_button);
                message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp remove " + st));
                message2.setColor(ChatColor.RED);
                message.addExtra(message2);
                pl.spigot().sendMessage(message);
            }
            return;
        }
        pl.sendMessage("§2"+ TownyFlats.getLang().command_info_sell+" §f" + price + ";");
        pl.sendMessage("§2"+ TownyFlats.getLang().command_info_roommates);
        for (Resident st : residents){
            pl.sendMessage("- "+st.getName());
    }
    }
    public void RemoveHologram() {
        if(hasHologram()) {
            hologram.RemoveHologram();
            hologram = null;
        }

    }
    public void BuyWithHologram(ArmorStand e,Resident pl) {
        if(hasHologram())
            hologram.BuyWithHologram(e,pl);
    }
    public boolean CanBuyPlot(Resident res){
        return  res.getAccount().getHoldingBalance() >= price
                && (res.getTownOrNull()==town || TownyUniverse.getInstance().getTownBlockOrNull(new WorldCoord(world, xC, zC)).getType().equals(TownBlockType.EMBASSY));

    }
    public void SetForSale(int p,Location loc){
        RemoveHologram();
        SetPrice(p);
        SetHologram(loc);
        
    }
    public void SetPrice(int pr){
        price = pr;
        SaveFile();

    }
    public boolean BuyPlot(Resident res){
        if(CanBuyPlot(res)) {
            // econ.withdrawPlayer(pl,price );
            res.getAccount().payTo(price, owner.getAccount(), "Buying property");
            // econ.depositPlayer(plOff,price );
            ChangePlayer(res);
            res.getPlayer().sendMessage("§L§0[§4TAPP§L§0]§f" + TownyFlats.getLang().command_buy_true + "§L" + price + TownyFlats.getLang().money_symbol);
            price = -1;
            RemoveHologram();
            res.getPlayer().getWorld().playSound(res.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            SaveFile();
            return true;
        }
    return false;
    }
    public boolean IsAllowedToAdminister(Resident resident){
        return (resident.equals(owner)||resident.equals(town.getMayor()));
    }
    public boolean IsAllowedToUse(Resident resident){
        return IsAllowedToAdminister(resident)||residents.contains(resident);

    }

    public String getName() {
        return name;
    }
}
