package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;

import java.util.ArrayList;
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
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("tapp")) {

            if (sender instanceof Player) {
                Player pl = (Player) sender;
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (args.length>0){
                switch (args[0]) {

                    case "claim":
                        if (pl.hasPermission("townyapartments.claim")&& res.isMayor()) {
                            Claim(pl,res);
                        }
                        break;
                    case "delete":
                        if (pl.hasPermission("townyapartments.delete")&& res.isMayor()) {
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
                            }else {pl.sendMessage(tapp + "Write a valid cost");}
                        } else {
                            pl.sendMessage(tapp + "/tapp sell COST");
                        }
                        break;
                    case "info":
                        info(pl);
                        break;
                    default:
                        pl.sendMessage(tapp + "/tapp");
                        break;

                }
    }else{
                    pl.sendMessage(tapp + "§2/tapp claim§f - claims plot if there are 2 positions set;\n " +
                            "§2/tapp delete§f - deletes the plot on which you are currently standing;\n " +
                            "§2/tapp sell§f  - put the plot for sale;\n" +
                            "§2/tapp buy§f - buys the plot on which you are currently standing; \n" +
                            "§2/tapp info§f - see the info of the plot on which you are currently standing;\n");
                }
            }
        }
        return true;
    }
    private void Claim(Player pl,Resident res){
        if (townyflats.cache.containsKey(pl.getUniqueId().toString())) {
            if (townyflats.cache.get(pl.getUniqueId().toString()).y1 !=-100 && townyflats.cache.get(pl.getUniqueId().toString()).y2!=-100) {
                if (ChunkTestor(pl,res.getTownOrNull())){
                    if (!townyflats.flats.containsKey(res.getTownOrNull())) {
                        townyflats.flats.put(res.getTownOrNull(),new ArrayList<>());
                    }
                    townyflats.flats.get(res.getTownOrNull()).add(townyflats.cache.get(pl.getUniqueId().toString()));
                    pl.sendMessage(tapp+"You have claimed this property");
            }}
                townyflats.cache.remove(pl.getUniqueId().toString());
            }

        }

    private void Buy(Player pl){
        Town town;
        boolean f;
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            town = TownyAPI.getInstance().getTownBlock(pl.getLocation()).getTownOrNull();
            if (town.hasResident(pl.getName())){
            if (townyflats.flats.containsKey(town)) {
                f = true;
                    if (CityTestor(pl,town)) {
                        int h =townyflats.flats.get(town).get(former).price;
                        if (( h>=0 )&&(townyflats.econ.getBalance(pl) >= townyflats.flats.get(town).get(former).price)) {
                            f = false;
                            townyflats.flats.get(town).get(former).BuyPlot(pl);
                        }


                }
                if (f) {
                    pl.sendMessage(tapp+"Nothing to buy");
                }
            }
        }}
    }
    private void Sell(Player pl,String price){
        Town town;
        boolean f;
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            town = TownyAPI.getInstance().getTownBlock(pl.getLocation()).getTownOrNull();
            if (townyflats.flats.containsKey(town)) {
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                f = true;
                if(CityTestor(pl,town)){
                        if ((townyflats.flats.get(town).get(former).owner.equals(pl.getUniqueId().toString())) || (res.isMayor())) {
                            f = false;
                                    pl.sendMessage( tapp +" You have just put up this property for sale");
                            townyflats.flats.get(town).get(former).price = Integer.parseInt(price);
                            if ( townyflats.flats.get(town).get(former).yA!=-100) {
                                townyflats.flats.get(town).get(former).RemoveHologram();
                            }
                            townyflats.flats.get(town).get(former).SetHologram(pl.getLocation());
                        }
                }
                if (f) {
                    pl.sendMessage(tapp+"Nothing to sell");
                }
            }
        }
    }
    private void Delete(Player pl){
        Town town;
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            town = TownyAPI.getInstance().getTownBlock(pl.getLocation()).getTownOrNull();
            if (townyflats.flats.containsKey(town)&&CityTestor(pl,town)) {
                    if (lengTh == 1) {

                            townyflats.flats.get(town).get(0).RemoveHologram();

                        townyflats.flats.remove(town);
                        pl.sendMessage(tapp + "You have deleted this property");

                    } else {
                        townyflats.flats.get(town).get(former).RemoveHologram();

                        townyflats.flats.get(town).remove(former);
                                pl.sendMessage(tapp + "You have deleted this property");

                    }

                }
            }
        else {pl.sendMessage(tapp+"Nothing to delete");}
        }

    private void info(Player pl) {
        if (!TownyAPI.getInstance().isWilderness(pl.getLocation())) {
            Town town = TownyAPI.getInstance().getTownBlock(pl.getLocation()).getTownOrNull();
            if (townyflats.flats.get(town)!=null) {
                if (CityTestor(pl,town)) {
                    pl.sendMessage(tapp + "§2Pos 1: §f(x:" + townyflats.flats.get(town).get(former).x1 + ";y:" + townyflats.flats.get(town).get(former).y1 + ";z:" + townyflats.flats.get(town).get(former).z1 + "); \n" +
                            "§2Pos 2: §f(x:" + townyflats.flats.get(town).get(former).x1 + ";y:" + townyflats.flats.get(town).get(former).y2 + ";z:" + townyflats.flats.get(town).get(former).z2 + "); \n" +
                            "§2Owner: §f" + Bukkit.getOfflinePlayer(UUID.fromString(townyflats.flats.get(town).get(former).owner)).getName() + "; \n" +
                            "§2For sale: §f" + townyflats.flats.get(town).get(former).price + ";");

                } else if(former>=lengTh-1) {
                    pl.sendMessage(tapp + "No property was found");
                }
            }
        }
        else {
            pl.sendMessage(tapp + "No property was found");
        }
    }
 private boolean CityTestor(Player pl,Town town){
        lengTh = townyflats.flats.get(town).size();
        for (int i = 0; i < lengTh; i+=1) {
        if (townyflats.flats.get(town).get(i).testIfInApartment(pl.getLocation()))
        {
            former = i ;
            return true;
        }
        }
        return false;
        }

    private boolean ChunkTestor(Player pl,Town town) {
        if (townyflats.flats.containsKey(town) ){
        lengTh = townyflats.flats.get(town).size();
        for (int i = 0; i < lengTh; i += 1) {
            if (townyflats.flats.get(town).get(i).xC == townyflats.cache.get(pl.getUniqueId().toString()).xC && townyflats.flats.get(town).get(i).zC == townyflats.cache.get(pl.getUniqueId().toString()).zC) {
                if (i+1 == townyflats.flatlim) {
                    pl.sendMessage(tapp + "You have reached the limit for this chunk");
                    return false;
                }
            }
        }
    }
        return true;

    }
}
