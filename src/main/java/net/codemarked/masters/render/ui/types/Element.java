package net.codemarked.masters.render.ui.types;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.render.Mouse;

@Getter
@Setter
public abstract class Element<T extends Element<T>> {
    public static final int SEPARATOR = 10;
    protected boolean centered, visible = true;
    protected float x, y, width, height;

    public abstract void render(long vg, Mouse mouse);

    public abstract void update(long vg, Mouse mouse);

    public T position(float x, float y) {
        this.x = x;
        this.y = y;
        return (T) this;
    }

    public T size(float width, float height) {
        this.width = width;
        this.height = height;
        return (T) this;
    }

    public T visible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public T centered(boolean centered) {
        this.centered = centered;
        return (T) this;
    }

    public void unfocus() {
    }

    public boolean isFocused() {
        return false;
    }

}
