package net.codemarked.masters;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.render.*;
import net.codemarked.masters.render.event.EventProcessor;
import net.codemarked.masters.simulation.rule.RuleManager;
import net.codemarked.masters.render.programs.Shader;
import net.codemarked.masters.render.programs.TextRenderer;
import net.codemarked.masters.render.ui.Menu;
import net.codemarked.masters.simulation.Scene;
import net.codemarked.masters.util.IOUtil;
import net.codemarked.masters.util.math.Mat4F;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.*;

@Getter
public class Engine {

    public enum State {
        MENU, GAME, PAUSE, SETTINGS, KEYBINDS
    }

    private final long window;

    private final EventProcessor eventProcessor;
    private final RuleManager ruleManager;

    private final Camera camera;
    private final Mouse mouse;

    private final Shader mainShader;
    private final Crosshair crosshair;
    private final TextRenderer textRenderer;

    private final Menu menu;
    private final Scene scene;

    private final Information information;
    @Getter
    private State state, lastState;
    @Setter
    private boolean freeze, sprint, debugScreen;

    public Engine(long window, float width, float height) {
        this.window = window;
        information = new Information();
        lastState = state = State.MENU;
        eventProcessor = new EventProcessor();
        ruleManager = new RuleManager();
        ruleManager.init();
        crosshair = new Crosshair(width, height);
        scene = new Scene();
        String fontPath = IOUtil.getAbsolutePath("fonts/Roboto-Regular.ttf");
        menu = new Menu(width, height);
        menu.loadFont("default", fontPath);
        textRenderer = new TextRenderer(width, height);
        textRenderer.loadFont("default", fontPath);
        camera = new Camera(width / height);
        mouse = new Mouse(width, height);
        mainShader = new Shader("shaders/main.vs", "shaders/main.fs");
    }

    public void setState(State newState) {
        lastState = state;
        state = newState;
        menu.init(state);
        if (state == State.GAME) {
            camera.updateSettings();
            scene.getAnt().setRuleSet(ruleManager.getSelected());
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            glfwSetCursorPos(window, camera.getLastXMove(), camera.getLastYMove());
        } else if (lastState == State.GAME) {
            mouse.center();
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetCursorPos(window, Main.windowWidth / 2f, Main.windowHeight / 2f);
        }
    }

    public void init() {
        glEnable(GL_BLEND);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LINE_SMOOTH);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mainShader.use();
    }

    public void setViewport(float width, float height) {
        menu.updateAspectRatio(width, height);
        textRenderer.setWidth(width);
        textRenderer.setHeight(height);
        camera.updateAspectRatio(width / height);
        crosshair.updateAspectRatio(width, height);
    }

    public void reset() {
        information.setTicks(0);
        freeze = sprint = false;
        setState(State.MENU);
        scene.cleanup();
        scene.init();
        camera.init();
    }

    public void process() {
        eventProcessor.process();
        if (state != State.GAME) return;
        camera.onKeyboard(window, information.getDeltaFrameTime(), sprint);
    }

    public void update() {
        if (state == State.GAME) {
            if (freeze) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime - information.getLastUpdateTime() >= Config.tickFrequencyMillis) {
                scene.tick();
                information.tick(currentTime);
            }
        } else {
            menu.update(mouse);
        }
    }

    public void render() {
        if (state == State.GAME) {
            Mat4F view = camera.getView();
            Mat4F projection = camera.getProjection();
            mainShader.use();
            glUniformMatrix4fv(glGetUniformLocation(mainShader.getId(), "view"), false, view.getM());
            glUniformMatrix4fv(glGetUniformLocation(mainShader.getId(), "projection"), false, projection.getM());
            scene.render(mainShader.getId());
            glBindVertexArray(0);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            crosshair.render();
            if (debugScreen) {
                glDisable(GL_BLEND);
                textRenderer.beginFrame();
                textRenderer.text(20, 20, 6, 24,
                        "Fps: " + String.format("%.2f", information.getFramesPerSecond()),
                        "Uptime: " + information.upTime() + " Ticks: " + information.getTicks() + " Freeze: " + freeze,
                        "Regions: " + scene.getRegions().size() + "/" + Scene.REGION_COUNT,
                        "Ant: " + scene.getAnt().getDebugInfo(),
                        "Rule: " + scene.getAnt().getRuleSet().getName(),
                        "Camera: " + camera.getDebugInfo(),
                        "Resolution: " + Main.windowWidth + "/" + Main.windowHeight,
                        "Sprinting: " + sprint,
                        "Block: " + scene.getBlockAtCamera(camera.getSceneCoords())
                );
                textRenderer.endFrame();
                glEnable(GL_BLEND);
            }
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CW);
        } else {
            menu.render(mouse);
        }
        eventProcessor.flush();
        mouse.onTickEnd();
    }

    public void cleanup() {
        textRenderer.cleanup();
        mainShader.cleanup();
        crosshair.cleanup();
        scene.cleanup();
        menu.cleanup();
    }
}