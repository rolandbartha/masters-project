package net.codemarked.masters.render.event.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.codemarked.masters.Engine;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.event.Event;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

@Getter
@AllArgsConstructor
public class MouseEvent implements Event {

    public enum Type {PRESS, RELEASE, MOVE, SCROLL}

    private final Type type;
    private final int button;
    private final double x, y;
    private final double scroll;

    @Override
    public void process() {
        Engine engine = Main.getEngine();
        switch (type) {
            case PRESS -> {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    engine.getMouse().onMouseButton(true);
                }
            }
            case RELEASE -> {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    engine.getMouse().onMouseButton(false);
                }
            }
            case MOVE -> {
                if (engine.getState() == Engine.State.GAME) {
                    engine.getCamera().onMouseMove((float) x, (float) y);
                } else {
                    engine.getMouse().onMouseMove((float) x, (float) y);
                }
            }
            case SCROLL -> engine.getMouse().onMouseScroll((float) scroll);
        }
    }
}