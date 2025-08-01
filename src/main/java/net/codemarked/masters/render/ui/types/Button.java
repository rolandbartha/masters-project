package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import org.lwjgl.nanovg.NVGColor;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.*;

public class Button extends Element<Button> {
    @Getter
    private final String label;
    @Getter
    @Setter
    private boolean active;
    private float fontSize = 16;
    private final Consumer<Button> onClick;

    public Button(String label, Consumer<Button> onClick) {
        this.label = label;
        this.onClick = onClick;
    }

    public Button fontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Button active(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public void render(long vg, Mouse mouse) {
        boolean hovered = isMouseOver(mouse) || active;
        nvgBeginPath(vg);
        if (centered) {
            float x = (Main.windowWidth - width) / 2f;
            nvgRect(vg, x, y, width, height);
        } else {
            nvgRect(vg, x, y, width, height);
        }
        try (NVGColor color = hovered ? GuiUtil.rgba(80, 80, 80, 255) : GuiUtil.rgba(50, 50, 50, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);

        nvgFontSize(vg, fontSize);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        if (centered) {
            float x = Main.windowWidth / 2f;
            nvgText(vg, x, y + height / 2f, label);
        } else {
            nvgText(vg, x + width / 2f, y + height / 2f, label);
        }
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (mouse.isPressed() && isMouseOver(mouse) && mouse.resetPress()) {
            if (onClick != null) onClick.accept(this);
        }
    }

    public boolean isMouseOver(Mouse mouse) {
        return isMouseOver(mouse.getX(), mouse.getY());
    }

    private boolean isMouseOver(float mx, float my) {
        if (centered) {
            float x = (Main.windowWidth - width) / 2f;
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}