package net.codemarked.masters.simulation.region.data.layer;

public class SingleBlockRegionDataLayer implements IRegionDataLayer {
    private final byte id;

    public SingleBlockRegionDataLayer(byte id) {
        this.id = id;
    }

    @Override
    public byte getBlock(int x, int z) {
        return id;
    }

    @Override
    public IRegionDataLayer setBlock(int x, int z, byte id) {
        if (this.id == id) return this;
        FullRegionDataLayer newLayer = new FullRegionDataLayer(this.id);
        newLayer.setBlock(x, z, id);
        return newLayer;
    }
}