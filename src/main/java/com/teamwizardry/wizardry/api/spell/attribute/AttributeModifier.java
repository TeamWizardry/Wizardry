package com.teamwizardry.wizardry.api.spell.attribute;

public class AttributeModifier {

	private String attribute;
	private double modifier;
	private Operation op;

	public AttributeModifier(String attribute, double modifier, Operation op) {
		this.attribute = attribute;
		this.modifier = modifier;
		this.op = op;
	}

	public double apply(double currentValue) {
		return op.apply(currentValue, modifier);
	}

	public boolean isAttribute(String attribute) {
		return this.attribute.equals(attribute);
	}

	public String getAttribute() {
		return attribute;
	}

	public Operation getOperation() {
		return op;
	}

	public AttributeModifier copy() {
		return new AttributeModifier(attribute, modifier, op);
	}
}
