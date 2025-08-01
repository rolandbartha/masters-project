package net.codemarked.masters.simulation.region.data.layer;

public interface IRegionDataLayer {

    IRegionDataLayer setBlock(int x, int z, byte id);

    byte getBlock(int x, int z);
}