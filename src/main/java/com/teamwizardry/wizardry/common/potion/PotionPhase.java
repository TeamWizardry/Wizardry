package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.common.base.PotionMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionPhase extends PotionMod {

	public PotionPhase() {
		super("phase", false, 0xA9F3A9);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		if (entityLivingBaseIn instanceof EntityPlayer) {
			entityLivingBaseIn.getEntityData().setInteger("prev-gamemode", ((EntityPlayer) entityLivingBaseIn).isCreative() ? 1 : ((EntityPlayer) entityLivingBaseIn).isSpectator() ? 3 : 0);
			((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SPECTATOR);
		}

		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		if (entityLivingBaseIn instanceof EntityPlayer) {
			int type = entityLivingBaseIn.getEntityData().getInteger("prev-gamemode");
			switch (type) {
				case 0:
					((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SURVIVAL);
					break;
				case 1:
					((EntityPlayer) entityLivingBaseIn).setGameType(GameType.CREATIVE);
					break;
				case 3:
					((EntityPlayer) entityLivingBaseIn).setGameType(GameType.SURVIVAL);
					break;
			}
		}
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
