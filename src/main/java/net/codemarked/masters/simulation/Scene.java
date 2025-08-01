package net.codemarked.masters.simulation;

import lombok.Getter;
import net.codemarked.masters.Config;
import net.codemarked.masters.simulation.region.Region;
import net.codemarked.masters.simulation.region.RegionCoords;
import net.codemarked.masters.simulation.rule.Block;
import net.codemarked.masters.util.math.Direction;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.*;

@Getter
public class Scene {
    public static final int REGION_COUNT = (int) (Math.pow(40, 3) + 1);
    private final Map<Integer, Region> regions = new HashMap<>();
    private final Ant ant = new Ant();

    public Scene() {
        init();
    }

    public void init() {
        ant.init();
    }

    public void render(int shaderId) {
        if (regions.isEmpty()) return;
        regions.values().stream()
                .filter(Region::isLoaded)
                .forEach(region -> region.render(shaderId));
    }

    public void tick() {
        regions.values().stream().filter(Region::isDirty).forEach(this::render);
        for (int i = 0; i < Math.max(1, Config.stepsPerTick); i++) ant.tick(this);
    }

    public void cleanup() {
        regions.values().forEach(Region::delete);
        regions.clear();
    }

    public Region getOrCreateRegion(RegionCoords coords) {
        int hash = coords.hash();
        Region region = regions.get(hash);
        if (region == null) {
            region = new Region(coords.getX(), coords.getY(), coords.getZ());
            regions.put(hash, region);
            render(region);
        }
        return region;
    }

    public String getBlockAtCamera(SceneCoords coords) {
        Integer block = getBlockIfLoadedAt(coords.getX(), coords.getY(), coords.getZ());
        return block == null ? "Not loaded" : Block.get(block).toString();
    }

    public Integer getBlockAt(int x, int y, int z) {
        Region region = getOrCreateRegion(RegionCoords.fromSceneCoords(x, y, z));
        if (region == null) return null;
        return region.getBlock(x & (Region.SIZE - 1), y & (Region.SIZE - 1), z & (Region.SIZE - 1));
    }

    public Integer getBlockIfLoadedAt(int x, int y, int z) {
        Region region = regions.get(RegionCoords.fromSceneCoords(x, y, z).hash());
        if (region == null) return null;
        return region.getBlock(x & (Region.SIZE - 1), y & (Region.SIZE - 1), z & (Region.SIZE - 1));
    }

    public void setNeighborDirty(Region region, Direction direction) {
        int regionX = region.getX() + direction.getVector().getX();
        int regionY = region.getY() + direction.getVector().getY();
        int regionZ = region.getZ() + direction.getVector().getZ();
        Region neighborRegion = regions.get(RegionCoords.hash(regionX, regionY, regionZ));
        if (neighborRegion == null) return;
        neighborRegion.setDirty(true);
    }

    public void setBlockAt(int x, int y, int z, int block) {
        Region region = getOrCreateRegion(RegionCoords.fromSceneCoords(x, y, z));
        if (region == null) return;
        int regionX = x & (Region.SIZE - 1);
        int regionY = y & (Region.SIZE - 1);
        int regionZ = z & (Region.SIZE - 1);
        if (regionX == 0) {
            setNeighborDirty(region, Direction.WEST);
        } else if (regionX == Region.SIZE - 1) {
            setNeighborDirty(region, Direction.EAST);
        }
        if (regionY == 0) {
            setNeighborDirty(region, Direction.DOWN);
        } else if (regionY == Region.SIZE - 1) {
            setNeighborDirty(region, Direction.UP);
        }
        if (regionZ == 0) {
            setNeighborDirty(region, Direction.NORTH);
        } else if (regionZ == Region.SIZE - 1) {
            setNeighborDirty(region, Direction.SOUTH);
        }
        region.setBlock(regionX, regionY, regionZ, block);
    }

    public void addRegion(Region region, IntBuffer vertices, IntBuffer indices) {
        //Main.LOGGER.info(region.getFileName() + " " + vertices.limit() + " " + indices.limit());
        region.init(vertices, indices);
    }

    public void updateRegion(Region region, IntBuffer vertices, IntBuffer indices) {
        //Main.LOGGER.info("updating " + region.getFileName() + " " + vertices.limit() + " " + indices.limit());
        if (!region.isLoaded()) return;
        region.update(vertices, indices);
    }

    public void render(Region region) {
        IntBuffer vertices = BufferUtils.createIntBuffer(Region.VERTICES);
        IntBuffer indices = BufferUtils.createIntBuffer(Region.INDICES);

        int regionX = region.getX() << Region.SHIFT;
        int regionY = region.getY() << Region.SHIFT;
        int regionZ = region.getZ() << Region.SHIFT;

        int indexOffset = 0;
        for (int y = 0; y < Region.SIZE; y++) {
            for (int x = 0; x < Region.SIZE; x++) {
                for (int z = 0; z < Region.SIZE; z++) {
                    Integer block = region.getBlock(x, y, z);
                    if (block == 0) continue;
                    int startVertex = indexOffset / 6 * 4;
                    for (Direction face : Direction.values()) {
                        int dx = x + face.getVector().getX();
                        int dy = y + face.getVector().getY();
                        int dz = z + face.getVector().getZ();
                        Integer block1;
                        if (Region.isOutside(dx, dy, dz)) {
                            block1 = getBlockIfLoadedAt(regionX + dx, regionY + dy, regionZ + dz);
                        } else {
                            block1 = region.getBlock(dx, dy, dz);
                        }
                        if (block1 != null && block1 != 0) continue;
                        int[] faceVertices = face.getFaceVertices();
                        for (int i = 0; i < faceVertices.length / 3; i++) {
                            vertices.put(Region.packVertexData(
                                    x + faceVertices[i * 3],
                                    y + faceVertices[i * 3 + 1],
                                    z + faceVertices[i * 3 + 2],
                                    face.getId(), Config.multicolorBlocks ? block : 1));
                        }
                        indices.put(startVertex);
                        indices.put(startVertex + 1);
                        indices.put(startVertex + 2);
                        indices.put(startVertex + 2);
                        indices.put(startVertex + 3);
                        indices.put(startVertex);
                        indexOffset += 6;
                        startVertex += 4;
                    }
                }
            }
        }
        vertices.flip();
        indices.flip();
        if (!region.isLoaded() && regions.size() < REGION_COUNT) {
            addRegion(region, vertices, indices);
        } else {
            updateRegion(region, vertices, indices);
        }
    }
}