package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.UUID;


public class BreakBlock implements Listener {
    public final Townyflats townyflats;

    public BreakBlock(Townyflats townyflats) {
        this.townyflats = townyflats;
    }

    private final String tapp = "§L§0[§4TAPP§L§0]§f";
    String b = "STICK";
    boolean f;
    ArrayList<UUID> haveCooldowns = new ArrayList<UUID>();
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
            if (town.hasResident(pl.getName())) {
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (!res.isMayor()) {
        if (townyflats.flats.containsKey(town)) {
            int lengTh = townyflats.flats.get(town).size();
            for (int i = 0; i < lengTh; i += 1) {
                if (townyflats.flats.get(town).get(i).testIfInApartment(selecedBl1.getLocation())||res.isMayor()) {
                    Resident ress = TownyUniverse.getInstance().getResident(Bukkit.getOfflinePlayer(UUID.fromString(townyflats.flats.get(town).get(i).owner)).getName());
                    if ((townyflats.flats.get(town).get(i).owner.equals(pl.getUniqueId().toString())) || (res.isMayor())||(ress.hasFriend(res))) {

                        event.setCancelled(false);
                    }
                    break;
                }


            }
        }
    }}}}
    //wand
    private void Privatev1(Player pl, Block selecedBl1, boolean testor, Location loc) {
        if (townyflats.cache.containsKey(pl.getUniqueId().toString())){
        if(!(townyflats.cache.get(pl.getUniqueId().toString()).xC == selecedBl1.getLocation().getChunk().getX() && townyflats.cache.get(pl.getUniqueId().toString()).zC == selecedBl1.getLocation().getChunk().getZ())) {
            townyflats.cache.remove(pl.getUniqueId().toString());
            pl.sendMessage(tapp + " Your new position is on the other chunk.All selected positions have been cleared");

        return;
        }}
            Townyflats.Apartment cacheL = new Townyflats.Apartment(0, 0, -100, -100, 0, 0,-100,-100, -1, pl.getUniqueId().toString());
            if (testor) {
                cacheL.setLocation1(loc);
                if (townyflats.cache.containsKey(pl.getUniqueId().toString())) {
                    cacheL.x2 = townyflats.cache.get(pl.getUniqueId().toString()).x2;
                    cacheL.y2 = townyflats.cache.get(pl.getUniqueId().toString()).y2;
                    cacheL.z2 = townyflats.cache.get(pl.getUniqueId().toString()).z2;
                }
                pl.sendMessage(tapp + " pos 1 (x: " + cacheL.x1 + "; y: " + cacheL.y1 + "; z: " + cacheL.z1 + ")");
            } else {

                cacheL.setLocation2(loc);
                if (townyflats.cache.containsKey(pl.getUniqueId().toString())) {
                    cacheL.x1 = townyflats.cache.get(pl.getUniqueId().toString()).x1;
                    cacheL.y1 = townyflats.cache.get(pl.getUniqueId().toString()).y1;
                    cacheL.z1 = townyflats.cache.get(pl.getUniqueId().toString()).z1;
                }
                pl.sendMessage(tapp + " pos 2 (x: " + cacheL.x2 + "; y: " + cacheL.y2 + "; z: " + cacheL.z2 + ")");
            }
        cacheL.zC = selecedBl1.getChunk().getZ();
        cacheL.xC = selecedBl1.getChunk().getX();
        townyflats.cache.put(pl.getUniqueId().toString(), cacheL);

            pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
        }






    @EventHandler
    public void PlayerInteration(PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        Block selecedBl1 = event.getClickedBlock();
        if (pl.getInventory().getItemInMainHand().getType().equals(Material.valueOf(b))) {
            if(event.getHand().equals(EquipmentSlot.HAND)){
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Town town;
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (pl.hasPermission("townyapartments.claim") || res.isMayor()) {
                    if (!TownyAPI.getInstance().isWilderness(selecedBl1.getLocation())) {

                        town = TownyAPI.getInstance().getTownBlock(selecedBl1.getLocation()).getTownOrNull();
                        if (town.hasResident(pl.getName())) {

                            if (haveCooldowns.contains(pl.getUniqueId())) {
                                pl.sendMessage(tapp +"Do not spam! Wait a bit");
                                event.setCancelled(true);
                                return;

                            }
                            haveCooldowns.add(pl.getUniqueId());
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(townyflats, new Runnable() {
                                public void run() {
                                    haveCooldowns.remove(pl.getUniqueId());
                                }
                            }, 40L);
                            f = true;
                            if (townyflats.flats.containsKey(town)) {
                                int lengTh = townyflats.flats.get(town).size();
                                for (int i = 0; i < lengTh; i += 1) {
                                    if (townyflats.flats.get(town).get(i).testIfInApartment(selecedBl1.getLocation())) {
                                        pl.sendMessage(tapp + "There is already an apartment here");
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
    public void playerLeave(PlayerQuitEvent event) {
        if (townyflats.cache.containsKey(event.getPlayer().getUniqueId().toString())) {
            townyflats.cache.remove(event.getPlayer().getUniqueId().toString());
            //townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }
    }
    //Deleting the info on town/chunk deletion;
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof ArmorStand) {
            Player pl = e.getPlayer();
           Town town = TownyAPI.getInstance().getTownBlock(e.getRightClicked().getLocation()).getTownOrNull();
            if (town.hasResident(pl.getName())&&townyflats.flats.containsKey(town)){
                for (Townyflats.Apartment ap: townyflats.flats.get(town) ){
                    for (ArmorStand ar:ap.entitys){
                        if (ar == e.getRightClicked()){
                            if (( ap.price>=0 )&&(townyflats.econ.getBalance(pl) >= ap.price)) {
                                f = false;
                                ap.BuyPlot(pl);
                            }
                                break;
                        }
                    }
                }


        }}
    }

    @EventHandler
    public void onUnclaimChunk(TownPreUnclaimEvent pos1) {
        Town town = pos1.getTown();
        int lengTh = townyflats.flats.get(town).size();
        for (int i = 0; i < lengTh; i += 1) {
            if ((pos1.getTownBlock().getX() == townyflats.flats.get(town).get(i).xC)
                    && (pos1.getTownBlock().getZ() == townyflats.flats.get(town).get(i).zC)) {
                    if (townyflats.flats.get(town).size() == 1){
                        townyflats.flats.get(town).get(0).RemoveHologram();
                        townyflats.flats.remove(town);
                    }
                    else {
                        townyflats.flats.get(town).get(i).RemoveHologram();
                        townyflats.flats.get(town).remove(i);
                    }

            }

        }

    }

    //public void onTownDelete(PreDeleteTownEvent pos1) {
    //    Town town = pos1.getTown();
    //    if (townyflats.flats.containsKey(town)){
    //    townyflats.flats.remove(town);
    //}
    //}

    //something important
}