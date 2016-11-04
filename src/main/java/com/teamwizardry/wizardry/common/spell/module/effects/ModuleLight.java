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

public class ModuleLight extends Module implements IHasAffinity {

	public ModuleLight(ItemStack stack) {
		super(stack);
		attributes.addAttribute(Attribute.DURATION);
	}

	@Override
	public ModuleType getType() {
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription() {
		return "Places a temporary light source at the targeted location. If the duration is high enough, the light will be permanent.";
	}

	@Override
	public String getDisplayName() {
		return "Light";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setInteger(Constants.Module.DURATION, (int) attributes.apply(Attribute.DURATION, 1.0));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 10.0));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 10.0));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<IBloodType, Integer> getAffinityLevels() {
		Map<IBloodType, Integer> levels = new HashMap<>();
		levels.put(BloodRegistry.PYROBLOOD, 2);
		levels.put(BloodRegistry.ZEPHYRBLOOD, 1);
		return levels;
	}
}
