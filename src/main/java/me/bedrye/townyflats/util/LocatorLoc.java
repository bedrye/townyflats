package me.bedrye.townyflats.util;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class LocatorLoc {
    private int x1,x2,y1,y2,z1,z2;
    final private Chunk chunk;
    public LocatorLoc(Chunk c){
        chunk=c;
        y1=-1000;
        y2=-1000;
    }
    public LocatorLoc (Location loc1,Location loc2){
        if(loc1.getChunk().equals(loc2.getChunk())) {
            chunk = loc1.getChunk();
            if (loc1.getBlockX()>loc2.getBlockX()) {
                x1 = loc1.getBlockX();
                x2 = loc2.getBlockX();
            }
            else {
                x1 = loc2.getBlockX();
                x2 = loc1.getBlockX();
            }
            if (loc1.getBlockY()>loc2.getBlockY()) {
                y1 = loc1.getBlockY();
                y2 = loc2.getBlockY();
            }
            else {
                y1 = loc2.getBlockY();
                y2 = loc1.getBlockY();
            }
            if (loc1.getBlockY()>loc2.getBlockY()) {
                z1 = loc1.getBlockZ();
                z2 = loc2.getBlockZ();
            }
            else {
                z1 = loc2.getBlockZ();
                z2 = loc1.getBlockZ();
            }
        }
        throw new RuntimeException("Different Chunks");

    }

        public void setLocation1(Location loc)
        {
            x1 = loc.getBlockX();
            y1 = loc.getBlockY();
            z1 = loc.getBlockZ();
        }
        public void setLocation2(Location loc)
        {
            x2 = loc.getBlockX();
            y2 = loc.getBlockY();
            z2 = loc.getBlockZ();
        }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public int getZ1() {
        return z1;
    }

    public int getZ2() {
        return z2;
    }

    public Chunk getChunk() {
        return chunk;
    }
    public boolean hasTwoLocations(){
        return y1!=-1000&&y2!=-1000;
    }
}
