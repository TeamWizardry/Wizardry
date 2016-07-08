package com.teamwizardry.wizardry.common.spell.module.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleSaturation extends Module
{
	public ModuleSaturation()
	{
		attributes.addAttribute(Attribute.POWER);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription()
	{
		return "Give the targeted player hunger and saturation points.";
	}

	@Override
	public String getDisplayName()
	{
		return "Saturation";
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setInteger(POWER, (int) attributes.apply(Attribute.POWER, 1));
		compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
		compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		if (caster instanceof EntityPlayer)
		{
			int power = spell.getInteger(POWER);
			EntityPlayer target = (EntityPlayer) caster;
			target.getFoodStats().addStats(power, 0.5F); // Function adds
															// saturation equal
															// to 2*arg1*arg2.
															// Setting arg2 to
															// 0.5 will make
															// spell saturate as
															// much hunger as it
															// fills
			return true;
		}
		return false;
	}
}