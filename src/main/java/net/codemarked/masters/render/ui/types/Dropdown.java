package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.util.GuiUtil;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.*;

public class Dropdown extends Element<Dropdown> {
    private final List<DropdownItem> items = new ArrayList<>();
    private int selectedIndex, hoveredIndex = -1;
    private boolean expanded = false;

    @Override
    public void unfocus() {
        expanded = false;
    }

    @Override
    public boolean isFocused() {
        return expanded;
    }

    public void add(String label, Consumer<DropdownItem> onSelect) {
        add(label, onSelect, false);
    }

    public void add(String label, Consumer<DropdownItem> onSelect, boolean selected) {
        if (selected) {
            selectedIndex = items.size();
        }
        items.add(new DropdownItem(label, onSelect));
    }

    public void remove(String label) {
        items.removeIf(item -> item.getLabel().equals(label));
    }

    @Override
    public void render(long vg, Mouse mouse) {
        nvgFontSize(vg, 18f);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);

        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        try (NVGColor color = GuiUtil.rgba(50, 50, 50, 255)) {
            nvgFillColor(vg, color);
        }
        nvgFill(vg);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgText(vg, x + 8, y + height / 2, items.get(selectedIndex).label);

        if (expanded) {
            for (int i = 0; i < items.size(); i++) {
                float optY = y + height + i * height;
                nvgBeginPath(vg);
                nvgRect(vg, x, optY, width, height);
                try (NVGColor color = i == hoveredIndex ? GuiUtil.rgba(80, 80, 80, 255) : GuiUtil.rgba(50, 50, 50, 255)) {
                    nvgFillColor(vg, color);
                }
                nvgFill(vg);
                try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
                    nvgFillColor(vg, color);
                }
                nvgText(vg, x + 8, optY + height / 2, items.get(i).label);
            }
        }
    }

    @Override
    public void update(long vg, Mouse mouse) {
        hoveredIndex = -1;
        if (expanded) {
            if (mouse.isPressed() && mouse.resetPress()) {
                Main.getEngine().getMenu().unfocusOther(this);
                expanded = false;
                if (isMouseOver(mouse.getX(), mouse.getY(), y)) return;
                for (int i = 0; i < items.size(); i++) {
                    float optY = y + height + i * height;
                    if (isMouseOver(mouse.getX(), mouse.getY(), optY)) {
                        selectedIndex = i;
                        DropdownItem item = items.get(i);
                        if (item != null) item.onSelect();
                        return;
                    }
                }
            } else {
                for (int i = 0; i < items.size(); i++) {
                    float optY = y + height + i * height;
                    if (isMouseOver(mouse.getX(), mouse.getY(), optY)) {
                        hoveredIndex = i;
                        break;
                    }
                }
            }
        } else if (mouse.isPressed() && isMouseOver(mouse.getX(), mouse.getY(), y) && mouse.resetPress()) {
            Main.getEngine().getMenu().unfocusOther(this);
            expanded = true;
        }
    }

    private boolean isMouseOver(float mx, float my, float y) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    public static class DropdownItem {
        @Getter
        private final String label;
        private final Consumer<DropdownItem> onSelect;

        public DropdownItem(String label, Consumer<DropdownItem> onSelect) {
            this.label = label;
            this.onSelect = onSelect;
        }

        public void onSelect() {
            if (onSelect != null)
                onSelect.accept(this);
        }

        @Override
        public String toString() {
            return label;
        }
    }
}