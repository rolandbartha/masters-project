package net.codemarked.masters.util.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class Vec3I {
    private int x, y, z;

    public Vec3I(Vec3F v) {
        this((int) Math.floor(v.getX()), (int) Math.floor(v.getY()), (int) Math.floor(v.getZ()));
    }

    public Vec3I add(Vec3I v) {
        return of(x + v.x, y + v.y, z + v.z);
    }

    public Vec3I subtract(Vec3I v) {
        return of(x - v.x, y - v.y, z - v.z);
    }

    public Vec3I multiply(int t) {
        return of(x * t, y * t, z * t);
    }

    public float lengthSquared() {
        return (float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vec3I normalized() {
        float length = length();
        if (length > 0)
            return of((int) (x / length), (int) (y / length), (int) (z / length));
        return of(x, y, z);
    }

    public float distanceSquared(Vec3I v) {
        return MathUtil.pow2(x - v.x) + MathUtil.pow2(y - v.y) + MathUtil.pow2(z - v.z);
    }

    public float distance(Vec3I v) {
        return (float) Math.sqrt(distanceSquared(v));
    }

    public Vec3I cross(Vec3I v) {
        return cross(v.x, v.y, v.z);
    }

    public Vec3I cross(int x1, int y1, int z1) {
        return of(
                y * z1 - z * y1,
                z * x1 - x * z1,
                x * y1 - y * x1
        );
    }

    public float dot(Vec3I v) {
        return x * v.x + y * v.y + z * v.z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vec3I v)
            return x == v.x && y == v.y && z == v.z;
        return false;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}

