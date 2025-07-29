package me.bedrye.townyflats.commands;

import com.palmergames.bukkit.towny.TownyUniverse;
import me.bedrye.townyflats.util.Apartment;
import me.bedrye.townyflats.TownyFlats;
import me.bedrye.townyflats.util.FlatManager;
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


import java.util.Objects;

public class MainCommands implements CommandExecutor {
    public final TownyFlats townyflats;
    private final String tapp ="§l§0[§4TAPP§l§0]§f";
    public MainCommands(TownyFlats townyflats){
        this.townyflats = townyflats;
}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tapp")) {
            if (sender instanceof Player) {
                Player pl = (Player) sender;
                if (FlatManager.Instance().hasPlayerCoolDown(pl.getUniqueId())) {
                    pl.sendMessage(tapp + TownyFlats.getLang().command_antispam);
                    return true;

                }
                FlatManager.Instance().addPlayerCoolDown(pl.getUniqueId());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(townyflats, new Runnable() {
                    public void run() {
                        FlatManager.Instance().removePlayerCoolDown(pl.getUniqueId());
                    }
                }, 20L);
                Resident res = TownyUniverse.getInstance().getResident(pl.getUniqueId());
                assert res != null;
                if (args.length>0){
                switch (args[0]) {

                    case "claim":
                        if (args.length==2) {
                            if (pl.hasPermission("townyapartments.player.claim")|| Objects.requireNonNull(res).isMayor()) {
                                Claim(res, args[1]);
                            }
                        }
                        else {

                            pl.sendMessage(tapp + "/tapp claim NAME");
                        }
                        break;
                    case "delete":
                        if (pl.hasPermission("townyapartments.player.delete")|| Objects.requireNonNull(res).isMayor()) {
                            Delete(pl);
                        }
                        break;
                    case "buy":
                        Buy(pl);
                        break;
                    case "sell":
                        if (args.length==2) {
                            if (Integer.parseInt(args[1])>=0) {
                                Sell(res, args[1]);
                            }else {pl.sendMessage(tapp +  TownyFlats.getLang().command_sell_false);}
                        } else {
                            pl.sendMessage(tapp + "/tapp sell COST");
                        }
                        break;
                    case "info":
                        if(args.length>=2){
                            infoName(res,args[1]);
                        }
                        else {
                            info(pl);
                        }

                        break;
                    case "list":
                        if (args.length>=2) {
                            switch (args[1]) {
                                case "town":
                                    if (args.length == 2) {

                                        listTown(pl, 1, res.getTownOrNull());
                                    } else if (args.length == 3) {
                                        try {
                                            Integer.parseInt(args[2]);
                                            listTown(pl, Integer.parseInt(args[2]), res.getTownOrNull());
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    } else {
                                        try {
                                            Integer.parseInt(args[2]);
                                            listTown(pl, Integer.parseInt(args[2]), TownyUniverse.getInstance().getTown(args[3]));
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    }

                                    break;
                                case "chunk":
                                    if (args.length == 2) {
                                        listChunk(pl, 1);
                                    } else if (args.length == 3) {
                                        try {
                                            Integer.parseInt(args[2]);
                                            listChunk(pl, Integer.parseInt(args[2]));
                                        } catch (Exception e) {
                                            pl.sendMessage(tapp + " " + args[2] + " is not a valid integer");
                                        }
                                    }

                                    break;
                            }
                        }
                            break;

                    case "reload":
                        if (pl.hasPermission("townyapartments.admin.reload")){
                            pl.sendMessage("Reloading...");
                            FlatManager.Instance().save();
                            townyflats.restart();
                            townyflats.load();
                            pl.sendMessage("Done!");
                    }
                        break;
                    case "add":
                        if (args.length==2) {
                            AddResident(res, args[1]);
                        } else {pl.sendMessage(tapp + "/tapp add PLAYER");}
                        break;
                    case "remove":
                        if (args.length==2) {
                            RemoveResident(res, args[1]);
                        }else {pl.sendMessage(tapp + "/tapp remove PLAYER");}
                        break;
                    default:
                        pl.sendMessage(tapp + TownyFlats.getLang().incorrect_command);
                        break;

                }
    }else{
                    pl.sendMessage(tapp + "§2/tapp claim NAME§f - "+TownyFlats.getLang().help_claim+"\n " +
                            "§2/tapp delete§f - "+TownyFlats.getLang().help_delete+"\n " +
                            "§2/tapp sell COST§f - "+TownyFlats.getLang().help_sell+"\n " +
                            "§2/tapp buy§f - "+TownyFlats.getLang().help_buy+"\n "  +
                            "§2/tapp info§f - "+ TownyFlats.getLang().help_info+"\n " +
                            "§2/tapp list§f - "+TownyFlats.getLang().help_list+"\n " +
                            "§2/tapp add PLAYER§f - "+TownyFlats.getLang().help_player_add +"\n " +
                            "§2/tapp remove PLAYER§f - "+TownyFlats.getLang().help_player_remove +"\n " +
                            "§2/tapp reload§f - "+TownyFlats.getLang().help_reload);
                }
            }
        }
        return true;
    }
    private void AddResident(Resident pl,String arg){
        Apartment apartment =  FlatManager.Instance().GetApartmentOrNull(pl.getPlayer());
        if (apartment!=null) {
            if (apartment.IsAllowedToAdminister(pl)) {
                Resident resident = TownyUniverse.getInstance().getResident(arg);
                if (Bukkit.getPlayerExact(arg)!=null) {
                    apartment.addResident(resident);
                    pl.getPlayer().sendMessage(tapp+"You have added " +arg+" to this property ");
                }
            }
        }

    }
    private void RemoveResident(Resident pl,String arg){
        Apartment apartment =  FlatManager.Instance().GetApartmentOrNull(pl.getPlayer());
            if (apartment!=null) {
                if (apartment.IsAllowedToAdminister(pl)) {
                    Resident resident = TownyUniverse.getInstance().getResident(arg);
                    apartment.removeResident(resident);
                    pl.getPlayer().sendMessage(tapp+"You have removed " +arg+" from this property ");
                }
            }

    }

    private void listTown(Player pl,int num,Town t) {
        pl.sendMessage(tapp + TownyFlats.getLang().command_list);
        pl.sendMessage("");
        int i = 0;
        for (Apartment ap:FlatManager.Instance().getApartments(t)) {

                if (i >= (num * 5) - 5) {
                    if (i < num * 5) {
                        TextComponent message = new TextComponent((i + 1) + "." + ap.getName());
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp info " + ap.getName()));
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

    private void listChunk(Player pl,int num) {
        pl.sendMessage(tapp + TownyFlats.getLang().command_list);
        pl.sendMessage("");
        int i=0;
        for (Apartment ap:FlatManager.Instance().getApartments(pl.getLocation().getChunk())) {
            if (i >= (num * 5) - 5) {
                if (i < num * 5) {
                        TextComponent message = new TextComponent((i + 1) + "." + ap.getName());
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tapp info " + ap.getID()));
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

    private void Claim(Resident res,String id){
        if (FlatManager.Instance().hasTemporary(res.getPlayer().getName())) {
            if (FlatManager.Instance().getTemporary(res.getPlayer().getName()).hasTwoLocations()) {
                if (ChunkLimitTester(res.getPlayer())){
                    Apartment loc =new Apartment(FlatManager.Instance().getTemporary(res.getPlayer().getName()),-1,res,id,res.getTownOrNull());
                    loc.WriteToMaps();
                    res.getPlayer().sendMessage(tapp+ TownyFlats.getLang().command_claim_true);
                    loc.SaveFile();

                }
            }
            FlatManager.Instance().removeTemporary(res.getPlayer().getName());
            }

        }

    private void Buy(Player pl){
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            Apartment apartment = FlatManager.Instance().GetApartmentOrNull(pl);
            if (apartment!=null) {
                Resident resident = TownyUniverse.getInstance().getResident(pl.getName());
                if(!apartment.BuyPlot(resident)){
                    pl.sendMessage(tapp+ TownyFlats.getLang().command_buy_false);
                }
                return;
            }
        }
        pl.sendMessage(tapp+ TownyFlats.getLang().command_buy_nothing);
    }
    private void Sell(Resident pl,String price){
        if (!TownyAPI.getInstance().isWilderness(pl.getPlayer().getLocation())) {
            Apartment apartment =  FlatManager.Instance().GetApartmentOrNull(pl.getPlayer());
                if (apartment!=null) {
                   if(apartment.IsAllowedToAdminister(pl)){
                    apartment.SetForSale(Integer.parseInt(price),pl.getPlayer().getLocation());
                    pl.getPlayer().sendMessage(tapp+ TownyFlats.getLang().command_sell_true);
                    return;
                   }
                }

            }

        pl.getPlayer().sendMessage(tapp+ TownyFlats.getLang().command_sell_nothing);
    }
    private void Delete(Player pl){
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            Apartment apartment =  FlatManager.Instance().GetApartmentOrNull(pl.getLocation());
            if (apartment!=null) {
                if(apartment.getTown().hasResident(pl)) {
                    apartment.RemoveHologram();
                    apartment.RemoveFromMaps();
                    apartment.DeleteFile();
                    pl.sendMessage(tapp + TownyFlats.getLang().command_delete_true);
                }
                return;
                }
            }
        pl.sendMessage(tapp+ TownyFlats.getLang().command_delete_nothing);
        }
    private void infoName(Resident res,String ID){
         for(Apartment ap : FlatManager.Instance().getApartments(res.getTownOrNull())){
             if(ap.getID().equals(ID)){
                 ap.InfoCommand(res.getPlayer());
                 break;
             }
        }
    }
    private void info(Player pl) {
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
                Apartment apartment =  FlatManager.Instance().GetApartmentOrNull(pl);
                if (apartment!=null) {
                    apartment.InfoCommand(pl);
                    apartment.HologramInfo(pl);
                    return;
                }
            }

        pl.sendMessage(tapp + TownyFlats.getLang().command_info_nothing);
    }


    private boolean ChunkLimitTester(Player pl) {
        Bukkit.getConsoleSender().sendMessage(pl.toString());
        if (FlatManager.Instance().getApartments(pl.getLocation().getChunk()).size() == townyflats.flatlim) {
            pl.sendMessage(tapp + TownyFlats.getLang().command_limit_reached);
            return false;
        }
        return true;
    }
}
