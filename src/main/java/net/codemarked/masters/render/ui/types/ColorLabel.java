package net.codemarked.masters.render.ui.types;

import lombok.Setter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Vec4I;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

@Setter
public class ColorLabel extends Element<ColorLabel> {
    private Vec4I color;

    public ColorLabel(Vec4I color) {
        this.color = color != null ? color
                : Vec4I.of(255, 255, 255, 255);
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
    }

    @Override
    public void update(long vg, Mouse mouse) {
    }
}
