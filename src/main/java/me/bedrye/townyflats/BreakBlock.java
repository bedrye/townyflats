package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Bukkit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BreakBlock implements Listener {
    public final Townyflats townyflats;

    public BreakBlock(Townyflats townyflats) {
        this.townyflats = townyflats;
    }

    private final String tapp = "§L§0[§4TAPP§L§0]§f";
    boolean f;

    //Block break/placement/usage events

    @EventHandler
    public void onPlayerBrBlock(TownyBuildEvent event) {
        PrivateCancelEvent(event, event.getBlock());
    }
    @EventHandler
    public void onPlayerUseBlock(TownySwitchEvent event) {
        PrivateCancelEvent(event, event.getBlock());
    }
    @EventHandler
    public void onPlayerBreakBlock(TownyDestroyEvent event) {
        PrivateCancelEvent(event, event.getBlock());
    }
    private void PrivateCancelEvent(TownyActionEvent event ,Block selecedBl1) {
        Player pl = event.getPlayer();
        Town town;
        if (!TownyAPI.getInstance().isWilderness(selecedBl1.getLocation())) {
            town = TownyAPI.getInstance().getTownBlock(selecedBl1.getLocation()).getTownOrNull();
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (!res.isMayor()) {
                    if (townyflats.TFlats.containsKey(town)) {
                        for(Apartment ap :townyflats.TFlats.get(town)) {
                            if (ap.testIfInApartment(selecedBl1.getLocation()) || res.isMayor()) {
                                Resident ress = TownyUniverse.getInstance().getResident(Bukkit.getOfflinePlayer(ap.owner).getName());
                                if ((ap.owner.equals(pl.getName())) || (ress.hasFriend(res))||(ap.hasResident(pl.getName()))) {

                                    event.setCancelled(false);
                                }
                                break;
                            }

                        }

                    }
                }}}
    //wand
    private void Privatev1(Player pl, Block selecedBl1, boolean testor, Location loc) {
        if (townyflats.cache.containsKey(pl.getName())){
        if(!(townyflats.cache.get(pl.getName()).xC == selecedBl1.getLocation().getChunk().getX() && townyflats.cache.get(pl.getName()).zC == selecedBl1.getLocation().getChunk().getZ())) {
            townyflats.cache.remove(pl.getName());
            pl.sendMessage(tapp + townyflats.clear_pos);

        return;
        }}
            LocatorLoc cacheL = new LocatorLoc(0, 0, -100, -100, 0, 0,selecedBl1.getChunk().getX(),selecedBl1.getChunk().getZ(),selecedBl1.getWorld().getName());
            if (testor) {
                cacheL.setLocation1(loc);
                if (townyflats.cache.containsKey(pl.getName())) {
                    cacheL.x2 = townyflats.cache.get(pl.getName()).x2;
                    cacheL.y2 = townyflats.cache.get(pl.getName()).y2;
                    cacheL.z2 = townyflats.cache.get(pl.getName()).z2;
                }
                pl.sendMessage(tapp + " pos 1 (x: " + cacheL.x1 + "; y: " + cacheL.y1 + "; z: " + cacheL.z1 + ")");
            } else {

                cacheL.setLocation2(loc);
                if (townyflats.cache.containsKey(pl.getName())) {
                    cacheL.x1 = townyflats.cache.get(pl.getName()).x1;
                    cacheL.y1 = townyflats.cache.get(pl.getName()).y1;
                    cacheL.z1 = townyflats.cache.get(pl.getName()).z1;
                }
                pl.sendMessage(tapp + " pos 2 (x: " + cacheL.x2 + "; y: " + cacheL.y2 + "; z: " + cacheL.z2 + ")");
            }
        cacheL.zC = selecedBl1.getChunk().getZ();
        cacheL.xC = selecedBl1.getChunk().getX();
        townyflats.cache.put(pl.getName(), cacheL);

            pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
        }






    @EventHandler
    public void PlayerInteration(PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        Block selecedBl1 = event.getClickedBlock();
        if (pl.getInventory().getItemInMainHand().getType().equals(townyflats.wand)) {
            if(event.getHand().equals(EquipmentSlot.HAND)){
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Town town;
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (pl.hasPermission("townyapartments.claim") || res.isMayor()) {
                    if (!TownyAPI.getInstance().isWilderness(selecedBl1.getLocation())) {

                        town = TownyAPI.getInstance().getTownBlock(selecedBl1.getLocation()).getTownOrNull();
                        if (town.hasResident(pl.getName())) {

                            if (townyflats.haveCooldowns.contains(pl.getUniqueId())) {
                                pl.sendMessage(tapp +townyflats.command_antispam);
                                event.setCancelled(true);
                                return;

                            }
                            townyflats.haveCooldowns.add(pl.getUniqueId());
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(townyflats, new Runnable() {
                                public void run() {
                                    townyflats.haveCooldowns.remove(pl.getUniqueId());
                                }
                            }, 20L);
                            f = true;
                            if (townyflats.TFlats.containsKey(town)) {
                                int lengTh = townyflats.TFlats.get(town).size();
                                for (int i = 0; i < lengTh; i += 1) {
                                    if (townyflats.TFlats.get(town).get(i).testIfInApartment(selecedBl1.getLocation())) {
                                        pl.sendMessage(tapp + townyflats.already_apartment_here);
                                        f = false;
                                        break;
                                    }
                                }
                            }
                            if (f) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    Privatev1(pl, selecedBl1, false, selecedBl1.getLocation());
                                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    Privatev1(pl, selecedBl1, true, selecedBl1.getLocation());

                                }

                            }

                        }
                    }
                }
                event.setCancelled(true);
            }
            }
        }
    }
    @EventHandler
    public void plTownLeave(PlayerLeaveTownEvent event){
        if (townyflats.PFlats.containsKey(event.getPlayer())){
            for (Apartment ap :townyflats.PFlats.get(event.getPlayer())) {
                if (ap.owner.equals(event.getPlayer())) {
                    ap.ChangePlayer(ap.town.getMayor().getPlayer());
                }
            }
        }

    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (townyflats.cache.containsKey(event.getPlayer().getName())) {
            townyflats.cache.remove(event.getPlayer().getName());
            //townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }
    }
    //Deleting the info on town/chunk deletion;
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof ArmorStand) {
            Player pl = e.getPlayer();
            Chunk ch = e.getRightClicked().getLocation().getChunk();
           Town town = TownyAPI.getInstance().getTownBlock(e.getRightClicked().getLocation()).getTownOrNull();
            if ((town.hasResident(pl.getName())||TownyUniverse.getInstance().getTownBlockOrNull(new WorldCoord( ch.getWorld().getName(), ch.getX(), ch.getZ())).getType().equals(TownBlockType.EMBASSY))&&townyflats.TFlats.containsKey(town)){
                for (Apartment ap: townyflats.TFlats.get(town) ){
                    for (ArmorStand ar:ap.entitys){
                        if (ar == e.getRightClicked()){
                            if (( ap.price>=0 )&&(TownyUniverse.getInstance().getResident(pl.getUniqueId()).getAccount().getHoldingBalance() >= ap.price)) {
                                f = false;
                                ap.BuyPlot(pl);
                            }else {pl.sendMessage(tapp+townyflats.command_sell_false);}
                                return;
                        }
                    }
                }


        }}
    }

    @EventHandler
    public void onUnclaimChunk(TownPreUnclaimEvent pos1) {
        Town town = pos1.getTown();
        Chunk ch = Bukkit.getWorld(pos1.getTownBlock().getWorld().getName()).getChunkAt(pos1.getTownBlock().getX(),pos1.getTownBlock().getZ());
        Bukkit.getConsoleSender().sendMessage("Delete");
        List<Apartment> l = new ArrayList<>();
        for (Apartment ap:townyflats.CFlats.get(ch.getX()+""+ch.getZ()+ch.getWorld().getName())) {
            Bukkit.getConsoleSender().sendMessage(ap.ID);
                l.add(ap);


        }
        for (Apartment ap: l ) {
            ap.RemoveHologram();
            ap.DeleteFile(town);
            ap.RemoveFromMaps(town);
        }
    }
    /*@EventHandler
    public void onTownDelete(PreDeleteTownEvent pos1) {
        Town town = pos1.getTown();
       List<Apartment> l = new ArrayList<>();
       for (Apartment ap:townyflats.TFlats.get(town)) {
           Bukkit.getConsoleSender().sendMessage(ap.ID);
           l.add(ap);
       }
       for (Apartment ap: l ) {
           ap.RemoveHologram();
           ap.DeleteFile();
           ap.RemoveFromMaps();
       }
    }*/

    //something important
}