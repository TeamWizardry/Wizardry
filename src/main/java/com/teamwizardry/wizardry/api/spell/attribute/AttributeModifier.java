package com.teamwizardry.wizardry.api.spell.attribute;

import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AttributeModifier {

	private AttributeRegistry.Attribute attribute;
	private double modifier;
	private Operation op;
	@Nonnull
	private Set<Module> moduleSet = new HashSet<>();

	public AttributeModifier(AttributeRegistry.Attribute attribute, double modifier, Operation op, @Nullable Module... modules) {
		this.attribute = attribute;
		this.modifier = modifier;
		this.op = op;
		if (modules != null) {
			Collections.addAll(this.moduleSet, modules);
		}
	}

	public AttributeModifier(AttributeRegistry.Attribute attribute, double modifier, Operation op, @NotNull Set<Module> moduleSet) {
		this.attribute = attribute;
		this.modifier = modifier;
		this.op = op;
		this.moduleSet = moduleSet;
	}

	public double apply(double currentValue) {
		return op.apply(currentValue, modifier);
	}

	@Nullable
	public static AttributeModifier deserialize(NBTTagCompound compound) {
		AttributeRegistry.Attribute attribute = null;
		Operation operation = null;
		double modifier = -1;
		Set<Module> moduleSet = new HashSet<>();

		if (compound.hasKey("attribute"))
			attribute = AttributeRegistry.getAttributeFromName(compound.getString("attribute"));

		if (compound.hasKey("op")) operation = Operation.valueOf(compound.getString("op"));

		if (compound.hasKey("modifier"))
			modifier = compound.getDouble("modifier");

		if (compound.hasKey("module_set")) {
			NBTTagList list = compound.getTagList("module_set", Constants.NBT.TAG_STRING);
			for (NBTBase base : list) {
				if (base instanceof NBTTagString) {
					Module module = Module.deserialize((NBTTagString) base);
					if (module == null) continue;
					moduleSet.add(module);
				}
			}
		}

		if (attribute == null || operation == null || modifier == -1) return null;

		return new AttributeModifier(attribute, modifier, operation, moduleSet);
	}

	public AttributeRegistry.Attribute getAttribute() {
		return attribute;
	}

	public Operation getOperation() {
		return op;
	}

	@Nonnull
	public Set<Module> getModuleSet() {
		return moduleSet;
	}

	public double getModifier() {
		return modifier;
	}

	public void setModifier(double newValue) {
		modifier = newValue;
	}

	public AttributeModifier copy() {
		return new AttributeModifier(attribute, modifier, op, moduleSet);
	}

	@Override
	public String toString() {
		return attribute.getShortName() + ": " + op + " " + modifier;
	}

	public NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("attribute", attribute.getNbtName());
		compound.setDouble("modifier", modifier);
		compound.setString("op", getOperation().toString());

		NBTTagList list = new NBTTagList();
		for (Module module : moduleSet) {
			list.appendTag(module.serialize());
		}
		compound.setTag("module_set", list);
		return compound;
	}
}
