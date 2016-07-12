package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ModulePotion extends Module {
	public static final String POTION = "Potion";
	private int potionID;
	
    public ModulePotion() {
        attributes.addAttribute(Attribute.POWER);
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Cause the targeted entity to gain the given potion effect, at a certain stiffness and duration.";
    }

    @Override
    public String getDisplayName() {
        return "Potion";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setInteger(POTION, potionID);
        compound.setInteger(POWER, (int) attributes.apply(Attribute.POWER, 1));
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
    	return compound;
    }
    
    public ModulePotion setPotionID(int potionID)
    {
    	this.potionID = potionID;
    	return this;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		int potionId = spell.getInteger(POTION);
		int power = spell.getInteger(POWER);
		int duration = spell.getInteger(DURATION);
		if (caster instanceof EntityLivingBase)
			((EntityLivingBase) caster).addPotionEffect(new PotionEffect(Potion.getPotionById(potionId), duration, power));
		return false;
	}
}