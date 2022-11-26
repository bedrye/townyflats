package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.WorldCoord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class Commands implements CommandExecutor {
    public final Townyflats townyflats;
    private final String tapp ="§l§0[§4TAPP§l§0]§f";
    private int former;
    private int lengTh;
    public Commands(Townyflats townyflats){
        this.townyflats = townyflats;
}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tapp")) {
            if (sender instanceof Player) {

                Player pl = (Player) sender;
                if (townyflats.haveCooldowns.contains(pl.getUniqueId())) {
                    pl.sendMessage(tapp + Townyflats.getLang().command_antispam);
                    return true;

                }
                townyflats.haveCooldowns.add(pl.getUniqueId());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(townyflats, new Runnable() {
                    public void run() {
                        townyflats.haveCooldowns.remove(pl.getUniqueId());
                    }
                }, 20L);

                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (args.length>0){
                switch (args[0]) {

                    case "claim":
                        if (args.length==2) {
                            if (pl.hasPermission("townyapartments.claim")|| Objects.requireNonNull(res).isMayor()) {
                                Claim(pl, res, args[1]);
                            }
                        }
                        break;
                    case "delete":
                        if (pl.hasPermission("townyapartments.delete")|| Objects.requireNonNull(res).isMayor()) {
                            Delete(pl);
                        }
                        break;
                    case "buy":
                        Buy(pl);
                        break;
                    case "sell":
                        if (args.length==2) {
                            if (Integer.parseInt(args[1])>=0) {
                                Sell(pl, args[1]);
                            }else {pl.sendMessage(tapp +  Townyflats.getLang().command_sell_false);}
                        } else {
                            pl.sendMessage(tapp + "/tapp sell COST");
                        }
                        break;
                    case "info":
                        if(args.length>=2){
                            assert res != null;
                            infoName(pl,res,args[1]);
                        }
                        else {
                            info(pl);
                        }

                        break;
                    case "list":
                        if (args.length>=2) {
                            switch (args[1]) {
                                case "player":
                                    if (args.length == 2) {
                                        list(pl, 1, pl.getName());
                                    } else if (args.length == 3) {
                                        try {
                                            Integer.parseInt(args[2]);
                                            list(pl, Integer.parseInt(args[2]), pl.getName());
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    } else {
                                        try {
                                            Integer.parseInt(args[2]);
                                            list(pl, Integer.parseInt(args[2]), args[3]);
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");

                                        }
                                    }
                                    break;
                                case "town":
                                    if (args.length == 2) {
                                        assert res != null;
                                        listtown(pl, 1, res.getTownOrNull());
                                    } else if (args.length == 3) {
                                        try {
                                            Integer.parseInt(args[2]);
                                            assert res != null;
                                            listtown(pl, Integer.parseInt(args[2]), res.getTownOrNull());
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    } else {
                                        try {
                                            Integer.parseInt(args[2]);
                                            listtown(pl, Integer.parseInt(args[2]), TownyUniverse.getInstance().getTown(args[3]));
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    }

                                    break;
                                case "chunk":
                                    if (args.length == 2) {
                                        listchunk(pl, 1);
                                    } else if (args.length == 3) {
                                        try {
                                            Integer.parseInt(args[2]);
                                            listchunk(pl, Integer.parseInt(args[2]));
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    }

                                    break;
                            }
                        }
                            break;

                    case "reload":
                        if (pl.hasPermission("townyapartments.reload")){
                            pl.sendMessage("Reloading...");
                            townyflats.Save();
                            townyflats.Load();
                            townyflats.Restart();
                            pl.sendMessage("Done!");
                    }
                        break;
                    case "add":
                        if (args.length==2) {
                            AddResident(pl, args[1]);
                        } else {pl.sendMessage(tapp + "/tapp add PLAYER");}
                        break;
                    case "remove":
                        if (args.length==2) {
                            RemoveResident(pl, args[1]);
                        }else {pl.sendMessage(tapp + "/tapp remove PLAYER");}
                        break;
                    default:
                        pl.sendMessage(tapp + Townyflats.getLang().incorrect_command);
                        break;

                }
    }else{
                    pl.sendMessage(tapp + "§2/tapp claim NAME§f - "+"claims plot if there are 2 positions set;"+"\n " +
                            "§2/tapp delete§f - "+"deletes the plot on which you are currently standing; "+"\n " +
                            "§2/tapp sell COST§f - "+"put the plot for sale;\n" +
                            "§2/tapp buy§f - "+"buys the plot on which you are currently standing; "+"\n "  +
                            "§2/tapp info§f - "+"see the info of the plot on which you are currently standing;" +"\n " +
                            "§2/tapp list§f - "+"see the list of properties you own;"+"\n " +
                            "§2/tapp add PLAYER§f - "+"add a player to the plot on which you are currently standing;" +"\n " +
                            "§2/tapp remove PLAYER§f - "+"remove a player from the plot on which you are currently standing;" +"\n " +
                            "§2/tapp reload§f - "+"Reloads the config");
                }
            }
        }
        return true;
    }
    private void AddResident(Player pl,String arg){
        for (Apartment ap : townyflats.PFlats.get(pl.getName())){
            if(ap.testIfInApartment(pl.getLocation())){
            if (ap.owner.equals(pl.getName())) {
                if (Bukkit.getPlayerExact(arg)!=null) {
                    ap.addResident(arg);
                    pl.sendMessage(tapp+"You have added " +arg+" to this property ");
                    return;
                }
            }
        }
        }
    }
    private void RemoveResident(Player pl,String arg){
        for (Apartment ap : townyflats.PFlats.get(pl.getName())) {
            if (ap.testIfInApartment(pl.getLocation())) {
                if (ap.owner.equals(pl.getName())) {
                    ap.removeResident(arg);
                    pl.sendMessage(tapp+"You have removed " +arg+" from this property ");
                    return;
                }
            }
        }
    }
    private void list(Player pl,int num,String name) {
        pl.sendMessage(tapp + Townyflats.getLang().command_list);
        pl.sendMessage("");
        int i = 0;
        if (townyflats.PFlats.containsKey(name)) {
            for (Apartment ap : townyflats.PFlats.get(name)) {

                if (i >= (num * 5) - 5) {
                    if (i < num * 5) {
                        TextComponent message = new TextComponent((i + 1) + "." + ap.name);
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp info " + ap.ID));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see the info").create()));
                        message.setUnderlined(true);
                        pl.spigot().sendMessage(message);
                        pl.sendMessage("");

                    } else {
                        i++;
                        break;
                    }
                }
                i++;

            }

            TextComponent message = new TextComponent("<------");
            if (num != 1) {
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list player " + (num - 1)+" "+name));
                message.setUnderlined(true);
                message.setColor(ChatColor.GOLD);
            } else {
                message.setColor(ChatColor.DARK_GRAY);
            }

            TextComponent message2 = new TextComponent("------>");
            if (num * 5 - i < 0) {
                message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list player " + (num + 1)+" "+name));
                message2.setUnderlined(true);
                message2.setColor(ChatColor.GOLD);
            } else {
                message2.setColor(ChatColor.DARK_GRAY);
            }
            message.addExtra(" ");
            message.addExtra(message2);

            pl.spigot().sendMessage(message);
        }
    }

    private void listtown(Player pl,int num,Town t) {
        pl.sendMessage(tapp + Townyflats.getLang().command_list);
        pl.sendMessage("");
        int i = 0;
        if (townyflats.TFlats.containsKey(t)) {
            for (Apartment ap : townyflats.TFlats.get(t)) {

                if (i >= (num * 5) - 5) {
                    if (i < num * 5) {
                        TextComponent message = new TextComponent((i + 1) + "." + ap.name);
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp info " + ap.ID));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see the info").create()));
                        message.setUnderlined(true);
                        pl.spigot().sendMessage(message);
                        pl.sendMessage("");

                    } else {
                        i++;
                        break;
                    }
                }
                i++;

            }

            TextComponent message = new TextComponent("<------");
            if (num != 1) {
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list town " + (num - 1)+" "+t.getName()));
                message.setUnderlined(true);
                message.setColor(ChatColor.GOLD);
            } else {
                message.setColor(ChatColor.DARK_GRAY);
            }

            TextComponent message2 = new TextComponent("------>");
            if (num * 5 - i < 0) {
                message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list town " + (num + 1)+" "+t.getName()));
                message2.setUnderlined(true);
                message2.setColor(ChatColor.GOLD);
            } else {
                message2.setColor(ChatColor.DARK_GRAY);
            }
            message.addExtra(" ");
            message.addExtra(message2);

            pl.spigot().sendMessage(message);
        }
    }

    private void listchunk(Player pl,int num) {
        pl.sendMessage(tapp + Townyflats.getLang().command_list);
        pl.sendMessage("");
        String g = pl.getLocation().getChunk().getX()+""+pl.getLocation().getChunk().getZ()+pl.getLocation().getWorld().getName();
        int i = 0;
        if (townyflats.CFlats.containsKey(g)) {
            for (Apartment ap : townyflats.CFlats.get(g)) {

                if (i >= (num * 5) - 5) {
                    if (i < num * 5) {
                        TextComponent message = new TextComponent((i + 1) + "." + ap.name);
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp info " + ap.ID));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see the info").create()));
                        message.setUnderlined(true);
                        pl.spigot().sendMessage(message);
                        pl.sendMessage("");

                    } else {
                        i++;
                        break;
                    }
                }
                i++;

            }

            TextComponent message = new TextComponent("<------");
            if (num != 1) {
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list chunk " + (num - 1)));
                message.setUnderlined(true);
                message.setColor(ChatColor.GOLD);
            } else {
                message.setColor(ChatColor.DARK_GRAY);
            }

            TextComponent message2 = new TextComponent("------>");
            if (num * 5 - i < 0) {
                message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp list chunk " + (num + 1)));
                message2.setUnderlined(true);
                message2.setColor(ChatColor.GOLD);
            } else {
                message2.setColor(ChatColor.DARK_GRAY);
            }
            message.addExtra(" ");
            message.addExtra(message2);

            pl.spigot().sendMessage(message);
        }
    }

    private void Claim(Player pl,Resident res,String id){
        if (townyflats.cache.containsKey(pl.getName())) {
            if (townyflats.cache.get(pl.getName()).y1 !=-100 && townyflats.cache.get(pl.getName()).y2!=-100) {
                if (ChunkTestor(pl,res.getTownOrNull())){
                    Apartment loc =new Apartment(townyflats.cache.get(pl.getName()),-1,pl.getName(),id,res.getTownOrNull(),townyflats);
                    loc.WriteToMaps();
                    pl.sendMessage(tapp+ Townyflats.getLang().command_claim_true);
                    loc.SaveFile();

            }}
                townyflats.cache.remove(pl.getName());
            }

        }

    private void Buy(Player pl){
        Town town;
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(pl.getLocation())).getTownOrNull();
            assert town != null;
            if (town.hasResident(pl.getName())||TownyUniverse.getInstance().getTownBlockOrNull(new WorldCoord( pl.getLocation().getChunk().getWorld().getName(), pl.getLocation().getChunk().getX(), pl.getLocation().getChunk().getZ())).getType().equals(TownBlockType.EMBASSY)){
            if (townyflats.TFlats.containsKey(town)) {
                    if (CityTestor(pl,town)) {
                        int h =townyflats.TFlats.get(town).get(former).price;
                        if (( h>=0 )&&(Objects.requireNonNull(TownyUniverse.getInstance().getResident(pl.getUniqueId())).getAccount().getHoldingBalance()  >= h)) {
                            townyflats.TFlats.get(town).get(former).BuyPlot(pl);
                            return;
                        }else {
                            pl.sendMessage(tapp+Townyflats.getLang().command_buy_false);
                            return;
                        }
                }

            }
        }}
        pl.sendMessage(tapp+Townyflats.getLang().command_buy_nothing);
    }
    private void Sell(Player pl,String price){
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            if (townyflats.PFlats.containsKey(pl.getName())) {
                for (Apartment ap:townyflats.PFlats.get(pl.getName())){
                        if(ap.testIfInApartment(pl.getLocation()) && ap.owner.equals(pl.getName())){
                            ap.RemoveHologram();
                            ap.price=Integer.parseInt(price);
                            ap.SetHologram(pl.getLocation());
                            pl.sendMessage(tapp+Townyflats.getLang().command_sell_true);
                            return;
                        }
                }

            }
        }
        pl.sendMessage(tapp+Townyflats.getLang().command_sell_nothing);
    }
    private void Delete(Player pl){
        Town town;
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(pl.getLocation())).getTownOrNull();
            if (townyflats.TFlats.containsKey(town)&&CityTestor(pl,town)) {
                    if (lengTh == 1) {
                            townyflats.TFlats.get(town).get(0).RemoveHologram();
                        assert town != null;
                        new File(townyflats.getDataFolder()+File.separator+"userdata"+File.separator+town.getName()+".yml").delete();
                        townyflats.TFlats.get(town).get(0).RemoveFromMaps(townyflats.TFlats.get(town).get(0).town);
                        pl.sendMessage(tapp + Townyflats.getLang().command_delete_true);

                    } else {
                        townyflats.TFlats.get(town).get(former).RemoveHologram();
                        townyflats.TFlats.get(town).get(former).DeleteFile(townyflats.TFlats.get(town).get(former).town);
                        townyflats.TFlats.get(town).get(former).RemoveFromMaps(townyflats.TFlats.get(town).get(former).town);
                                pl.sendMessage(tapp + Townyflats.getLang().command_delete_true);

                    }
                    return;
                }
            }
        pl.sendMessage(tapp+Townyflats.getLang().command_delete_nothing);
        }
    private void infoName(Player pl,Resident res,String ID){
         for(Apartment ap : townyflats.TFlats.get(res.getTownOrNull())){
             if(ap.ID.equals(ID)){
                 ap.InfoCommand(pl);
                 break;
             }
        }
    }
    private void info(Player pl) {
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            Town town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(pl.getLocation())).getTownOrNull();
            if (townyflats.TFlats.get(town)!=null) {
                if (CityTestor(pl,town)) {
                    townyflats.TFlats.get(town).get(former).InfoCommand(pl);
                    townyflats.TFlats.get(town).get(former).HologramInfo(pl);
                    return;
                }
            }
        }
        pl.sendMessage(tapp + Townyflats.getLang().command_info_nothing);
    }
 private boolean CityTestor(Player pl,Town town){
        lengTh = townyflats.TFlats.get(town).size();
        for (int i = 0; i < lengTh; i+=1) {
        if (townyflats.TFlats.get(town).get(i).testIfInApartment(pl.getLocation()))
        {
            former = i ;
            return true;
        }
        }
        return false;
        }

    private boolean ChunkTestor(Player pl,Town town) {
        if (townyflats.TFlats.containsKey(town) ){
        lengTh = townyflats.TFlats.get(town).size();
        for (int i = 0; i < lengTh; i += 1) {
            if (townyflats.TFlats.get(town).get(i).xC == townyflats.cache.get(pl.getName()).xC && townyflats.TFlats.get(town).get(i).zC == townyflats.cache.get(pl.getName()).zC) {
                if (i+1 == townyflats.flatlim) {
                    pl.sendMessage(tapp + Townyflats.getLang().command_limit_reached);
                    return false;
                }
            }
        }
    }
        return true;

    }
}
