package net.codemarked.masters.util.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class Mat4F {
    private final float[] m;

    public static Mat4F of() {
        return of(new float[16]);
    }

    public Mat4F identity() {
        float[] m = new float[16];
        for (int i = 0; i < 16; i++) {
            m[i] = i % 5 == 0 ? 1.0f : 0.0f;
        }
        return of(m);
    }

    public Mat4F multiply(Mat4F other) {
        float[] result = new float[16];
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                result[col * 4 + row] =
                        m[row] * other.m[col * 4] +
                                m[row + 4] * other.m[col * 4 + 1] +
                                m[row + 8] * other.m[col * 4 + 2] +
                                m[row + 12] * other.m[col * 4 + 3];
        return of(result);
    }

    // https://www.songho.ca/opengl/gl_projectionmatrix.html
    public Mat4F projection(float fov, float aspectRatio, float near, float far) {
        float tangent = (float) Math.tan(Math.toRadians(fov) / 2f);
        float right = near * tangent;
        float top = right / aspectRatio;

        float[] m = new float[16];
        m[0] = near / right;
        m[5] = near / top;
        m[10] = -(far + near) / (far - near);
        m[11] = -1f;
        m[14] = -(2f * far * near) / (far - near);
        return of(m);
    }

    // https://en.wikipedia.org/wiki/Orthographic_projection
    public Mat4F ortho(float left, float right, float bottom, float top, float near, float far) {
        float right_left = right - left;
        float top_bottom = top - bottom;
        float far_near = far - near;

        float[] m = new float[16];
        m[0] = 2f / right_left;
        m[5] = 2f / top_bottom;
        m[10] = -2f / far_near;
        m[12] = -(right + left) / right_left;
        m[13] = -(top + bottom) / top_bottom;
        m[14] = -(far + near) / far_near;
        m[15] = 1f;
        return of(m);
    }

    // https://www.3dgep.com/understanding-the-view-matrix/#Look_At_Camera - optimized
    public Mat4F lookAt(Vec3F eye, Vec3F target, Vec3F up) {
        Vec3F zAxis = eye.subtract(target).normalized();
        Vec3F xAxis = up.cross(zAxis).normalized();
        Vec3F yAxis = zAxis.cross(xAxis);

        float[] m = new float[16];
        m[0] = xAxis.getX();
        m[1] = yAxis.getX();
        m[2] = zAxis.getX();

        m[4] = xAxis.getY();
        m[5] = yAxis.getY();
        m[6] = zAxis.getY();

        m[8] = xAxis.getZ();
        m[9] = yAxis.getZ();
        m[10] = zAxis.getZ();

        m[12] = -xAxis.dot(eye);
        m[13] = -yAxis.dot(eye);
        m[14] = -zAxis.dot(eye);
        m[15] = 1.0f;
        return of(m);
    }
}
