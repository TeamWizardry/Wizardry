package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.network.PacketVanishPotion;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
// TODO: other player testing, proper in/out fading
@SuppressWarnings({"rawtypes", "unused"})
public class PotionVanish extends PotionBase {


	public PotionVanish() {
		super("vanish", false, 0xA9F3A9);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);

		if (!(entityLivingBaseIn instanceof EntityPlayer))
			PacketHandler.NETWORK.sendToAll(new PacketVanishPotion(entityLivingBaseIn.getEntityId(), 0, 100));

		PotionEffect pe = entityLivingBaseIn.getActivePotionEffect(ModPotions.VANISH);
		if (pe == null) return;
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, pe.getDuration(), 100, true, true));
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);

		if (!(entityLivingBaseIn instanceof EntityPlayer))
			PacketHandler.NETWORK.sendToAll(new PacketVanishPotion(entityLivingBaseIn.getEntityId()));
	}

}
