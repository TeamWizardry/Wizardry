package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleProjectileCount extends Module implements IModifier {
	public ModuleProjectileCount(ItemStack stack) {
		super(stack);
		canHaveChildren = false;
	}

	@Override
	public ModuleType getType() {
		return ModuleType.MODIFIER;
	}

	@Override
	public String getDescription() {
		return "Increases the number of beams or projectiles fired by the spell.";
	}

	@Override
	public String getDisplayName() {
		return "Increase Projectile Count";
	}

	@Override
	public void apply(AttributeMap map) {
		map.putModifier(Attribute.PROJ_COUNT, new AttributeModifier(Operation.ADD, 1.0));

		map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.8));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.8));
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
}
