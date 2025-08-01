package net.codemarked.masters.simulation.rule;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Rotation {
    LEFT(0), RIGHT(1), UP(2), DOWN(3), FORWARD(4), BACKWARD(5);
    private final int id;

    Rotation(int id) {
        this.id = id;
    }

    public String getFirstLetter() {
        return toString().substring(0, 1);
    }

    public static Rotation get(int id) {
        return Arrays.stream(values())
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
