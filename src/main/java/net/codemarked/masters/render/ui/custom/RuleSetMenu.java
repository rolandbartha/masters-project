package net.codemarked.masters.render.ui.custom;

import net.codemarked.masters.Engine;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.simulation.rule.Block;
import net.codemarked.masters.simulation.rule.Rotation;
import net.codemarked.masters.simulation.rule.Rule;
import net.codemarked.masters.simulation.rule.RuleSet;
import net.codemarked.masters.render.ui.types.*;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Vec4I;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;

public class RuleSetMenu extends Panel {
    private Label nameLabel;
    private RuleSet ruleSet;
    private Element<?> addButton, backButton;
    private TextBox textBox;

    private float scrollOffset = 0;

    private static final int MAX_CONTENT_UNTIL_SCROLL = 5;

    public RuleSetMenu init(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
        String name = ruleSet.getName();
        nameLabel = new Label("Name: " + (name.isEmpty() ? "N/A" : name), Vec4I.of(50, 50, 50, 255))
                .fontSize(24)
                .position(x, y)
                .size(width, height);
        addButton = new Button("Add Rule", this::addItemPrompt)
                .fontSize(24)
                .position(x, y + height + SEPARATOR)
                .size(width, height);
        backButton = new Button("Back", (self) -> {
            Main.getEngine().getRuleManager().save();
            Main.getEngine().setState(Engine.State.SETTINGS);
        })
                .fontSize(24)
                .position(x, y + 2 * (height + SEPARATOR))
                .size(width, height);
        textBox = new TextBox().fontSize(24)
                .text(ruleSet.getNotes())
                .onEdit(self -> ruleSet.setNotes(self.getText()))
                .placeholder("Add your notes here...")
                .position(x, y + 3 * (height + SEPARATOR))
                .size(width, height * 5);
        for (int i = 0; i < ruleSet.getRules().size(); i++) {
            addItem(i, ruleSet.getRules().get(i));
        }
        return this;
    }

    private void updateNameLabel() {
        String name = ruleSet.getName();
        nameLabel.setText("Name: " + (name.isEmpty() ? "N/A" : name));
    }

    private void addItemPrompt(Button button) {
        int rulesPresent = getChildren().size();
        if (Block.get(rulesPresent) == null) return;
        Rule rule = new Rule();
        if (ruleSet.getRules() instanceof ArrayList<Rule> rules) {
            rules.add(rule);
        } else {
            List<Rule> rules = new ArrayList<>(ruleSet.getRules());
            rules.add(rule);
            ruleSet.setRules(rules);
        }
        addItem(rulesPresent, rule);
        updateNameLabel();
        Main.getEngine().getRuleManager().save();
    }

    private void addItem(int i, Rule rule) {
        float rowY = y + (getChildren().size() + 1) * (height + SEPARATOR) - scrollOffset;
        add(((RuleRow) new RuleRow()
                .position(x, rowY)
                .size(width, height))
                .init(i, rule, this::removeItem));
        updateButtonsHeight();
    }

    private void removeItem(RuleRow item) {
        remove(item);
        if (ruleSet.getRules() instanceof ArrayList<Rule> rules) {
            rules.remove(item.rule);
        } else {
            List<Rule> rules = new ArrayList<>(ruleSet.getRules());
            rules.remove(item.rule);
            ruleSet.setRules(rules);
        }
        for (int i = 0; i < getChildren().size(); i++) {
            Element<?> element = getChildren().get(i);
            if (element instanceof RuleRow row) {
                row.update(i, x, y + (i + 1) * (height + SEPARATOR) - scrollOffset);
            }
        }
        updateButtonsHeight();
        updateNameLabel();
        Main.getEngine().getRuleManager().save();
    }

    private void updateButtonsHeight() {
        float buttonHeight = y + Math.min(getChildren().size(), MAX_CONTENT_UNTIL_SCROLL) * (height + SEPARATOR);
        addButton.setY(buttonHeight + height + SEPARATOR);
        backButton.setY(buttonHeight + 2 * (height + SEPARATOR));
        textBox.setY(buttonHeight + 3 * (height + SEPARATOR));
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
        nameLabel.render(vg, mouse);
        addButton.render(vg, mouse);
        backButton.render(vg, mouse);
        textBox.render(vg, mouse);
        if (getChildren().size() <= MAX_CONTENT_UNTIL_SCROLL) return;

        float panelHeight = MAX_CONTENT_UNTIL_SCROLL * (height + SEPARATOR);
        float contentHeight = getChildren().size() * (height + SEPARATOR);
        float barHeight = Math.max(20, (panelHeight / contentHeight) * panelHeight);
        float barY = scrollOffset / (contentHeight - panelHeight) * (panelHeight - barHeight);

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
        nameLabel.update(vg, mouse);
        addButton.update(vg, mouse);
        backButton.update(vg, mouse);
        textBox.update(vg, mouse);
        getChildren().stream()
                .filter(Element::isVisible)
                .forEach(child -> {
                    if (deltaOffset != 0 && child instanceof RuleRow row) {
                        row.update(x, row.getY() - deltaOffset);
                    }
                    child.update(vg, mouse);
                });
    }

    private class RuleRow extends Panel {

        private Rule rule;
        private ColorLabel colorLabel;

        public RuleRow init(int i, Rule rule, Consumer<RuleRow> onDelete) {
            this.rule = rule;
            float buttonWidth = width / 10;
            float componentWidth = width - (buttonWidth + 2) * (Rotation.values().length + 1);
            Block block = Block.get(i);
            colorLabel = new ColorLabel(Vec4I.of(block.getR(), block.getG(), block.getB(), 255))
                    .position(x, y)
                    .size(buttonWidth, height);
            add(colorLabel);
            int count = 1;
            for (Rotation rotation : Rotation.values()) {
                add(new Button(rotation.toString().substring(0, 1),
                        (self) -> {
                            rule.setRotation(rotation.getId());
                            processRotation(rotation);
                            updateNameLabel();
                        })
                        .fontSize(16)
                        .active(rule.getRotation() == rotation.getId())
                        .position(x + (buttonWidth + 2) * count, y)
                        .size(buttonWidth, height));
                count++;
            }
            add(new Button("Delete", (self) -> onDelete.accept(this))
                    .fontSize(16)
                    .position(x + (buttonWidth + 2) * count, y)
                    .size(componentWidth, height));
            return this;
        }

        public void update(int i, float x, float y) {
            Block block = Block.get(i);
            colorLabel.setColor(Vec4I.of(block.getR(), block.getG(), block.getB(), 255));
            setX(x);
            setY(y);
            getChildren().forEach(child -> child.setY(y));
        }

        public void update(float x, float y) {
            setX(x);
            setY(y);
            getChildren().forEach(child -> child.setY(y));
        }

        public void processRotation(Rotation rotation) {
            getChildren().stream()
                    .filter(element -> element instanceof Button)
                    .map(element -> (Button) element)
                    .forEach(button -> button.setActive(button.getLabel().equals(rotation.toString().substring(0, 1))));
        }
    }
}
