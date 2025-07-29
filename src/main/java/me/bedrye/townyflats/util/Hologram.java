package me.bedrye.townyflats.util;

import com.palmergames.bukkit.towny.object.Resident;
import me.bedrye.townyflats.TownyFlats;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

    private ArmorStand[] entities = new ArmorStand[4] ;
    private final Apartment apartment;
    private final int  xA,zA, yA;

    public int getX() {
        return xA;
    }

    public int getZ() {
        return zA;
    }

    public int getY() {
        return yA;
    }

    public Hologram(Apartment apartment, Location loc){


        this.apartment = apartment;
        xA = loc.getBlockX();
        zA = loc.getBlockZ();
        yA = loc.getBlockY();

        entities[0] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.3, 0), EntityType.ARMOR_STAND);
        entities[1] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
        entities[2] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.25, 0), EntityType.ARMOR_STAND);
        entities[3] = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, 0.40, 0), EntityType.ARMOR_STAND);
        entities[3].setCustomName(TownyFlats.getLang().hologram_1);
        entities[2].setCustomName(TownyFlats.getLang().command_info_owner+" §l" + apartment.getOwner());
        entities[1].setCustomName(TownyFlats.getLang().command_info_sell+" §l" + apartment.getPrice() + TownyFlats.getLang().money_symbol);
        entities[0].setCustomName(TownyFlats.getLang().hologram_4);
        for (ArmorStand ent : entities) {
            ent.setGravity(false);
            ent.setCanPickupItems(false);
            ent.setCustomNameVisible(true);
            ent.setVisible(false);
            ent.setSmall(true);
            ent.setInvisible(true);

        }

        loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 2004);
    }


    @Override
    protected void finalize() {
        RemoveHologram();


    }

    public void RemoveHologram(){
            for (ArmorStand ent: entities)
                if (ent!=null)
                    ent.remove();

        }
    public void BuyWithHologram(ArmorStand e, Resident pl) {
        for (ArmorStand ar : entities) {
            if (ar == e) {
                if (!apartment.BuyPlot(pl)){
                    pl.getPlayer().sendMessage(TownyFlats.getLang().tapp + TownyFlats.getLang().command_buy_false);
                }
                return;
            }

        }

    }
}
