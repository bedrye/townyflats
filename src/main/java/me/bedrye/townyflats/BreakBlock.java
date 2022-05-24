package me.bedrye.townyflats;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.actions.*;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.event.block.Action;
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
    int x1, y1, z1;
    boolean f;
    String[] cacheL = {"0", "-100", "0", "0", "-100", "0", "-100", "-100", "che"};
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

            int lengTh = townyflats.flats.get(town).length;
            x1 = selecedBl1.getLocation().getBlockX();
            y1 = selecedBl1.getLocation().getBlockY();
            z1 = selecedBl1.getLocation().getBlockZ();
            for (int i = 0; i < lengTh; i += 1) {
                if (CheckIfBlInCity(i, town, res)||res.isMayor()) {
                    Resident ress = TownyUniverse.getInstance().getResident(Bukkit.getOfflinePlayer(UUID.fromString(townyflats.flats.get(town)[i].owner)).getName());
                    if ((townyflats.flats.get(town)[i].owner.equals(pl.getUniqueId().toString())) || (res.isMayor())||(ress.hasFriend(res))) {

                        event.setCancelled(false);
                    }
                    break;
                }


            }
        }
    }}}}
    //wand
    private void Privatev1(Player pl, Block selecedBl1, boolean testor) {
        if (townyflats.cacheCh.get(pl.getUniqueId().toString()) == selecedBl1.getLocation().getChunk() || townyflats.cacheCh.get(pl.getUniqueId().toString()) == null) {
            townyflats.cacheCh.put(pl.getUniqueId().toString(), selecedBl1.getLocation().getChunk());

            if (testor) {
                cacheL[0] = "" + x1;
                cacheL[1] = "" + y1;
                cacheL[2] = "" + z1;
                if (townyflats.cache.containsKey(pl.getUniqueId().toString())) {
                    cacheL[3] = townyflats.cache.get(pl.getUniqueId().toString())[3];
                    cacheL[4] = townyflats.cache.get(pl.getUniqueId().toString())[4];
                    cacheL[5] = townyflats.cache.get(pl.getUniqueId().toString())[5];
                }
                pl.sendMessage(tapp + " pos 1(x: " + x1 + "y: " + y1 + "z: " + z1 + ")");
            } else {

                cacheL[3] = "" + x1;
                cacheL[4] = "" + y1;
                cacheL[5] = "" + z1;
                if (townyflats.cache.containsKey(pl.getUniqueId().toString())) {
                    cacheL[0] = townyflats.cache.get(pl.getUniqueId().toString())[0];
                    cacheL[1] = townyflats.cache.get(pl.getUniqueId().toString())[1];
                    cacheL[2] = townyflats.cache.get(pl.getUniqueId().toString())[2];
                }
                pl.sendMessage(tapp + " pos 2(x: " + x1 + "y: " + y1 + "z: " + z1 + ")");
            }

            pl.getLocation().getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 2);
        } else {
            pl.sendMessage(tapp + " Your new position is on the other chunk.All selected positions have been cleared");
            townyflats.cacheCh.put(pl.getUniqueId().toString(), selecedBl1.getLocation().getChunk());
            cacheL[3] = "0";
            cacheL[4] = "-100";
            cacheL[5] = "0";
            cacheL[0] = "0";
            cacheL[1] = "-100";
            cacheL[2] = "0";
        }
        townyflats.cache.put(pl.getUniqueId().toString(), cacheL);
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
                            x1 = selecedBl1.getLocation().getBlockX();
                            y1 = selecedBl1.getLocation().getBlockY();
                            z1 = selecedBl1.getLocation().getBlockZ();
                            if (townyflats.flats.containsKey(town)) {
                                int lengTh = townyflats.flats.get(town).length;
                                for (int i = 0; i < lengTh; i += 1) {
                                    if (CheckIfBlInCity(i,town,res)) {
                                        pl.sendMessage(tapp + "There is already an apartment here");
                                        y1 = -100;
                                        f = false;
                                        break;
                                    }
                                }
                            }
                            if (f) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    Privatev1(pl, selecedBl1, false);
                                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    Privatev1(pl, selecedBl1, true);

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
            townyflats.cacheCh.remove(event.getPlayer().getUniqueId().toString());
        }
    }
    //Deleting the info on town/chunk deletion;
    @EventHandler
    public void onUnclaimChunk(TownPreUnclaimEvent pos1) {
        Town town = pos1.getTown();
        int lengTh = townyflats.flats.get(town).length;
        for (int i = 0; i < lengTh; i += 1) {
            if ((pos1.getTownBlock().getX() == townyflats.flats.get(town)[i].xC)
                    && (pos1.getTownBlock().getZ() == townyflats.flats.get(town)[i].zC)) {

                Townyflats.Apartment[] copy = new Townyflats.Apartment[lengTh - 1];
                f = false;
                if (i + 1 != lengTh) {
                    for (int j = 0; j < townyflats.flats.get(town).length; j++) {

                        if (j >= i && j < i + 1) {

                            copy[j] = townyflats.flats.get(town)[(lengTh - 1) + (j - i)];
                        } else if (j >= lengTh - 1) {
                            break;
                        } else {
                            copy[j] = townyflats.flats.get(town)[j];
                        }

                    }
                    townyflats.flats.put(town, copy);
                } else if (lengTh != 1) {

                    for (int j = 0; j < lengTh - 1; j++) {
                        copy[j] = townyflats.flats.get(town)[j];
                        townyflats.flats.put(town, copy);

                    }
                } else {
                    townyflats.flats.remove(town);
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
    private boolean CheckIfBlInCity(int i,Town town,Resident res) {
        if (((x1 >= townyflats.flats.get(town)[i].x1 && x1 <= townyflats.flats.get(town)[i].x2)
                || (x1 <= townyflats.flats.get(town)[i].x1 && x1 >= townyflats.flats.get(town)[i].x2))
                && ((y1 >= townyflats.flats.get(town)[i].y1 && y1 <=townyflats.flats.get(town)[i].y2)
                || (y1 <= townyflats.flats.get(town)[i].y1 && y1 >= townyflats.flats.get(town)[i].y2))
                && ((z1 >= townyflats.flats.get(town)[i].z1 && z1 <= townyflats.flats.get(town)[i].z2)
                || (z1 <= townyflats.flats.get(town)[i].z1 && z1 >= townyflats.flats.get(town)[i].z2))
        )
        {return true;}
        else {return  false;}

    }
}