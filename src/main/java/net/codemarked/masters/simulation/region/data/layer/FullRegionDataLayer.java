package net.codemarked.masters.simulation.region.data.layer;

import net.codemarked.masters.simulation.region.Region;

public class FullRegionDataLayer implements IRegionDataLayer {
    private final byte[] blocks = new byte[Region.SIZE * Region.SIZE];

    public FullRegionDataLayer(byte id) {
        for (int x = 0; x < Region.SIZE; x++) {
            for (int z = 0; z < Region.SIZE; z++) {
                blocks[(z << Region.SHIFT) | x] = id;
            }
        }
    }

    @Override
    public byte getBlock(int x, int z) {
        return blocks[(z << Region.SHIFT) | x];
    }

    @Override
    public IRegionDataLayer setBlock(int x, int z, byte id) {
        blocks[(z << Region.SHIFT) | x] = id;
        return this;
    }
}