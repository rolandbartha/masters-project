package net.codemarked.masters.simulation.region;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.simulation.region.data.IRegionData;
import net.codemarked.masters.simulation.region.data.SingleBlockRegionData;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Region {
    public static final int SIZE = 16;
    public static final int SHIFT = Integer.numberOfTrailingZeros(SIZE);
    public static final int BLOCKS = SIZE * SIZE * SIZE;
    public static final int VERTICES = 4 * 6 * BLOCKS;
    public static final int INDICES = 6 * 6 * BLOCKS;

    @Getter
    private final int x, y, z, hash;
    private IRegionData regionData = new SingleBlockRegionData((byte) 0);
    @Getter
    private int vao, vbo, ebo, indexBufferSize;
    @Getter
    @Setter
    private boolean loaded, dirty;

    public Region(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = RegionCoords.hash(x, y, z);
    }

    public void init(IntBuffer vertices, IntBuffer indices) {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        if (vbo == 0) throw new RuntimeException("Failed to generate vbo!");

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, Integer.BYTES, 0);

        ebo = glGenBuffers();
        if (ebo == 0) throw new RuntimeException("Failed to generate ebo!");

        indexBufferSize = indices.limit();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
        loaded = true;
        dirty = false;
    }

    public void update(IntBuffer vertices, IntBuffer indices) {
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        indexBufferSize = indices.limit();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
        dirty = false;
    }

    public void render(int shaderId) {
        glBindVertexArray(vao);
        glUniform3i(glGetUniformLocation(shaderId, "offset"), x << Region.SHIFT, y << Region.SHIFT, z << Region.SHIFT);
        glDrawElements(GL_TRIANGLES, indexBufferSize, GL_UNSIGNED_INT, 0);
    }

    public void delete() {
        loaded = false;
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

    public Integer getBlock(int x, int y, int z) {
        if (isOutside(x, y, z)) return null;
        return regionData.getBlock(x, y, z) & 0xFF;
    }

    public void setBlock(int x, int y, int z, int block) {
        if (isOutside(x, y, z)) return;
        regionData = regionData.setBlock(x, y, z, (byte) (block & 0xFF));
        dirty = true;
    }

    public static boolean isOutside(int x, int y, int z) {
        return isOutside(x) || isOutside(y) || isOutside(z);
    }

    public static boolean isOutside(int value) {
        return value < 0 || value >= SIZE;
    }

    public static int packVertexData(int x, int y, int z, int face, int blockId) {
        return (x & 0x1F) |  // 5 bits
                ((y & 0x1F) << 5) |  // 5 bits
                ((z & 0x1F) << 10) |  // 5 bits
                ((face & 0x07) << 15) |  // 3 bits
                ((blockId & 0x3FFF) << 18);  // 14 bits
    }
}