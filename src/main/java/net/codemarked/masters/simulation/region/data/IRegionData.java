package net.codemarked.masters.simulation.region.data;

public interface IRegionData {
    IRegionData setBlock(int x, int y, int z, byte id);

    byte getBlock(int x, int y, int z);
}