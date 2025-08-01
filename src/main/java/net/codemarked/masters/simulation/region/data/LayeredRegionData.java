package net.codemarked.masters.simulation.region.data;

import net.codemarked.masters.simulation.region.Region;
import net.codemarked.masters.simulation.region.data.layer.IRegionDataLayer;
import net.codemarked.masters.simulation.region.data.layer.SingleBlockRegionDataLayer;

public class LayeredRegionData implements IRegionData {
    private final IRegionDataLayer[] layers;

    public LayeredRegionData(byte id) {
        layers = new IRegionDataLayer[Region.SIZE];
        for (int i = 0; i < Region.SIZE; i++) {
            layers[i] = new SingleBlockRegionDataLayer(id);
        }
    }

    @Override
    public IRegionData setBlock(int x, int y, int z, byte id) {
        layers[y] = layers[y].setBlock(x, z, id);
        return this;
    }

    @Override
    public byte getBlock(int x, int y, int z) {
        return layers[y].getBlock(x, z);
    }
}
