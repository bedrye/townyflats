package me.bedrye.townyflats.events;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import me.bedrye.townyflats.util.Apartment;
import me.bedrye.townyflats.TownyFlats;
import me.bedrye.townyflats.util.FlatManager;
import org.bukkit.Bukkit;



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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FlatsEventManager implements Listener {




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
    private void PrivateCancelEvent(TownyActionEvent event ,Block block) {
        Player pl = event.getPlayer();
        if (!TownyAPI.getInstance().isWilderness(block.getLocation())) {
            Resident res = TownyUniverse.getInstance().getResident(pl.getName());
            Apartment apartment = FlatManager.GetApartmentOrNull(block.getLocation());
            if (apartment != null) {
                if (apartment.IsAllowedToUse(res)) {
                    event.setCancelled(false);
                }
            }

        }
    }
    @EventHandler
    public void PlayerInteraction(PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        Block selectedBl1 = event.getClickedBlock();
        if(WandCheck(pl.getInventory().getItemInMainHand(),event.getHand())){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Town town;
                Resident res = TownyUniverse.getInstance().getResident(pl.getName());
                if (pl.hasPermission("townyapartments.player.claim") || Objects.requireNonNull(res).isMayor()) {
                    assert selectedBl1 != null;
                    if (!TownyAPI.getInstance().isWilderness(selectedBl1.getLocation())) {
                        town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(selectedBl1.getLocation())).getTownOrNull();
                        assert town != null;
                        if (town.hasResident(res)) {
                            if (FlatManager.hasPlayerCoolDown(pl.getUniqueId())) {
                                pl.sendMessage(TownyFlats.getLang().tapp + TownyFlats.getLang().command_antispam);
                                event.setCancelled(true);
                                return;

                            }
                            FlatManager.addPlayerCoolDown(pl.getUniqueId());
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TownyFlats.getProvidingPlugin(TownyFlats.class),
                            new Runnable() {
                                public void run() {
                                    FlatManager.removePlayerCoolDown(pl.getUniqueId());
                                }
                            }, 20L);
                            Apartment apartment = FlatManager.GetApartmentOrNull(selectedBl1.getLocation());
                            if (apartment!=null){
                                pl.sendMessage(TownyFlats.getLang().tapp + TownyFlats.getLang().already_apartment_here);
                                }
                            else {
                                FlatManager.ClaimApartment(pl, selectedBl1, event.getAction() != Action.RIGHT_CLICK_BLOCK, selectedBl1.getLocation());
                                }
                            }
                        }
                    }
                }
                event.setCancelled(true);

        }
    }

    @EventHandler
    public void plTownLeave(TownLeaveEvent event){
        if (FlatManager.hasTemporary(event.getResident().getPlayer().getName())) {
            FlatManager.removeTemporary(event.getResident().getPlayer().getName());
            //townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }

    }
    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        if (FlatManager.hasTemporary(event.getPlayer().getName())) {
            FlatManager.removeTemporary(event.getPlayer().getName());
            //townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }
    }
    @EventHandler
    public void onHologramInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof ArmorStand) {
            Player pl = e.getPlayer();
            Town town = Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(e.getRightClicked().getLocation())).getTownOrNull();
            assert town != null:"Town is null!";
                for (Apartment ap: FlatManager.getApartments(town) ){
                    ap.BuyWithHologram((ArmorStand) e.getRightClicked(),TownyUniverse.getInstance().getResident(pl.getUniqueId()));
                    }
        }
    }

    @EventHandler
    public void onUnclaimChunk(TownPreUnclaimEvent pos1) {
        Bukkit.getConsoleSender().sendMessage("Delete");
        List<Apartment> l = new ArrayList<>(FlatManager.getApartments(pos1.getTownBlock()));
        for (Apartment ap: l ) {
            ap.RemoveHologram();
            ap.DeleteFile();
            ap.RemoveFromMaps();
        }
    }
    @EventHandler
    public void onTownCreate(NewTownEvent event) throws TownyException {
        FlatManager.putNew(event.getTown());
        FlatManager.putNew( event.getTown().getHomeBlock());
    }

    @EventHandler
    public void onClaimCreate(TownClaimEvent event){
        Bukkit.getConsoleSender().sendMessage(event.getTownBlock().toString());
        FlatManager.putNew( event.getTownBlock());



    }
    private boolean WandCheck(ItemStack item, EquipmentSlot eq){
        return item.getType().equals(TownyFlats.wand)&& Objects.equals(eq, EquipmentSlot.HAND);

    }
}