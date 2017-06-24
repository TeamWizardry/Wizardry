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
}
