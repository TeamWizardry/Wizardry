package com.teamwizardry.wizardry.common.module.effects;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.init.ModPotions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegisterModule(ID="effect_grace")
public class ModuleEffectGrace implements IModuleEffect
{
	@Override
	public String[] compatibleModifiers()
	{
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, SpellData spell, SpellRing spellRing)
	{
		Entity entity = spell.getVictim();
		
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
		
		if (!spellRing.taxCaster(spell, true)) return false;
		
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.GRACE, (int) time));
		
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing)
	{
		// TODO: EffectGrace Particles
	}
}
