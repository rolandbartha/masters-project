package net.codemarked.masters.render.event.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.codemarked.masters.Engine;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.event.Event;

import static org.lwjgl.glfw.GLFW.*;

@Getter
@AllArgsConstructor
public class KeyboardEvent implements Event {

    public enum Type {PRESS, RELEASE, REPEAT}

    private final Type type;
    private final int key;

    @Override
    public void process() {
        Engine engine = Main.getEngine();
        switch (engine.getState()) {
            case SETTINGS, KEYBINDS -> {
                if (key == GLFW_KEY_ESCAPE && type == Type.PRESS) {
                    engine.setState(engine.getLastState());
                } else if (type == Type.PRESS || type == Type.REPEAT) {
                    engine.getMenu().onKey(this);
                }
            }
            case GAME -> {
                if (type == Type.PRESS) {
                    switch (key) {
                        case GLFW_KEY_ESCAPE -> engine.setState(Engine.State.PAUSE);
                        case GLFW_KEY_LEFT_CONTROL -> engine.setSprint(!engine.isSprint());
                        case GLFW_KEY_F -> engine.setFreeze(!engine.isFreeze());
                        case GLFW_KEY_F3 -> engine.setDebugScreen(!engine.isDebugScreen());
                    }
                }
            }
            case PAUSE -> {
                if (key == GLFW_KEY_ESCAPE && type == Type.PRESS) {
                    engine.setState(Engine.State.GAME);
                }
            }
        }
    }
}
