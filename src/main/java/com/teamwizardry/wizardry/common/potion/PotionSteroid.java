package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.common.base.PotionMod;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class PotionSteroid extends PotionMod {

	public PotionSteroid() {
		super("steroid", false, 0xFFFFFF);
		setBeneficial();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull EntityLivingBase entityLivingBase, int amplifier, double health) {
		if (entityLivingBase instanceof EntityPlayer) {
			IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) entityLivingBase);
			cap.setMana(cap.getMaxMana(), (EntityPlayer) entityLivingBase);
			cap.setBurnout(0, (EntityPlayer) entityLivingBase);
		}
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		PotionEffect effect = event.getEntityLiving().getActivePotionEffect(ModPotions.STEROID);
		if (effect == null) return;

		if (effect.getDuration() >= 250) {
			if (event.getEntityLiving() instanceof EntityPlayer) {
				IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) event.getEntityLiving());
				cap.setMana(cap.getMaxMana(), (EntityPlayer) event.getEntityLiving());
				cap.setBurnout(0, (EntityPlayer) event.getEntityLiving());
			}
		} else {
			if (event.getEntityLiving() instanceof EntityPlayer) {
				IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) event.getEntityLiving());
				cap.setMana(0, (EntityPlayer) event.getEntityLiving());
				cap.setBurnout(cap.getMaxBurnout(), (EntityPlayer) event.getEntityLiving());
			}
			event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10, 4, true, false));
			event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 4, true, false));
			event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.HUNGER, 10, 4, true, false));
			event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 10, 4, true, false));
			event.getEntityLiving().setHealth(0.5f);
		}
	}
}
