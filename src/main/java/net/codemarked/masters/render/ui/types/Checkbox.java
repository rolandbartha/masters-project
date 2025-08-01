package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

import java.util.function.Consumer;

public class Checkbox extends Element<Checkbox> {
    private final String label;
    @Getter
    @Setter
    private boolean checked;
    private float fontSize = 16;
    private final Consumer<Boolean> onCheck;

    public Checkbox(String label, Consumer<Boolean> onCheck) {
        this.label = label;
        this.onCheck = onCheck;
    }

    public Checkbox checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public Checkbox fontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public void render(long vg, Mouse mouse) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        try (NVGColor color = GuiUtil.rgba(50, 50, 50, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);

        nvgFontSize(vg, fontSize);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgText(vg, x + 10, y + height / 2f, label);

        float boxSize = 16;
        float boxX = x + width - boxSize - 10;
        float boxY = y + (height - boxSize) / 2f;

        if (checked) {
            nvgBeginPath(vg);
            nvgRect(vg, boxX, boxY, boxSize, boxSize);
            try (NVGColor color = GuiUtil.rgba(50, 200, 255, 255)) {
                nvgFillColor(vg, color);
            }
            nvgFill(vg);
        }
        nvgBeginPath(vg);
        nvgRect(vg, boxX, boxY, boxSize, boxSize);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgStrokeWidth(vg, 2f);
        nvgStroke(vg);
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (!visible) return;
        if (mouse.isPressed() && isMouseOver(mouse.getX(), mouse.getY()) && mouse.resetPress()) {
            checked = !checked;
            if (onCheck != null) onCheck.accept(checked);
        }
    }

    private boolean isMouseOver(float mx, float my) {
        if (centered) {
            float x = (Main.windowWidth - width) / 2f;
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}
