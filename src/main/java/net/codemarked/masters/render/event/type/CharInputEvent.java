package net.codemarked.masters.render.event.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.codemarked.masters.Main;
import net.codemarked.masters.render.event.Event;

@Getter
@AllArgsConstructor
public class CharInputEvent implements Event {

    private final char character;

    @Override
    public void process() {
        Main.getEngine().getMenu().onChar(this);
    }
}