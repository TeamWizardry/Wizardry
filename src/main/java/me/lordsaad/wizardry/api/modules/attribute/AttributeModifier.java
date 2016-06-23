package me.lordsaad.wizardry.api.modules.attribute;

public class AttributeModifier {

	public final Operation op;
	public final double value;
	
	public AttributeModifier(Operation op, double value) {
		this.op = op;
		this.value = value;
	}
	
	public double apply(double value) {
		if(op == Operation.ADD)
			value += this.value;
		if(op == Operation.MULTIPLY)
			value *= this.value;
		return value;
	}
	
	public static enum Operation {
		ADD, MULTIPLY
	}
	
}
