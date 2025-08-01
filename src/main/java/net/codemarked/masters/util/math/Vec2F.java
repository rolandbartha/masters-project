package net.codemarked.masters.util.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class Vec2F {
    private float x, y;

    public Vec2F add(Vec2F v) {
        return of(x + v.x, y + v.y);
    }

    public Vec2F subtract(Vec2F v) {
        return of(x - v.x, y - v.y);
    }

    public Vec2F multiply(int t) {
        return of(x * t, y * t);
    }

    public float lengthSquared() {
        return (float) (Math.pow(x, 2) + Math.pow(y, 2));
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vec2F normalized() {
        float length = length();
        if (length > 0)
            return of(x / length, y / length);
        return of(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vec2F v)
            return x == v.x && y == v.y;
        return false;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}