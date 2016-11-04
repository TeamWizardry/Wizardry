package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IHasAffinity;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class ModuleFallProtection extends Module implements IHasAffinity {

	public ModuleFallProtection(ItemStack stack) {
		super(stack);
		attributes.addAttribute(Attribute.POWER);
		attributes.addAttribute(Attribute.DURATION);
	}

	@Override
	public ModuleType getType() {
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription() {
		return "Negate fall damage by 5% per feather.";
	}

	@Override
	public String getDisplayName() {
		return "Fall Protection";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setInteger(Constants.Module.POWER, (int) attributes.apply(Attribute.POWER, 1.0));
		compound.setInteger(Constants.Module.DURATION, (int) attributes.apply(Attribute.DURATION, 1.0));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 10.0));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 10.0));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		// TODO: Add fall protection (potion effect?)
		return false;
	}

	@Override
	public Map<IBloodType, Integer> getAffinityLevels() {
		Map<IBloodType, Integer> levels = new HashMap<>();
		levels.put(BloodRegistry.ZEPHYRBLOOD, 3);
		return levels;
	}
}
