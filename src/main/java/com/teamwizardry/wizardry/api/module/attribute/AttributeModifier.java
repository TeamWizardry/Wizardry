package com.teamwizardry.wizardry.api.module.attribute;

public class AttributeModifier {

    public final Operation op;
    public final double value;
    public final Priority priority;

    public AttributeModifier(Operation op, double value) {
        this.op = op;
        this.value = value;
        this.priority = Priority.NORMAL;
    }

    public AttributeModifier(Operation op, double value, Priority priority) {
        this.op = op;
        this.value = value;
        this.priority = priority;
    }

    public double apply(double value) {
        if (op == Operation.ADD)
            value += this.value;
        if (op == Operation.MULTIPLY)
            value *= this.value;
        return value;
    }

    public enum Priority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }

    public enum Operation {
        ADD, MULTIPLY
    }
}
