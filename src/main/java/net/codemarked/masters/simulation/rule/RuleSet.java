package net.codemarked.masters.simulation.rule;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class RuleSet {
    private List<Rule> rules;
    private String notes;

    public RuleSet() {
        this.rules = new ArrayList<>(3);
    }

    public RuleSet(List<Rule> rules) {
        this.rules = rules;
    }

    public String getName() {
        return rules.stream()
                .mapToInt(Rule::getRotation)
                .mapToObj(Rotation::get)
                .map(Rotation::getFirstLetter)
                .collect(Collectors.joining());
    }
}