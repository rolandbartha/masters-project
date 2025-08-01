package net.codemarked.masters.util.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class Vec3F {
    private float x, y, z;

    public Vec3F add(Vec3F v) {
        return of(x + v.x, y + v.y, z + v.z);
    }

    public Vec3F subtract(Vec3F v) {
        return of(x - v.x, y - v.y, z - v.z);
    }

    public Vec3F multiply(float t) {
        return of(x * t, y * t, z * t);
    }

    public float lengthSquared() {
        return (float) (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vec3F normalized() {
        float length = length();
        if (length > 0)
            return of(x / length, y / length, z / length);
        return of(x, y, z);
    }

    public float distanceSquared(Vec3F v) {
        return MathUtil.pow2(x - v.x) + MathUtil.pow2(y - v.y) + MathUtil.pow2(z - v.z);
    }

    public float distance(Vec3F v) {
        return (float) Math.sqrt(distanceSquared(v));
    }

    public Vec3F cross(Vec3F v) {
        return cross(v.x, v.y, v.z);
    }

    public Vec3F cross(float x1, float y1, float z1) {
        return of(
                y * z1 - z * y1,
                z * x1 - x * z1,
                x * y1 - y * x1
        );
    }

    public float dot(Vec3F v) {
        return x * v.x + y * v.y + z * v.z;
    }
}
