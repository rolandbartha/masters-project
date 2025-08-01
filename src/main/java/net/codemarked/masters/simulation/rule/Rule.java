package net.codemarked.masters.simulation.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rule {
    private int rotation;

    public Rule() {
        rotation = Rotation.LEFT.getId();
    }

    public Rule(Rotation rotation) {
        this(rotation.getId());
    }
}