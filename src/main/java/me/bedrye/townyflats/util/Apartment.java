package me.bedrye.townyflats.util;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import me.bedrye.townyflats.TownyFlats;
import me.bedrye.townyflats.util.LocatorLoc;
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
import java.util.Objects;

import static org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin;

public class Apartment {
    public final int x1,x2,y1,y2,z1,z2,xC,zC;
    public int price, xA,zA;
    public int yA = -100;
    public String owner;
    public final String world,ID,name;
    public final Town town;
    public ArrayList<String> residents = new ArrayList<>();
    public boolean hasHologram =false;
    public ArmorStand[] entitys= new ArmorStand[4] ;
    public  final TownyFlats tf;
    public Apartment(LocatorLoc loc, int price, String owner, String name, Town town, TownyFlats tf){
        this.x1 =loc.x1;
        this.x2 =loc.x2 ;
        this.y1 =loc.y1 ;
        this.y2 =loc.y2 ;
        this.z1 =loc.z1 ;
        this.z2 =loc.z2 ;
        this.zC =loc.zC ;
        this.xC =loc.xC ;
        this.price = price;
        this.owner =owner;
        this.world = loc.world;
        this.name = name;
        this.town = town;
        this.tf = tf;
        ID = x1+""+x2+""+y1+""+y2+""+z1+""+z2+ world;

    }
    public Apartment(int x1, int x2, int y1, int y2, int z1, int z2, int xC, int zC, int price, String owner, String world, String name, Town town, TownyFlats tf){
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
        this.tf = tf;
        ID = x1+""+x2+""+y1+""+y2+""+z1+""+z2+ world;

    }
    public void setOwner(Player pl){
        ChangePlayer(pl);
        price = -1;
    }
    public void addResident(String arg){
        if (!residents.contains(arg)) {
            residents.add(arg);
            if (!tf.PFlats.containsKey(arg)) {
                tf.PFlats.put(arg, new ArrayList<>());
            }
            tf.PFlats.get(arg).add(this);
            File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
            FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
            userconfig.set("saved." + ID + ".residents." + (residents.size() - 1), arg);
            try {
                userconfig.save(userfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public boolean hasResident(String arg){
    for (String bg: residents){
         if (arg.equals(bg)){return true;}
    }
    return false;
    }
    public void removeResident(String pl){
        if (residents.contains(pl)) {
            residents.remove(pl);
            if (tf.PFlats.get(pl).size() == 1) {
                tf.PFlats.remove(pl);
            } else {
                tf.PFlats.get(pl).remove(this);
            }
            File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
            FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
            userconfig.set("saved." + ID + ".residents", null);
            int d = 0;
            for (String pil : residents) {
                userconfig.set("saved." + ID + ".residents." + d, pil);
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
        return (((x1 >= loc.getBlockX()  && x2 <= loc.getBlockX())
                || (x1 <= loc.getBlockX() && x2 >= loc.getBlockX()))
                && ((y1 >= loc.getBlockY() && y2 <= loc.getBlockY())
                || (y1 <= loc.getBlockY() && y2 >= loc.getBlockY()))
                && ((z1 >= loc.getBlockZ() && z2 <= loc.getBlockZ())
                || (z1 <= loc.getBlockZ() && z2 >= loc.getBlockZ())));
    }
    public void SetHologram(Location loc) {
        hasHologram =true;
        xA = loc.getBlockX();
        zA = loc.getBlockZ();
        yA = loc.getBlockY();
        entitys[0] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.3, 0), EntityType.ARMOR_STAND);
        entitys[1] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
        entitys[2] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
        entitys[3] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.40, 0), EntityType.ARMOR_STAND);
        entitys[3].setCustomName(TownyFlats.getLang().hologram_1);
        entitys[2].setCustomName(TownyFlats.getLang().command_info_owner+" §l" + owner);
        entitys[1].setCustomName(TownyFlats.getLang().command_info_sell+" §l" + price + TownyFlats.getLang().money_symbol);
        entitys[0].setCustomName(TownyFlats.getLang().hologram_4);
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
    public void SaveFile(){
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+ID+".x1",x1);
        userconfig.set("saved."+ID+".x2",x2);
        userconfig.set("saved."+ID+".y1",y1);
        userconfig.set("saved."+ID+".y2",y2);
        userconfig.set("saved."+ID+".z1",z1);
        userconfig.set("saved."+ID+".z2",z2);
        userconfig.set("saved."+ID+".xC",xC);
        userconfig.set("saved."+ID+".zC",zC);
        userconfig.set("saved."+ID+".owner",owner);
        userconfig.set("saved."+ID+".price",price);
        userconfig.set("saved."+ID+".world",world);
        userconfig.set("saved."+ID+".name",name);
        if (yA!=-100) {
            userconfig.set("saved."+ID+".xA", xA);
            userconfig.set("saved."+ID+".yA", yA);
            userconfig.set("saved."+ID+".zA", zA);
        }
        int d =0;
        for (String pl:residents){
            userconfig.set("saved."+ID+".residents."+d,pl);
            d++;
        }
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void DeleteFile(Town t){
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+t.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+ID, null);
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteToMaps(){
        tf.flats.add(this);
        if(!tf.TFlats.containsKey(town)) {
            tf.TFlats.put(town, new ArrayList<>());
        }
        tf.TFlats.get(town).add(this);
        if(!tf.PFlats.containsKey(owner)) {
            tf.PFlats.put(owner, new ArrayList<>());
        }
        tf.PFlats.get(owner).add(this);
        if(!tf.CFlats.containsKey(xC+""+zC+world)) {
            tf.CFlats.put(xC+""+zC+world, new ArrayList<>());
        }
        tf.CFlats.get(xC+""+zC+world).add(this);
        for (String rs :residents){
            if(!tf.PFlats.containsKey(rs)) {
                tf.PFlats.put(rs, new ArrayList<>());
            }
            tf.PFlats.get(rs).add(this);
        }
    }
    void Deletion(){
        new File(tf.getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml").delete();
    }
    public void ChangePlayer(Player pl){
        tf.PFlats.get(owner).remove(this);
        for (String p: residents) {
            tf.PFlats.get(p).remove(this);
        }
        residents.clear();
        if(!tf.PFlats.containsKey(pl.getName())) {
            tf.PFlats.put(pl.getName(), new ArrayList<>());
        }
        tf.PFlats.get(pl.getName()).add(this);
        owner = pl.getName();
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+ID+".owner",owner);
        userconfig.set("saved."+ID+".price",-1);
        userconfig.set("saved."+ID+".residents",null);
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void RemoveFromMaps(Town t){
        tf.flats.remove(this);
        if(tf.TFlats.get(t).size()==1){
            tf.TFlats.remove(t);
            File fl = new File(tf.getDataFolder()+File.separator+"userdata"+File.separator+t.getName()+".yml");
            fl.delete();        }
        else {
            tf.TFlats.get(t).remove(this);
        }
        if(tf.PFlats.get(owner).size()==1) {
            tf.PFlats.remove(owner);
        }else {
            tf.PFlats.get(owner).remove(this);
        }
        if(tf.CFlats.get(xC+""+zC+world).size()==1) {
            tf.CFlats.remove(xC+""+zC+world);
        }else {
            tf.CFlats.get(xC+""+zC+world).remove(this);
        }
        for (String rs :residents){
            if(tf.AFlats.get(rs).size()==1) {
                tf.AFlats.remove(rs);
            }else {
                tf.AFlats.get(rs).remove(this);
            }
        }
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
        if (owner.equals(pl.getName())) {
            TextComponent messagepr =new TextComponent(TownyFlats.getLang().command_info_sell);
            TextComponent messagep =new TextComponent(price+" ;");
            messagep.setColor(ChatColor.WHITE);
            messagepr.setColor(ChatColor.DARK_GREEN);
            TextComponent messageAct =new TextComponent(TownyFlats.getLang().command_info_sell_button);
            messageAct.setColor(ChatColor.GREEN);
            messageAct.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tapp sell"));
            messagepr.addExtra(messagep);
            messagepr.addExtra(messageAct);
            pl.spigot().sendMessage(messagepr);
            pl.sendMessage("§2"+ TownyFlats.getLang().command_info_roommates);
            for (String st : residents) {
                TextComponent message = new TextComponent("- "+st);
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
        for (String st : residents){
            pl.sendMessage("- "+st);
    }
    }
    public void RemoveHologram(){
        if (hasHologram){
            for (ArmorStand ent:entitys) {
                ent.remove();
            }
            hasHologram=false;
            yA = -100;
        }}
    public void SetPrice(int pr){
        price = pr;
        File userfile = new File(getProvidingPlugin(TownyFlats.class).getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml");
        FileConfiguration userconfig = YamlConfiguration.loadConfiguration(userfile);
        userconfig.set("saved."+ID+".price",pr);
        try {
            userconfig.save(userfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void BuyPlot(Player pl){
       // econ.withdrawPlayer(pl,price );
        OfflinePlayer plOff = Bukkit.getOfflinePlayer(owner);
        Objects.requireNonNull(TownyUniverse.getInstance().getResident(pl.getUniqueId())).getAccount().payTo(price, Objects.requireNonNull(TownyUniverse.getInstance().getResident(plOff.getUniqueId())).getAccount(),"Buying property");
       // econ.depositPlayer(plOff,price );
        ChangePlayer(pl);
        pl.sendMessage("§L§0[§4TAPP§L§0]§f"+ TownyFlats.getLang().command_buy_true+"§L"+price + TownyFlats.getLang().money_symbol);
        price = -1;
        RemoveHologram();
        pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_VILLAGER_YES,1,1);
    }
}
