package net.codemarked.masters;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class Information {

    private long lastFrameTime, lastUpdateTime;
    private int framesSinceUpdate, ticks;
    private float framesPerSecond, frameTimeAccumulator;
    private final long startTime;

    public Information() {
        startTime = lastUpdateTime = System.currentTimeMillis();
    }

    public void tick(long currentTime) {
        ticks++;
        lastUpdateTime = currentTime;
    }

    public float getDeltaFrameTime() {
        long currentFrameTime = System.nanoTime();
        float deltaTime = (currentFrameTime - lastFrameTime) / 1000000000f;
        lastFrameTime = currentFrameTime;

        frameTimeAccumulator += deltaTime;
        framesSinceUpdate++;

        float fpsUpdateInterval = 0.5f;
        if (frameTimeAccumulator >= fpsUpdateInterval) {
            framesPerSecond = framesSinceUpdate / frameTimeAccumulator;
            frameTimeAccumulator = 0.0f;
            framesSinceUpdate = 0;
        }
        return deltaTime;
    }

    public String upTime() {
        Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - startTime);
        long hours = elapsed.toHours();
        long minutes = elapsed.toMinutes() % 60;
        long seconds = elapsed.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
