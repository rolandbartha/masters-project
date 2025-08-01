package net.codemarked.masters.simulation.region.data;

public class SingleBlockRegionData implements IRegionData {
    private final byte id;

    public SingleBlockRegionData(byte id) {
        this.id = id;
    }

    @Override
    public IRegionData setBlock(int x, int y, int z, byte id) {
        if (this.id == id) return this;
        LayeredRegionData chunkData = new LayeredRegionData(this.id);
        chunkData.setBlock(x, y, z, id);
        return chunkData;
    }

    @Override
    public byte getBlock(int x, int y, int z) {
        return id;
    }
}
