package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.wizardry.api.events.PlayerClipEvent;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionPhase extends PotionMod {

	public PotionPhase() {
		super("phase", false, 0xA9F3A9);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		//if (entityLivingBaseIn instanceof EntityPlayer) {
		//	entityLivingBaseIn.getEntityData().setInteger("prev_gamemode", ((EntityPlayer) entityLivingBaseIn).isCreative() ? 1 : ((EntityPlayer) entityLivingBaseIn).isSpectator() ? 3 : 0);
		//	entityLivingBaseIn.getEntityData().setBoolean("was_flying", ((EntityPlayer) entityLivingBaseIn).capabilities.isFlying);
		//	((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SPECTATOR);
		//}

		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		//if (entityLivingBaseIn instanceof EntityPlayer) {
		//	int type = entityLivingBaseIn.getEntityData().getInteger("prev_gamemode");
		//	switch (type) {
		//		case 0:
		//			((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SURVIVAL);
		//			break;
		//		case 1:
		//			((EntityPlayer) entityLivingBaseIn).setGameType(GameType.CREATIVE);
		//			break;
		//		case 3:
		//			((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SURVIVAL);
		//			break;
		//	}
//
		//	if (entityLivingBaseIn.getEntityData().getBoolean("was_flying"))
		//		((EntityPlayer) entityLivingBaseIn).capabilities.isFlying = true;
		//}
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@SubscribeEvent
	public void playerClipEvent(PlayerClipEvent event) {
		if (event.player.isPotionActive(ModPotions.PHASE)) event.noClip = true;
	}
}
