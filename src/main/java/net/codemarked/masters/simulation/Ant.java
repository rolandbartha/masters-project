package net.codemarked.masters.simulation;

import lombok.Getter;
import lombok.Setter;
import net.codemarked.masters.Config;
import net.codemarked.masters.simulation.rule.Rotation;
import net.codemarked.masters.simulation.rule.Rule;
import net.codemarked.masters.simulation.rule.RuleSet;
import net.codemarked.masters.util.GuiUtil;
import net.codemarked.masters.util.math.Direction;
import net.codemarked.masters.util.math.Vec3I;

@Getter
public class Ant {
    private Vec3I position;
    private Direction facing, up;
    @Setter
    private RuleSet ruleSet;
    private int steps;

    public void init() {
        this.position = Vec3I.of();
        this.facing = Config.startingDirection;
        this.up = Direction.UP;
        this.steps = 0;
    }

    public void tick(Scene scene) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        Integer blockId = scene.getBlockAt(x, y, z);
        Rule rule = ruleSet.getRules().get(blockId);
        if (rule == null) return;
        boolean isOfLastType = blockId == ruleSet.getRules().size() - 1;
        scene.setBlockAt(x, y, z, isOfLastType ? 0 : blockId + 1);
        switch (Rotation.get(rule.getRotation())) {
            case LEFT -> rotate(false);
            case RIGHT -> rotate(true);
            case UP -> roll(false);
            case DOWN -> roll(true);
            case BACKWARD -> rotate180();
        }
        move();
        steps++;
    }

    public void move() {
        position = position.add(facing.getVector());
    }

    public void roll(boolean clockwise) {
        if (clockwise) {// Forward
            Direction oldUp = up;
            up = facing;
            facing = oldUp.opposite();
        } else {
            Direction oldFacing = facing;
            facing = up;
            up = oldFacing.opposite();
        }
    }

    public void rotate180() {
        facing = facing.opposite();
    }

    public void rotate(boolean clockwise) {
        Vec3I leftVec = up.getVector().cross(facing.getVector());
        Direction direction = Direction.fromVector(leftVec);
        if (clockwise) direction = direction.opposite();
        facing = direction;
    }

    public String getDebugInfo() {
        return "(" + position.toString() + "," + facing + "," + GuiUtil.decimalFormat.format(steps) + ")";
    }
}