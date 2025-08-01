package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import org.lwjgl.nanovg.NVGColor;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.*;

public class DragBar extends Element<DragBar> {
    @Getter
    private float value, increment, min = 0f, max = 1f;
    @Getter
    private boolean dragging, lastMousePress;
    private final Consumer<DragBar> onChange;

    public DragBar(Consumer<DragBar> onChange) {
        this.onChange = onChange;
    }

    public DragBar value(float v) {
        this.value = Math.max(0f, Math.min(1f, v));
        return this;
    }

    public DragBar increment(float increment) {
        this.increment = increment;
        return this;
    }

    public DragBar min(float v) {
        this.min = Math.max(0f, Math.min(1f, v));
        return this;
    }

    public DragBar max(float v) {
        this.max = Math.max(0f, Math.min(1f, v));
        return this;
    }

    @Override
    public void unfocus() {
        dragging = false;
    }

    @Override
    public void render(long vg, Mouse mouse) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y + height / 2f - 3f, width, 6f);
        try (NVGColor color = GuiUtil.rgba(100, 100, 100, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgRect(vg, x, y + height / 2f - 3f, width * value, 6f);
        try (NVGColor color = GuiUtil.rgba(50, 200, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);

        float knobX = x + value * width;
        float knobY = y + height / 2f;
        nvgBeginPath(vg);
        nvgCircle(vg, knobX, knobY, 8f);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);
        try (NVGColor color = GuiUtil.rgba(50, 50, 50, 255)) {
            nvgStrokeColor(vg, color);
        }
        nvgStrokeWidth(vg, 1.5f);
        nvgStroke(vg);
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (!lastMousePress && mouse.isPressed() && isMouseOver(mouse.getX(), mouse.getY())) {
            dragging = true;
        } else if (!mouse.isPressed()) {
            dragging = false;
        }
        if (dragging) {
            float relativeX = Math.max(0, Math.min(mouse.getX() - x, width));
            value = Math.round(relativeX / width / increment) * increment;
            value = Math.max(min, Math.min(max, value));
            if (onChange != null) onChange.accept(this);
        }
        lastMousePress = mouse.isPressed();
    }

    private boolean isMouseOver(float mx, float my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}
