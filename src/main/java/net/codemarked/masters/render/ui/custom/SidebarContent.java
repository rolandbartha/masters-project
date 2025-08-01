package net.codemarked.masters.render.ui.custom;

import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.render.ui.types.*;
import org.apache.logging.log4j.util.Strings;

public class SidebarContent extends Panel {
    private final Panel sidebar = new Panel();
    private final Panel content = new Panel();
    private Category activeCategory = null;

    @Override
    public boolean isFocused() {
        return activeCategory != null
                && activeCategory.isFocused();
    }

    @Override
    public void unfocus() {
        if (activeCategory != null)
            activeCategory.unfocus();
    }

    public void addCategory(Panel childPanel) {
        float buttonY = y + getChildren().size() * (height + SEPARATOR);
        Button button = new Button(childPanel.isHasLabel() ? childPanel.getLabel() : Strings.EMPTY, (self) -> setActiveCategory(childPanel.getLabel()))
                .fontSize(24)
                .position(x, buttonY)
                .size(width, height);

        Category category = new Category(childPanel.isHasLabel() ? childPanel.getLabel() : Strings.EMPTY, button, childPanel);
        getChildren().add(category);
        sidebar.add(button);

        childPanel.setVisible(false);
        content.add(childPanel);

        if (activeCategory == null) {
            setActiveCategory(childPanel.getLabel());
        }
    }

    private void setActiveCategory(String name) {
        for (Element<?> element : getChildren()) {
            if (element instanceof Category category) {
                boolean isActive = category.name.equals(name);
                category.panel.setVisible(isActive);
                category.button.setActive(isActive);
                if (isActive) activeCategory = category;
            }
        }
    }

    @Override
    public void render(long vg, Mouse mouse) {
        sidebar.render(vg, mouse);
        content.render(vg, mouse);
    }

    @Override
    public void update(long vg, Mouse mouse) {
        sidebar.update(vg, mouse);
        content.update(vg, mouse);
    }

    private static class Category extends Element<Category> {
        String name;
        Button button;
        Panel panel;

        Category(String name, Button button, Panel panel) {
            this.name = name;
            this.button = button;
            this.panel = panel;
        }

        @Override
        public boolean isFocused() {
            return panel.isFocused();
        }

        @Override
        public void unfocus() {
            panel.unfocus();
        }

        @Override
        public void render(long vg, Mouse mouse) {
        }

        @Override
        public void update(long vg, Mouse mouse) {
        }
    }
}
