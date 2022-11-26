package me.bedrye.townyflats.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.bedrye.townyflats.util.Apartment;
import me.bedrye.townyflats.TownyFlats;
import me.bedrye.townyflats.util.LocatorLoc;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BreakBlockEvent implements Listener {
    public final TownyFlats townyFlats;

    public BreakBlockEvent(TownyFlats townyflats) {
        this.townyFlats = townyflats;
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
    private void PrivateCancelEvent(TownyActionEvent event ,Block selectedBl1) {
        Player pl = event.getPlayer();
        Town town;
        if (!TownyAPI.getInstance().isWilderness(selectedBl1.getLocation())) {
            town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(selectedBl1.getLocation())).getTownOrNull();
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
            assert res != null;
            if (!res.isMayor()) {
                    if (townyFlats.TFlats.containsKey(town)) {
                        for(Apartment ap : townyFlats.TFlats.get(town)) {
                            if (ap.testIfInApartment(selectedBl1.getLocation()) || res.isMayor()) {
                                Resident ress = TownyUniverse.getInstance().getResident(Objects.requireNonNull(Bukkit.getOfflinePlayer(ap.owner).getName()));
                                if ((ap.owner.equals(pl.getName())) || (Objects.requireNonNull(ress).hasFriend(res))||(ap.hasResident(pl.getName()))) {

                                    event.setCancelled(false);
                                }
                                break;
                            }

                        }

                    }
                }}}
    //wand
    private void Privatev1(Player pl, Block selecedBl1, boolean testor, Location loc) {
        if (townyFlats.cache.containsKey(pl.getName())){
        if(!(townyFlats.cache.get(pl.getName()).xC == selecedBl1.getLocation().getChunk().getX() && townyFlats.cache.get(pl.getName()).zC == selecedBl1.getLocation().getChunk().getZ())) {
            townyFlats.cache.remove(pl.getName());
            pl.sendMessage(tapp + TownyFlats.getLang().clear_pos);

        return;
        }}
            LocatorLoc cacheL = new LocatorLoc(0, 0, -100, -100, 0, 0,selecedBl1.getChunk().getX(),selecedBl1.getChunk().getZ(),selecedBl1.getWorld().getName());
            if (testor) {
                cacheL.setLocation1(loc);
                if (townyFlats.cache.containsKey(pl.getName())) {
                    cacheL.x2 = townyFlats.cache.get(pl.getName()).x2;
                    cacheL.y2 = townyFlats.cache.get(pl.getName()).y2;
                    cacheL.z2 = townyFlats.cache.get(pl.getName()).z2;
                }
                pl.sendMessage(tapp + " pos 1 (x: " + cacheL.x1 + "; y: " + cacheL.y1 + "; z: " + cacheL.z1 + ")");
            } else {

                cacheL.setLocation2(loc);
                if (townyFlats.cache.containsKey(pl.getName())) {
                    cacheL.x1 = townyFlats.cache.get(pl.getName()).x1;
                    cacheL.y1 = townyFlats.cache.get(pl.getName()).y1;
                    cacheL.z1 = townyFlats.cache.get(pl.getName()).z1;
                }
                pl.sendMessage(tapp + " pos 2 (x: " + cacheL.x2 + "; y: " + cacheL.y2 + "; z: " + cacheL.z2 + ")");
            }
        cacheL.zC = selecedBl1.getChunk().getZ();
        cacheL.xC = selecedBl1.getChunk().getX();
        townyFlats.cache.put(pl.getName(), cacheL);

            pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
        }






    @EventHandler
    public void PlayerInteraction(PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        Block selectedBl1 = event.getClickedBlock();
        if (pl.getInventory().getItemInMainHand().getType().equals(townyFlats.wand)) {
            if(Objects.equals(event.getHand(), EquipmentSlot.HAND)){
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Town town;
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (pl.hasPermission("townyapartments.claim") || Objects.requireNonNull(res).isMayor()) {
                    assert selectedBl1 != null;
                    if (!TownyAPI.getInstance().isWilderness(selectedBl1.getLocation())) {

                        town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(selectedBl1.getLocation())).getTownOrNull();
                        assert town != null;
                        if (town.hasResident(pl.getName())) {

                            if (townyFlats.haveCooldowns.contains(pl.getUniqueId())) {
                                pl.sendMessage(tapp + TownyFlats.getLang().command_antispam);
                                event.setCancelled(true);
                                return;

                            }
                            townyFlats.haveCooldowns.add(pl.getUniqueId());
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(townyFlats, new Runnable() {
                                public void run() {
                                    townyFlats.haveCooldowns.remove(pl.getUniqueId());
                                }
                            }, 20L);
                            f = true;
                            if (townyFlats.TFlats.containsKey(town)) {
                                int lengTh = townyFlats.TFlats.get(town).size();
                                for (int i = 0; i < lengTh; i += 1) {
                                    if (townyFlats.TFlats.get(town).get(i).testIfInApartment(selectedBl1.getLocation())) {
                                        pl.sendMessage(tapp + TownyFlats.getLang().already_apartment_here);
                                        f = false;
                                        break;
                                    }
                                }
                            }
                            if (f) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    Privatev1(pl, selectedBl1, false, selectedBl1.getLocation());
                                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    Privatev1(pl, selectedBl1, true, selectedBl1.getLocation());

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
        if (townyFlats.PFlats.containsKey(event.getPlayer())){
            for (Apartment ap : townyFlats.PFlats.get(event.getPlayer())) {
                if (ap.owner.equals(event.getPlayer())) {
                    ap.ChangePlayer(ap.town.getMayor().getPlayer());
                }
            }
        }

    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (townyFlats.cache.containsKey(event.getPlayer().getName())) {
            townyFlats.cache.remove(event.getPlayer().getName());
            //townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }
    }
    //Deleting the info on town/chunk deletion;
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof ArmorStand) {
            Player pl = e.getPlayer();
            Chunk ch = e.getRightClicked().getLocation().getChunk();
           Town town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(e.getRightClicked().getLocation())).getTownOrNull();
            assert town != null;
            if ((town.hasResident(pl.getName())|| Objects.requireNonNull(TownyUniverse.getInstance().getTownBlockOrNull(new WorldCoord(ch.getWorld().getName(), ch.getX(), ch.getZ()))).getType().equals(TownBlockType.EMBASSY))&& townyFlats.TFlats.containsKey(town)){
                for (Apartment ap: townyFlats.TFlats.get(town) ){
                    for (ArmorStand ar:ap.entitys){
                        if (ar == e.getRightClicked()){
                            if (( ap.price>=0 )&&(Objects.requireNonNull(TownyUniverse.getInstance().getResident(pl.getUniqueId())).getAccount().getHoldingBalance() >= ap.price)) {
                                f = false;
                                ap.BuyPlot(pl);
                            }else {pl.sendMessage(tapp+ TownyFlats.getLang().command_sell_false);}
                                return;
                        }
                    }
                }


        }}
    }

    @EventHandler
    public void onUnclaimChunk(TownPreUnclaimEvent pos1) {
        Town town = pos1.getTown();
        Chunk ch = Objects.requireNonNull(Bukkit.getWorld(pos1.getTownBlock().getWorld().getName())).getChunkAt(pos1.getTownBlock().getX(),pos1.getTownBlock().getZ());
        Bukkit.getConsoleSender().sendMessage("Delete");
        List<Apartment> l = new ArrayList<>();
        for (Apartment ap: townyFlats.CFlats.get(ch.getX()+""+ch.getZ()+ch.getWorld().getName())) {
            Bukkit.getConsoleSender().sendMessage(ap.ID);
                l.add(ap);


        }
        for (Apartment ap: l ) {
            ap.RemoveHologram();
            assert town != null;
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