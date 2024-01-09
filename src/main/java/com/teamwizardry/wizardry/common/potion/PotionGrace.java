package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

@EventBusSubscriber
public class PotionGrace extends PotionBase {
	public PotionGrace() {
		super("grace", false, 0xDD5B23);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, UUID.randomUUID().toString(), 0.2, 2);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.GOOD_ETHEREAL_CHILLS, CommonProxy.SoundCategory_WizardryGeneral, 1f, 1f);
	}

	@SubscribeEvent
	public void onCrit(CriticalHitEvent event) {
		if (event.getEntityPlayer().getActivePotionEffect(ModPotions.GRACE) != null) {
			event.setDamageModifier(1.5F);
			event.setResult(Result.ALLOW);
		}
		event.getEntityPlayer().removePotionEffect(ModPotions.GRACE);
	}
}
