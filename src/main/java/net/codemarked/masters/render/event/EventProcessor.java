package net.codemarked.masters.render.event;

import lombok.Getter;
import net.codemarked.masters.render.event.type.CharInputEvent;
import net.codemarked.masters.render.event.type.KeyboardEvent;
import net.codemarked.masters.render.event.type.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

@Getter
public class EventProcessor {
    private final List<Event> events = new ArrayList<>();

    public void process() {
        events.forEach(Event::process);
    }

    public void flush() {
        events.clear();
    }

    public void onMouseMove(long window, double x, double y) {
        events.add(new MouseEvent(MouseEvent.Type.MOVE, -1, x, y, 0));
    }

    public void onMouseScroll(long window, double offsetX, double offsetY) {
        events.add(new MouseEvent(MouseEvent.Type.SCROLL, -1, 0, 0, offsetY));
    }

    public void onMouseButton(long window, int button, int action, int mods) {
        MouseEvent.Type type = action == GLFW_PRESS ? MouseEvent.Type.PRESS : MouseEvent.Type.RELEASE;
        events.add(new MouseEvent(type, button, 0, 0, 0));
    }

    public void onKey(long window, int key, int scancode, int action, int mods) {
        KeyboardEvent.Type type = switch (action) {
            case GLFW_PRESS -> KeyboardEvent.Type.PRESS;
            case GLFW_RELEASE -> KeyboardEvent.Type.RELEASE;
            case GLFW_REPEAT -> KeyboardEvent.Type.REPEAT;
            default -> null;
        };
        if (type != null) {
            events.add(new KeyboardEvent(type, key));
        }
    }

    public void onChar(long window, int codepoint) {
        events.add(new CharInputEvent((char) codepoint));
    }
}