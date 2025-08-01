package net.codemarked.masters.render.ui.types;

import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Vec2F;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.codemarked.masters.util.GuiUtil.rgba;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;

public class TextBox extends Element<TextBox> {
    private final List<StringBuilder> lines
            = new ArrayList<>(List.of(new StringBuilder()));
    private String placeholder = Strings.EMPTY;
    private float fontSize = 16;
    private boolean focused = false;
    private int caretLine, caretPos;
    private boolean caretVisible = true;
    private int caretTimer;
    private float ascent, descent, lineHeight = 20f, padding = 5f;
    private boolean metrics;
    private Consumer<TextBox> onEdit;

    public TextBox onEdit(Consumer<TextBox> consumer) {
        onEdit = consumer;
        return this;
    }

    public TextBox placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public TextBox fontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public TextBox padding(float padding) {
        this.padding = padding;
        return this;
    }

    @Override
    public void unfocus() {
        focused = false;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public TextBox text(String text) {
        if (text != null) {
            lines.clear();
            Arrays.stream(text.split("\n")).map(StringBuilder::new).forEach(lines::add);
        }
        if (lines.isEmpty())
            lines.add(new StringBuilder());
        return this;
    }

    public String getText() {
        return String.join("\n", lines.stream().map(StringBuilder::toString).toList());
    }

    @Override
    public void render(long vg, Mouse mouse) {
        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        try (NVGColor color = rgba(40, 40, 40, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);

        try (NVGColor color = focused ? rgba(255, 255, 255, 255) : rgba(150, 150, 150, 255)) {
            nvgStrokeColor(vg, color);
        }
        nvgStrokeWidth(vg, 2f);
        nvgStroke(vg);

        nvgFontSize(vg, fontSize);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        if (!metrics) {
            metrics = true;
            FloatBuffer asc = BufferUtils.createFloatBuffer(1);
            FloatBuffer desc = BufferUtils.createFloatBuffer(1);
            FloatBuffer lh = BufferUtils.createFloatBuffer(1);
            nvgTextMetrics(vg, asc, desc, lh);
            ascent = asc.get(0);
            descent = desc.get(0);
            lineHeight = lh.get(0);
        }
        try (NVGColor color = rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        float px = x + padding, py = y + padding;
        if (lines.isEmpty() || (lines.size() == 1 && lines.get(0).isEmpty())) {
            try (NVGColor color = rgba(150, 150, 150, 255)) {
                nvgFillColor(vg, color);
            }
            nvgText(vg, px, py, placeholder);
        } else {
            for (int i = 0; i < lines.size(); i++) {
                try (NVGColor color = rgba(255, 255, 255, 255)) {
                    nvgFillColor(vg, color);
                }
                nvgText(vg, px, py + i * lineHeight, lines.get(i).toString());
            }
        }

        if (focused && caretVisible) {
            StringBuilder currentLine = lines.get(caretLine);
            float caretY = py + caretLine * lineHeight;
            float caretX = GuiUtil.substringWidth(vg, px, caretY, currentLine, caretPos);
            nvgBeginPath(vg);
            nvgMoveTo(vg, caretX + 1, caretY + ascent + 6);
            nvgLineTo(vg, caretX + 1, caretY - descent - 6);
            try (NVGColor color = rgba(255, 255, 255, 255)) {
                nvgStrokeColor(vg, color);
            }
            nvgStrokeWidth(vg, 1);
            nvgStroke(vg);
        }
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (!visible) return;
        if (++caretTimer >= 1000L) {
            caretVisible = !caretVisible;
            caretTimer = 0;
        }
        if (!mouse.isPressed()) return;
        focused = isMouseOver(mouse.getX(), mouse.getY()) && mouse.resetPress();
        Main.getEngine().getMenu().setFocusedTextBox(focused ? this : null);
        int clickedLine = Math.max(0, Math.min((int) ((mouse.getY() - getY()) / lineHeight), lines.size() - 1));
        StringBuilder lineText = lines.get(clickedLine);
        if (lineText.isEmpty()) {
            caretLine = clickedLine;
            caretPos = 0;
            return;
        }
        float px = x + padding, py = y + padding;
        int clickedIndex = 0;
        Vec2F stringBounds = GuiUtil.stringHorizontalBounds(vg, px, py, lineText, lineText.length());
        if (mouse.getX() >= stringBounds.getY()) {
            clickedIndex = lineText.length();
        } else if (mouse.getX() > stringBounds.getX()) {
            for (int i = 0; i <= lineText.length(); i++) {
                if (mouse.getX() < GuiUtil.substringWidth(vg, px, py, lineText, i)) {
                    clickedIndex = i;
                    break;
                }
            }
        }
        caretLine = clickedLine;
        caretPos = clickedIndex;
    }

    public void onKey(int key) {
        if (!focused) return;
        StringBuilder line = lines.get(caretLine);
        switch (key) {
            case GLFW_KEY_BACKSPACE -> {
                if (caretPos > 0) {
                    line.deleteCharAt(caretPos - 1);
                    caretPos--;
                } else if (caretLine > 0) {
                    caretPos = lines.get(caretLine - 1).length();
                    lines.get(caretLine - 1).append(line);
                    lines.remove(caretLine);
                    caretLine--;
                }
            }
            case GLFW_KEY_LEFT -> {
                if (caretPos > 0) {
                    caretPos--;
                } else if (caretLine > 0) {
                    caretLine--;
                    caretPos = lines.get(caretLine).length();
                }
            }
            case GLFW_KEY_RIGHT -> {
                if (caretPos < line.length()) {
                    caretPos++;
                } else if (caretLine + 1 < lines.size()) {
                    caretLine++;
                    caretPos = 0;
                }
            }
            case GLFW_KEY_UP -> {
                if (caretLine > 0) {
                    caretLine--;
                    caretPos = Math.min(caretPos, lines.get(caretLine).length());
                }
            }
            case GLFW_KEY_DOWN -> {
                if (caretLine + 1 < lines.size()) {
                    caretLine++;
                    caretPos = Math.min(caretPos, lines.get(caretLine).length());
                }
            }
            case GLFW_KEY_ENTER -> onChar('\n');
        }
    }

    public void onChar(char c) {
        if (!focused) return;
        StringBuilder line = lines.get(caretLine);
        if (c == '\n') {
            StringBuilder newLine = new StringBuilder(line.substring(caretPos));
            line.delete(caretPos, line.length());
            lines.add(caretLine + 1, newLine);
            caretLine++;
            caretPos = 0;
        } else {
            line.insert(caretPos, c);
            caretPos++;
        }
        if (onEdit != null)
            onEdit.accept(this);
    }

    private boolean isMouseOver(float mx, float my) {
        if (centered) {
            float x = (Main.windowWidth - width) / 2f;
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}

