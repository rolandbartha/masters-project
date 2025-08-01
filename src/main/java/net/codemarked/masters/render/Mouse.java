package net.codemarked.masters.render;

import lombok.Getter;
import lombok.Setter;

public class Mouse {
    @Getter
    private float x, y;
    private float width, height;
    @Getter
    @Setter
    private boolean pressed;
    @Getter
    private float scrollOffset;

    public Mouse(float width, float height) {
        updateViewPort(width, height);
        center();
    }

    public void updateViewPort(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void onTickEnd() {
        scrollOffset = 0;
    }

    public boolean resetPress() {
        pressed = false;
        return true;
    }

    public void center() {
        x = width / 2;
        y = height / 2;
    }

    public void onMouseButton(boolean pressed) {
        this.pressed = pressed;
    }

    public void onMouseMove(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void onMouseScroll(float offset) {
        this.scrollOffset = offset;
    }
}
