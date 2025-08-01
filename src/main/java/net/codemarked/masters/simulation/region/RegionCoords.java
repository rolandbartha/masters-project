package net.codemarked.masters.simulation.region;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegionCoords {
    private final int x, y, z;

    public static RegionCoords fromSceneCoords(int x, int y, int z) {
        return new RegionCoords(
                x >> Region.SHIFT,
                y >> Region.SHIFT,
                z >> Region.SHIFT);
    }

    private static final int BITS = 10;
    private static final int MASK = (1 << BITS) - 1; // 0x3FF
    private static final int BIAS = 1 << (BITS - 1); // 512

    public int hash() {
        return hash(x, y, z);
    }

    public static int hash(int x, int y, int z) {
        int ux = (x + BIAS) & MASK;
        int uy = (y + BIAS) & MASK;
        int uz = (z + BIAS) & MASK;
        return (ux << (BITS * 2)) | (uy << BITS) | uz;
    }
}
