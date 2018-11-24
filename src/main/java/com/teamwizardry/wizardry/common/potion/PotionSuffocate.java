package com.teamwizardry.wizardry.common.potion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * Created by Demoniaque.
 */
public class PotionSuffocate extends PotionBase {

	public PotionSuffocate() {
		super("suffocate", true, 0x00003C);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		entity.setAir(0);
		entity.attackEntityFrom(DamageSource.DROWN, 2.0F);
	}
}
