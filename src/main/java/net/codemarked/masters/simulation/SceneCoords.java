package net.codemarked.masters.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.codemarked.masters.util.math.Vec3I;

@AllArgsConstructor
@Getter
public class SceneCoords {
    private final int x, y, z;

    public SceneCoords(Vec3I v) {
        this(v.getX(), v.getY(), v.getZ());
    }
}