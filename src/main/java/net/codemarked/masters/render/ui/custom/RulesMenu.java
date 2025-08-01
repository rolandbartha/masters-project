package net.codemarked.masters.render.ui.custom;

import net.codemarked.masters.Engine;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.simulation.rule.RuleSet;
import net.codemarked.masters.render.ui.types.*;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Vec4I;
import org.lwjgl.nanovg.NVGColor;

import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.*;

public class RulesMenu extends Panel {
    private Label selectedLabel;
    private Element<?> addButton, backButton;
    private Button selectedButton;

    private float scrollOffset = 0;

    private static final int MAX_CONTENT_UNTIL_SCROLL = 5;

    public RulesMenu() {
        label("RuleSets");
    }

    public void init() {
        selectedLabel = new Label(Main.getEngine().getRuleManager().getSelectedName(), Vec4I.of(50, 50, 50, 255))
                .fontSize(24)
                .position(x, y)
                .size(width, height);
        addButton = new Button("Add RuleSet", this::addItemPrompt)
                .fontSize(24)
                .position(x, y)
                .size(width, height);
        backButton = new Button("Back", (self) -> Main.getEngine().setState(Engine.State.MENU))
                .fontSize(24)
                .position(x, y + height + SEPARATOR)
                .size(width, height);
        Main.getEngine().getRuleManager().getRuleSets().forEach(this::addItem);
    }

    private void addItemPrompt(Button button) {
        RuleSet ruleSet = new RuleSet();
        Main.getEngine().getRuleManager().add(ruleSet);
        addItem(ruleSet);
        Main.getEngine().getRuleManager().save();
    }

    private void addItem(RuleSet ruleSet) {
        float rowY = y + (getChildren().size() + 1) * (height + SEPARATOR) - scrollOffset;
        add(((RuleSetRow) new RuleSetRow()
                .position(x, rowY)
                .size(width, height))
                .init(ruleSet, this::editItem, this::removeItem));
        updateButtonsHeight();
    }

    private void editItem(RuleSetRow item) {
        int sidebarWidth = 150;
        int startX = 200 + sidebarWidth + SEPARATOR;
        Main.getEngine().getMenu().open(((RuleSetMenu) new RuleSetMenu()
                .position(startX, 200).size(width, height))
                .init(item.ruleSet));
    }

    private void removeItem(RuleSetRow item) {
        Main.getEngine().getRuleManager().remove(item.ruleSet);
        remove(item);
        for (int i = 0; i < getChildren().size(); i++) {
            Element<?> element = getChildren().get(i);
            if (element instanceof RuleSetRow row) {
                row.update(x, y + (i + 1) * (height + SEPARATOR) - scrollOffset);
            }
        }
        updateButtonsHeight();
        updateSelectedButton();
        Main.getEngine().getRuleManager().save();
    }

    private void updateButtonsHeight() {
        float buttonHeight = y + Math.min(getChildren().size(), MAX_CONTENT_UNTIL_SCROLL) * (height + SEPARATOR);
        addButton.setY(buttonHeight + height + SEPARATOR);
        backButton.setY(buttonHeight + 2 * (height + SEPARATOR));
    }

    private void updateSelectedButton() {
        selectedLabel.setText(Main.getEngine().getRuleManager().getSelectedName());
    }

    @Override
    public void render(long vg, Mouse mouse) {
        if (!visible) return;
        nvgScissor(vg, x, y + (height + SEPARATOR), width, Math.min(getChildren().size(), MAX_CONTENT_UNTIL_SCROLL) * (height + SEPARATOR));
        nvgSave(vg);
        getChildren().stream()
                .filter(Element::isVisible)
                .forEach(child -> child.render(vg, mouse));
        nvgRestore(vg);
        nvgResetScissor(vg);
        selectedLabel.render(vg, mouse);
        addButton.render(vg, mouse);
        backButton.render(vg, mouse);
        if (getChildren().size() <= MAX_CONTENT_UNTIL_SCROLL) return;
        float panelHeight = MAX_CONTENT_UNTIL_SCROLL * (height + SEPARATOR);
        float contentHeight = getChildren().size() * (height + SEPARATOR);

        float barHeight = Math.max(20, (panelHeight / contentHeight) * panelHeight);

        float barY = (scrollOffset / (contentHeight - panelHeight)) * (panelHeight - barHeight);

        nvgBeginPath(vg);
        nvgRect(vg, x + width + 2, y + height + SEPARATOR, 6, panelHeight);
        try (NVGColor color = GuiUtil.rgba(50, 50, 50, 200)) {
            nvgFillColor(vg, color);
            nvgFill(vg);
        }

        nvgBeginPath(vg);
        nvgRect(vg, x + width + 2, y + height + SEPARATOR + barY, 6, barHeight);
        try (NVGColor color = GuiUtil.rgba(200, 200, 200, 200)) {
            nvgFillColor(vg, color);
            nvgFill(vg);
        }
    }

    @Override
    public void update(long vg, Mouse mouse) {
        if (!visible) return;
        float deltaOffset;
        if (mouse.getScrollOffset() == 0) {
            deltaOffset = 0;
        } else {
            float contentHeight = getChildren().size() * (height + SEPARATOR);
            float scrollableHeight = MAX_CONTENT_UNTIL_SCROLL * (height + SEPARATOR);
            float previousOffset = scrollOffset;
            scrollOffset = Math.max(0,
                    Math.min(scrollOffset - mouse.getScrollOffset() * 15, contentHeight - scrollableHeight));
            deltaOffset = scrollOffset - previousOffset;
        }
        selectedLabel.update(vg, mouse);
        addButton.update(vg, mouse);
        backButton.update(vg, mouse);
        getChildren().stream()
                .filter(Element::isVisible)
                .forEach(child -> {
                    if (deltaOffset != 0 && child instanceof RuleSetRow row) {
                        row.update(x, row.getY() - deltaOffset);
                    }
                    child.update(vg, mouse);
                });
    }

    private class RuleSetRow extends Panel {

        public RuleSet ruleSet;

        public RuleSetRow init(RuleSet ruleSet, Consumer<RuleSetRow> onEdit, Consumer<RuleSetRow> onDelete) {
            this.ruleSet = ruleSet;
            float componentWidth = width / 5;
            float labelWidth = width - (componentWidth + 2) * 2;
            Button button = new Button(ruleSet.getName(),
                    (self) -> {
                        if (selectedButton != null) {
                            selectedButton.setActive(false);
                        }
                        Main.getEngine().getRuleManager().setSelected(ruleSet);
                        if (selectedLabel != null) {
                            updateSelectedButton();
                        }
                        selectedButton = self;
                        self.setActive(true);
                    })
                    .fontSize(24)
                    .position(x, y)
                    .size(labelWidth, height);
            if (Main.getEngine().getRuleManager().getSelected() == ruleSet) {
                button.setActive(true);
                selectedButton = button;
            }
            this.add(button);
            this.add(new Button("Edit", (self) -> onEdit.accept(this))
                    .fontSize(16)
                    .position(x + labelWidth + 2, y)
                    .size(componentWidth, height));
            this.add(new Button("Delete", (self) -> onDelete.accept(this))
                    .fontSize(16)
                    .position(x + labelWidth + componentWidth + 4, y)
                    .size(componentWidth, height));
            return this;
        }

        public void update(float x, float y) {
            setX(x);
            setY(y);
            getChildren().forEach(child -> child.setY(y));
        }
    }
}
