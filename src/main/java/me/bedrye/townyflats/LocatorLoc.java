package me.bedrye.townyflats;

import org.bukkit.Location;

public class LocatorLoc {
    int x1,x2,y1,y2,z1,z2,xC,zC;
    String world;
    public LocatorLoc(int x1,int x2,int y1,int y2,int z1,int z2,int xC, int zC,String world) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
        this.zC = zC;
        this.xC = xC;
        this.world = world;
    }
        void setLocation1(Location loc)
        {
            x1 = loc.getBlockX();
            y1 = loc.getBlockY();
            z1 = loc.getBlockZ();
        }
        void setLocation2(Location loc)
        {
            x2 = loc.getBlockX();
            y2 = loc.getBlockY();
            z2 = loc.getBlockZ();
        }
}
