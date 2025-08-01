package net.codemarked.masters.simulation.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RuleManager {
    private final List<RuleSet> ruleSets = new ArrayList<>();
    @Setter
    private RuleSet selected;

    public void init() {
        load();
        if (ruleSets.isEmpty()) {
            add(new RuleSet(List.of(
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.LEFT)
            )));
            add(new RuleSet(List.of(
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.RIGHT)
            )));
            add(new RuleSet(List.of(
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.RIGHT)
            )));
            add(new RuleSet(List.of(
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.RIGHT)
            )));
            add(new RuleSet(List.of(
                    new Rule(Rotation.DOWN),
                    new Rule(Rotation.UP),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.LEFT)
            )));
            add(new RuleSet(List.of(
                    new Rule(Rotation.LEFT),
                    new Rule(Rotation.RIGHT),
                    new Rule(Rotation.FORWARD),
                    new Rule(Rotation.BACKWARD)
            )));
            save();
        }
        setSelected(ruleSets.get(0));
    }

    public String getSelectedName() {
        return "Selected: " + (selected == null ? "N/A" : selected.getName());
    }

    public void add(RuleSet ruleSet) {
        if (selected == null) {
            selected = ruleSet;
        }
        ruleSets.add(ruleSet);
    }

    public void remove(RuleSet ruleSet) {
        if (selected == ruleSet) {
            selected = ruleSets.get(0);
        }
        ruleSets.remove(ruleSet);
    }

    public void load() {
        ruleSets.clear();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<RuleSet>>() {
        }.getType();
        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "langton_save.json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileReader reader = new FileReader(file)) {
            ruleSets.addAll(gson.fromJson(reader, listType));
        } catch (Exception e) {
            Main.LOGGER.error("RuleManager load", e);
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "langton_save.json";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(ruleSets, writer);
        } catch (Exception e) {
            Main.LOGGER.error("RuleManager save", e);
        }
    }
}
