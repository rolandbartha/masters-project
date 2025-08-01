package net.codemarked.masters.render.ui;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Config;
import net.codemarked.masters.Engine;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.Mouse;
import net.codemarked.masters.render.event.type.CharInputEvent;
import net.codemarked.masters.render.event.type.KeyboardEvent;
import net.codemarked.masters.render.ui.custom.RulesMenu;
import net.codemarked.masters.render.ui.custom.SidebarContent;
import net.codemarked.masters.render.ui.types.*;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Direction;
import org.lwjgl.nanovg.NVGColor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class Menu {
    private final long vg;
    @Getter
    private float width, height;
    private final List<Element<?>> elements;
    @Getter
    @Setter
    private TextBox focusedTextBox;
    private String title;

    public Menu(float width, float height) {
        this.width = width;
        this.height = height;
        elements = new CopyOnWriteArrayList<>();
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == 0) {
            throw new RuntimeException("Could not init NanoVG");
        }
        init(Engine.State.MENU);
    }

    public void updateAspectRatio(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void unfocusOther(Element<?> element) {
        elements.forEach(element1 -> {
            if (element != element1) element1.unfocus();
        });
        if (focusedTextBox != element)
            focusedTextBox = null;
    }

    public void loadFont(String fontName, String fontPath) {
        int font = nvgCreateFont(vg, fontName, fontPath);
        if (font == -1) {
            throw new RuntimeException("Could not load font: " + fontPath);
        }
    }

    public void init(Engine.State state) {
        elements.clear();
        switch (state) {
            case MENU -> {
                title = "Langton's Ant 3d";
                elements.add(new Button("Start", (self) -> Main.getEngine().setState(Engine.State.GAME))
                        .fontSize(24)
                        .position(0, 200)
                        .size(200, 50)
                        .centered(true));
                elements.add(new Button("Settings", (self) -> Main.getEngine().setState(Engine.State.SETTINGS))
                        .fontSize(24)
                        .position(0, 270)
                        .size(200, 50)
                        .centered(true));
                elements.add(new Button("Key binds", (self) -> Main.getEngine().setState(Engine.State.KEYBINDS))
                        .fontSize(24)
                        .position(0, 340)
                        .size(200, 50)
                        .centered(true));
                elements.add(new Button("Exit", (self) -> Main.close())
                        .fontSize(24)
                        .position(0, 410)
                        .size(200, 50)
                        .centered(true));
            }
            case PAUSE -> {
                title = "Paused";
                elements.add(new Button("Continue", (self) -> Main.getEngine().setState(Engine.State.GAME))
                        .fontSize(24)
                        .position(0, 200)
                        .size(200, 50)
                        .centered(true));
                elements.add(new Button("Main menu", (self) -> Main.getEngine().reset())
                        .fontSize(24)
                        .position(0, 270)
                        .size(200, 50)
                        .centered(true));
            }
            case KEYBINDS -> {
                title = "Key Binds";
                elements.add(new Label("ESC - back/pause")
                        .fontSize(24)
                        .position(0, 200)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("F3 - debug screen")
                        .fontSize(24)
                        .position(0, 240)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("F - freeze ant")
                        .fontSize(24)
                        .position(0, 280)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("WASD - movement")
                        .fontSize(24)
                        .position(0, 320)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("SPACE - ascend")
                        .fontSize(24)
                        .position(0, 360)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("L-SHIFT - descend")
                        .fontSize(24)
                        .position(0, 400)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Label("L-CTRL - toggle/hold sprint")
                        .fontSize(24)
                        .position(0, 440)
                        .size(300, 30)
                        .centered(true));
                elements.add(new Button("Main menu", (self) -> Main.getEngine().setState(Engine.State.MENU))
                        .fontSize(24)
                        .position(0, 520)
                        .size(300, 30)
                        .centered(true));
            }
            case SETTINGS -> {
                title = "Settings";
                int sidebarWidth = 150;
                SidebarContent sidebarContent = (SidebarContent) new SidebarContent()
                        .position(200, 200)
                        .size(150, 30);
                int startX = 200 + sidebarWidth + Element.SEPARATOR;
                int startY = 200;
                RulesMenu rulesMenu = (RulesMenu) new RulesMenu()
                        .position(startX, startY)
                        .size(500, 30);
                rulesMenu.init();
                sidebarContent.addCategory(rulesMenu);

                Panel simulationPanel = new Panel().label("Simulation").visible(false);

                simulationPanel.add(new Checkbox("V-Sync", Main::setVsync)
                        .checked(Main.isVsync())
                        .fontSize(24)
                        .position(startX, startY)
                        .size(500, 30));

                simulationPanel.add(new Checkbox("Multicolor blocks",
                        (self) -> Config.multicolorBlocks = self)
                        .checked(Config.multicolorBlocks)
                        .fontSize(24)
                        .position(startX, startY + (30 + Element.SEPARATOR))
                        .size(500, 30));

                Panel tickDuration = new Panel();
                tickDuration.add(new Label("Tick duration: ")
                        .fontSize(24)
                        .position(startX, startY + 2 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                Dropdown tickDurationDropdown = new Dropdown()
                        .position(startX + 252, startY + 2 * (30 + Element.SEPARATOR))
                        .size(248, 30);
                for (int value : List.of(1, 5, 10, 25, 50, 250, 500, 1000, 2000, 5000)) {
                    tickDurationDropdown.add(value + "ms", (self) -> Config.tickFrequencyMillis = value, Config.tickFrequencyMillis == value);
                }
                tickDuration.add(tickDurationDropdown);
                simulationPanel.add(tickDuration);

                Panel tickSteps = new Panel();
                tickSteps.add(new Label("Steps per tick: ")
                        .fontSize(24)
                        .position(startX, startY + 3 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                Dropdown tickStepsDropdown = new Dropdown()
                        .position(startX + 252, startY + 3 * (30 + Element.SEPARATOR))
                        .size(248, 30);
                for (int value : List.of(1, 10, 50, 100, 500, 1000, 5000, 10000, 50000, 100000, 1000000)) {
                    tickStepsDropdown.add(String.valueOf(value), (self) -> Config.stepsPerTick = value, Config.stepsPerTick == value);
                }
                tickSteps.add(tickStepsDropdown);
                simulationPanel.add(tickSteps);

                Panel direction = new Panel();
                direction.add(new Label("Start direction: ")
                        .fontSize(24)
                        .position(startX, startY + 4 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                Dropdown directionDropdown = new Dropdown()
                        .position(startX + 252, startY + 4 * (30 + Element.SEPARATOR))
                        .size(248, 30);
                for (Direction direction1 : Direction.values()) {
                    directionDropdown.add(direction1.toString(), (self) -> Config.startingDirection = direction1, direction1 == Config.startingDirection);
                }
                direction.add(directionDropdown);
                simulationPanel.add(direction);

                Panel movementPanel = new Panel();
                movementPanel.add(new Label("Movement speed: ")
                        .fontSize(24)
                        .position(startX, startY + 5 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                movementPanel.add(new DragBar(self -> Config.movementSpeed = self.getValue())
                        .value(Config.movementSpeed)
                        .increment(0.1f)
                        .min(0.1f)
                        .position(startX + 252, startY + 5 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                simulationPanel.add(movementPanel);

                Panel fovPanel = new Panel();
                fovPanel.add(new Label("Field of view: ")
                        .fontSize(24)
                        .position(startX, startY + 6 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                fovPanel.add(new DragBar(self -> Config.fieldOfView = self.getValue())
                        .value(Config.fieldOfView)
                        .increment(0.1f)
                        .position(startX + 252, startY + 6 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                simulationPanel.add(fovPanel);

                Panel sensitivityPanel = new Panel();
                sensitivityPanel.add(new Label("Mouse sensitivity: ")
                        .fontSize(24)
                        .position(startX, startY + 7 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                sensitivityPanel.add(new DragBar(self -> Config.mouseSensitivity = self.getValue())
                        .value(Config.mouseSensitivity)
                        .increment(0.1f)
                        .min(0.1f)
                        .position(startX + 252, startY + 7 * (30 + Element.SEPARATOR))
                        .size(250, 30));
                simulationPanel.add(sensitivityPanel);

                sidebarContent.addCategory(simulationPanel);
                elements.add(sidebarContent);
            }
        }
    }

    public void open(Element<?> element) {
        elements.clear();
        elements.add(element);
    }

    public void render(Mouse mouse) {
        nvgBeginFrame(vg, width, height, 1);

        nvgFontFace(vg, "default");
        nvgFontSize(vg, 48.0f);
        nvgTextAlign(vg, NVG_ALIGN_CENTER);
        try (NVGColor color = GuiUtil.rgba(255, 255, 255, 255)) {
            nvgFillColor(vg, color);
        }
        nvgText(vg, width / 2f, 100, title);
        for (Element<?> element : elements) {
            element.render(vg, mouse);
        }
        nvgEndFrame(vg);
    }

    public void update(Mouse mouse) {
        for (Element<?> element : elements) {
            element.update(vg, mouse);
        }
    }

    public void cleanup() {
        elements.clear();
        nvgDelete(vg);
    }

    public void onKey(KeyboardEvent event) {
        if (focusedTextBox == null) return;
        focusedTextBox.onKey(event.getKey());
    }

    public void onChar(CharInputEvent event) {
        if (focusedTextBox == null) return;
        focusedTextBox.onChar(event.getCharacter());
    }
}