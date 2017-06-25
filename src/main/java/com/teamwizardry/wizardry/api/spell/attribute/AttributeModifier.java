package com.teamwizardry.wizardry.api.spell.attribute;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class AttributeModifier implements INBTSerializable<NBTTagCompound> {

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

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("attribute", attribute);
		compound.setDouble("modifier", modifier);
		compound.setString("operation", op.name());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		attribute = nbt.getString("attribute");
		modifier = nbt.getDouble("modifier");
		op = Operation.valueOf(nbt.getString("operation"));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AttributeModifier modifier1 = (AttributeModifier) o;

		if (Double.compare(modifier1.modifier, modifier) != 0) return false;
		if (attribute != null ? !attribute.equals(modifier1.attribute) : modifier1.attribute != null) return false;
		return op == modifier1.op;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = attribute != null ? attribute.hashCode() : 0;
		temp = Double.doubleToLongBits(modifier);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (op != null ? op.hashCode() : 0);
		return result;
	}
}
