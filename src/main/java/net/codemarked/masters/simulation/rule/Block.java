package net.codemarked.masters.simulation.rule;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Block {
    EMPTY(0, 0, 0, 0),
    YELLOW(1, 255, 255, 0),
    RED(2, 255, 0, 0),
    BLUE(3, 0, 0, 255),
    GREEN(4, 0, 128, 0),
    MAGENTA(5, 255, 0, 255),
    CYAN(6, 0, 255, 255),
    ORANGE(7, 255, 165, 0),
    PURPLE(8, 128, 0, 128),
    LIME(9, 51, 255, 51),
    WHITE(10, 255, 255, 255),
    GOLD(11, 255, 215, 0),
    BROWN(12, 139, 69, 19),
    DARKRED(13, 139, 0, 0),
    PINK(14, 255, 193, 203);

    private final int id;
    private final int r, g, b;

    Block(int id, int r, int g, int b) {
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Block get(int id) {
        return Arrays.stream(values()).filter(b -> b.id == id).findFirst().orElse(null);
    }
}
