package net.codemarked.masters.render.programs;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.util.GuiUtil;
import org.lwjgl.nanovg.NVGColor;

import java.nio.FloatBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class TextRenderer {
    private final long vg;
    @Getter
    @Setter
    private float width, height;
    @Getter
    @Setter
    private int fontSize = 16;

    public TextRenderer(float width, float height) {
        this.width = width;
        this.height = height;
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == 0) {
            throw new RuntimeException("Could not initialize NanoVG");
        }
    }

    public void loadFont(String fontName, String fontPath) {
        int font = nvgCreateFont(vg, fontName, fontPath);
        if (font == -1) {
            throw new RuntimeException("Couldn't load font: " + fontPath);
        }
    }

    public void beginFrame() {
        nvgBeginFrame(vg, width, height, 1f);
        nvgFontFace(vg, "default");
        nvgFontSize(vg, fontSize);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
    }

    public void text(float x, float y, float padding, float lineHeight, String... lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            float lineY = y + i * lineHeight;

            float textWidth = nvgTextBounds(vg, 0, 0, line, (FloatBuffer) null);

            nvgBeginPath(vg);
            nvgRect(vg, x - padding, lineY - lineHeight / 2f, textWidth + padding * 2, lineHeight);
            color(0, 0, 0, 0.7f);
            nvgFill(vg);

            color(1.0f, 1.0f, 1.0f, 1.0f);
            text(line, x, lineY);
        }
    }

    public void endFrame() {
        nvgEndFrame(vg);
    }

    public void text(String text, float x, float y) {
        nvgText(vg, x, y, text);
    }

    public void color(float r, float g, float b, float a) {
        try (NVGColor color = GuiUtil.rgba(r, g, b, a)) {
            nvgFillColor(vg, color);
        }
    }

    public void cleanup() {
        nvgDelete(vg);
    }
}

