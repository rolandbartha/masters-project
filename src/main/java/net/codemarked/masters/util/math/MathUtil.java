package net.codemarked.masters.util.math;

public class MathUtil {
    public static float clamp(float yaw) {
        yaw %= 360;
        if (yaw < -180) yaw += 360;
        if (yaw >= 180) yaw -= 360;
        return yaw;
    }

    public static float boundsFloat(float value, float min, float max) {
        return Math.max(Math.min(max, value), min);
    }

    public static float pow2(float x) {
        return x * x;
    }

    public static double pow2(double x) {
        return x * x;
    }
}
