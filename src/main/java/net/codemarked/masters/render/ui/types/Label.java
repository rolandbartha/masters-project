package net.codemarked.masters.render.ui.types;

import lombok.Setter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Vec4I;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public class Label extends Element<Label> {
    @Setter
    private String text;
    private float fontSize = 16;
    private final Vec4I color;

    public Label(String text) {
        this(text, null);
    }

    public Label(String text, Vec4I color) {
        this.text = text;
        this.color = color != null ? color : Vec4I.of(50, 50, 50, 255);
    }

    public Label fontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public void render(long vg, Mouse mouse) {
        nvgBeginPath(vg);
        if (centered) {
            float x = (Main.windowWidth - width) / 2f;
            nvgRect(vg, x, y, width, height);
        } else {
            nvgRect(vg, x, y, width, height);
        }
        try (NVGColor color1 = GuiUtil.rgba(color.getX(), color.getY(), color.getZ(), color.getW())) {
            nvgFillColor(vg, color1);
        }
        nvgFill(vg);

        nvgFontSize(vg, fontSize);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        if (centered) {
            float x = Main.windowWidth / 2f;
            nvgText(vg, x, y + height / 2f, text);
        } else {
            nvgText(vg, x + width / 2f, y + height / 2f, text);
        }
    }

    @Override
    public void update(long vg, Mouse mouse) {
    }
}
