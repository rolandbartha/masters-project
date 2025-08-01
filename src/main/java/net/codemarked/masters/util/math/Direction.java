package net.codemarked.masters.util.math;

import lombok.Getter;

@Getter
public enum Direction {
    EAST(0, Vec3I.of(1, 0, 0),
            new int[]{
                    1, 0, 1,
                    1, 1, 1,
                    1, 1, 0,
                    1, 0, 0
            }),
    WEST(1, Vec3I.of(-1, 0, 0),
            new int[]{
                    0, 0, 0,
                    0, 1, 0,
                    0, 1, 1,
                    0, 0, 1
            }),
    SOUTH(2, Vec3I.of(0, 0, 1),
            new int[]{
                    0, 0, 1,
                    0, 1, 1,
                    1, 1, 1,
                    1, 0, 1
            }),
    NORTH(3, Vec3I.of(0, 0, -1),
            new int[]{
                    1, 0, 0,
                    1, 1, 0,
                    0, 1, 0,
                    0, 0, 0
            }),
    UP(4, Vec3I.of(0, 1, 0),
            new int[]{
                    1, 1, 0,
                    1, 1, 1,
                    0, 1, 1,
                    0, 1, 0
            }),
    DOWN(5, Vec3I.of(0, -1, 0),
            new int[]{
                    0, 0, 0,
                    0, 0, 1,
                    1, 0, 1,
                    1, 0, 0
            });
    private final int id;
    private final Vec3I vector;
    private final int[] faceVertices;

    Direction(int id, Vec3I vector, int[] faceVertices) {
        this.id = id;
        this.vector = vector;
        this.faceVertices = faceVertices;
    }

    public Direction opposite() {
        return switch (this) {
            case UP:
                yield DOWN;
            case DOWN:
                yield UP;
            case EAST:
                yield WEST;
            case WEST:
                yield EAST;
            case SOUTH:
                yield NORTH;
            case NORTH:
                yield SOUTH;
        };
    }

    public static Direction fromYawPitch(float yaw, float pitch) {
        if (pitch > 45) return Direction.UP;
        if (pitch < -45) return Direction.DOWN;
        return fromYaw(yaw);
    }

    public static Direction fromYaw(float degrees) {
        float yaw = degrees % 360;
        if (yaw < 0) yaw += 360;
        yaw -= 90;
        if (yaw >= 45 && yaw < 135) {
            return Direction.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return Direction.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return Direction.EAST;
        }
        return Direction.SOUTH;
    }

    public static Direction fromVector(Vec3I vec) {
        for (Direction dir : values())
            if (dir.getVector().equals(vec)) return dir;
        throw new IllegalArgumentException("Invalid vector: " + vec);
    }
}