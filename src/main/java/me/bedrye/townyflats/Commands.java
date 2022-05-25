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
                            Sell(pl, args[1]);
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
                int g = 0;
                if (townyflats.flats.containsKey(res.getTownOrNull())) {
                    g = townyflats.flats.get(res.getTownOrNull()).length;
                }
                Townyflats.Apartment[] changer = new Townyflats.Apartment[g + 1];
                g = changer.length;

                if (townyflats.flats.containsKey(res.getTownOrNull())) {
                    for (int i = 0; i < g-1; i++) {
                        changer[i] = townyflats.flats.get(res.getTownOrNull())[i];
                    }
                }
                    changer[g-1] = townyflats.cache.get(pl.getUniqueId().toString());
                townyflats.flats.put(res.getTownOrNull(), changer);
                pl.sendMessage(tapp+"You have claimed this property");
            }
                townyflats.cache.remove(pl.getUniqueId().toString());
            }

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
                        int h =townyflats.flats.get(town)[former].price;
                        if (( h!= -1 )&&(townyflats.econ.getBalance(pl) >= townyflats.flats.get(town)[former].price)) {
                            f = false;
                            townyflats.econ.withdrawPlayer(pl,townyflats.flats.get(town)[former].price );
                            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(townyflats.flats.get(town)[former].owner));
                            townyflats.econ.depositPlayer(owner,townyflats.flats.get(town)[former].price );
                            townyflats.flats.get(town)[former].owner = ""+pl.getUniqueId();
                            pl.sendMessage(tapp+"You have bought this property for "+townyflats.flats.get(town)[former].price + ".");
                            townyflats.flats.get(town)[former].price = -1;
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
                        if ((townyflats.flats.get(town)[former].owner.equals(pl.getUniqueId().toString())) || (res.isMayor())) {
                            f = false;
                                    pl.sendMessage( tapp +" You have just put up this property for sale");
                            townyflats.flats.get(town)[former].price = Integer.parseInt(price);
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
                        townyflats.flats.remove(town);
                        pl.sendMessage(tapp + "You have deleted this property");
                    } else {
                        Townyflats.Apartment[] copy = new Townyflats.Apartment[lengTh - 1];
                        int k=0;
                        for (int i = 0; i < lengTh; i++) {
                            if (i != former) {
                                copy[k] = townyflats.flats.get(town)[i];
                                k++;
                                pl.sendMessage(tapp+townyflats.flats.get(town)[i].x2);

                            } else {
                                pl.sendMessage(tapp + "You have deleted this property");
                            }


                        }
                        townyflats.flats.put(town,copy);
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
                    pl.sendMessage(tapp + "§2Pos 1: §f(x:" + townyflats.flats.get(town)[former].x1 + ";y:" + townyflats.flats.get(town)[former].y1 + ";z:" + townyflats.flats.get(town)[former].z1 + "); \n" +
                            "§2Pos 2: §f(x:" + townyflats.flats.get(town)[former].x1 + ";y:" + townyflats.flats.get(town)[former].y2 + ";z:" + townyflats.flats.get(town)[former].z2 + "); \n" +
                            "§2Owner: §f" + Bukkit.getOfflinePlayer(UUID.fromString(townyflats.flats.get(town)[former].owner)).getName() + "; \n" +
                            "§2For sale: §f" + townyflats.flats.get(town)[former].price + ";");
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
        lengTh = townyflats.flats.get(town).length;
        for (int i = 0; i < lengTh; i+=1) {
        if ((((pl.getLocation().getBlockX() >= townyflats.flats.get(town)[i].x1 && pl.getLocation().getBlockX() <= townyflats.flats.get(town)[i].x2)
        || (pl.getLocation().getBlockX() <= townyflats.flats.get(town)[i].x1 && pl.getLocation().getBlockX() >= townyflats.flats.get(town)[i].x2))
        && ((pl.getLocation().getBlockY() >= townyflats.flats.get(town)[i].y1 && pl.getLocation().getBlockY() <= townyflats.flats.get(town)[i].y2)
        || (pl.getLocation().getBlockY() <= townyflats.flats.get(town)[i].y1 && pl.getLocation().getBlockY() >= townyflats.flats.get(town)[i].y2))
        && ((pl.getLocation().getBlockZ() >= townyflats.flats.get(town)[i].z1 && pl.getLocation().getBlockZ() <= townyflats.flats.get(town)[i].z2)
        || (pl.getLocation().getBlockZ() <= townyflats.flats.get(town)[i].z1 && pl.getLocation().getBlockZ() >= townyflats.flats.get(town)[i].z2))
        )) {
            former = i ;
            return true;
        }
        }
        return false;
        }

    private boolean ChunkTestor(Player pl,Town town) {
        if (townyflats.flats.containsKey(town) ){
        lengTh = townyflats.flats.get(town).length;
        for (int i = 0; i < lengTh; i += 1) {
            if (townyflats.flats.get(town)[i].xC == townyflats.cache.get(pl.getUniqueId().toString()).xC && townyflats.flats.get(town)[i].zC == townyflats.cache.get(pl.getUniqueId().toString()).zC) {
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
