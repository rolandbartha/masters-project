package net.codemarked.masters.util;

import net.codemarked.masters.util.math.Vec2F;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;

public class GuiUtil {

    public static final DecimalFormat decimalFormat;

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setMaximumFractionDigits(2);
    }

    public static NVGColor rgba(int r, int g, int b, int a) {
        NVGColor color = NVGColor.calloc();
        color.r(r / 255f);
        color.g(g / 255f);
        color.b(b / 255f);
        color.a(a / 255f);
        return color;
    }

    public static NVGColor rgba(float r, float g, float b, float a) {
        NVGColor color = NVGColor.calloc();
        color.r(r);
        color.g(g);
        color.b(b);
        color.a(a);
        return color;
    }

    public static float substringWidth(long vg, float x, float y, StringBuilder text, int untilIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb = stack.UTF8(text.substring(0, Math.min(untilIndex, text.length())));
            FloatBuffer bounds = stack.mallocFloat(4);
            nvgTextBounds(vg, x, y, bb, bounds);
            return bounds.get(2);
        }
    }

    public static Vec2F stringHorizontalBounds(long vg, float x, float y, StringBuilder text, int untilIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb = stack.UTF8(text.substring(0, Math.min(untilIndex, text.length())));
            FloatBuffer bounds = stack.mallocFloat(4);
            nvgTextBounds(vg, x, y, bb, bounds);
            return Vec2F.of(bounds.get(0), bounds.get(2));
        }
    }
}