package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import net.codemarked.masters.render.Mouse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Panel extends Element<Panel> {
    private String label;
    private boolean hasLabel;
    private final List<Element<?>> children
            = new CopyOnWriteArrayList<>();

    public Panel label(String label) {
        this.label = label;
        hasLabel = true;
        return this;
    }

    @Override
    public void unfocus() {
        children.stream().filter(Element::isFocused).forEach(Element::unfocus);
    }

    @Override
    public boolean isFocused() {
        return children.stream().anyMatch(Element::isFocused);
    }

    public void add(Element<?> e) {
        children.add(e);
    }

    public void remove(Element<?> e) {
        children.remove(e);
    }

    @Override
    public void render(long vg, Mouse mouse) {
        if (!visible) return;
        Element<?> renderLast = null;
        for (Element<?> child : children) {
            if (!child.visible) continue;
            if (renderLast == null && child.isFocused()) {
                renderLast = child;
                continue;
            }
            child.render(vg, mouse);
        }
        if (renderLast != null)
            renderLast.render(vg, mouse);
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (!visible) return;
        children.stream()
                .filter(Element::isVisible)
                .forEach(child -> child.update(vg, mouse));
    }
}
