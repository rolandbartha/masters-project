package net.codemarked.masters.render;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Config;
import net.codemarked.masters.simulation.SceneCoords;
import net.codemarked.masters.util.math.Direction;
import net.codemarked.masters.util.math.Mat4F;
import net.codemarked.masters.util.math.MathUtil;
import net.codemarked.masters.util.math.Vec3F;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@Setter
public class Camera {
    private static final float zNear = 0.1f, zFar = 1000.0f;
    private static final float movementSpeed = 10.0f;
    private static final Vec3F UP = Vec3F.of(0, 1, 0);

    private Vec3F position, front, right;
    private Mat4F projection, view;
    private float yaw, pitch, lastXMove, lastYMove;
    private float aspectRatio, fov = 80f, mouseSensitivity = 0.1f;
    private boolean firstMouse;

    public Camera(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        init();
    }

    public void updateAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        projection = Mat4F.of().projection(fov, aspectRatio, zNear, zFar);
    }

    public void updateViewMatrix() {
        view = Mat4F.of().lookAt(position, position.add(front), UP);
    }

    public void updateVectors() {
        float yawRadians = (float) Math.toRadians(yaw);
        float pitchRadians = (float) Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRadians);
        float x = (float) (Math.cos(yawRadians) * cosPitch);
        float y = (float) Math.sin(pitchRadians);
        float z = (float) (Math.sin(yawRadians) * cosPitch);
        front = Vec3F.of(x, y, z).normalized();
        right = front.cross(UP).normalized();
        updateViewMatrix();
    }

    public void updateSettings() {
        fov = 30 + Math.max(Math.min(120, Config.fieldOfView * 120), 0);
        mouseSensitivity = Math.max(0.1f, Math.min(1f, Config.mouseSensitivity));
        updateAspectRatio(aspectRatio);
    }

    public void init() {
        yaw = 90f;
        lastXMove = lastYMove = 0;
        pitch = -89;
        firstMouse = true;
        position = Vec3F.of(0, 100, 0);
        front = Vec3F.of();
        right = Vec3F.of();
        updateViewMatrix();
        updateVectors();
        updateSettings();
    }

    public Direction getDirection() {
        return Direction.fromYawPitch(yaw, pitch);
    }

    public SceneCoords getSceneCoords() {
        return new SceneCoords(
                (int) Math.floor(position.getX()),
                (int) Math.floor(position.getY()),
                (int) Math.floor(position.getZ()));
    }

    public void onMouseMove(float xMove, float yMove) {
        if (firstMouse) {
            lastXMove = xMove;
            lastYMove = yMove;
            firstMouse = false;
        }
        yaw = MathUtil.clamp(yaw + (xMove - lastXMove) * mouseSensitivity);
        pitch = MathUtil.boundsFloat(pitch + (lastYMove - yMove) * mouseSensitivity, -89f, 89f);
        lastXMove = xMove;
        lastYMove = yMove;
        updateVectors();
    }

    public void onKeyboard(long window, float deltaTime, boolean sprinting) {
        Vec3F movement = Vec3F.of();
        boolean isSprinting = sprinting || glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS;
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            movement = movement.add(front);
            movement.setY(0);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            movement = movement.subtract(front);
            movement.setY(0);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            movement = movement.subtract(right);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            movement = movement.add(right);
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            movement = movement.add(UP);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            movement = movement.subtract(UP);
        }
        if (movement.lengthSquared() > 0) {
            float multiplier = (isSprinting ? 2f : 1f) * Config.movementSpeed
                    * MathUtil.pow2(movementSpeed) * deltaTime;
            position = position.add(movement.normalized().multiply(multiplier));
            updateViewMatrix();
        }
    }

    public Mat4F getView() {
        if (view == null)
            updateViewMatrix();
        return view;
    }

    public String getDebugInfo() {
        return "(" +
                String.format("%.2f", position.getX()) + "," +
                String.format("%.2f", position.getY()) + "," +
                String.format("%.2f", position.getZ()) + "," +
                String.format("%.2f", yaw) + "," +
                String.format("%.2f", pitch) + "," +
                getDirection() +
                ")";
    }
}