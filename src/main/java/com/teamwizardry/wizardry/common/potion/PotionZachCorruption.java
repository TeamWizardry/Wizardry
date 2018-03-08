package com.teamwizardry.wizardry.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public class PotionZachCorruption extends PotionBase {

	private static HashMap<UUID, Float> HEALTH_MAP = new HashMap<>();

	public PotionZachCorruption() {
		super("zach_corruption", false, 0x469CD6);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (entityLivingBaseIn instanceof EntityPlayer)
			HEALTH_MAP.putIfAbsent(entityLivingBaseIn.getUniqueID(), entityLivingBaseIn.getHealth());
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
		if (!entity.isPotionActive(this)) return;

		if (HEALTH_MAP.containsKey(entity.getUniqueID())) {
			//	if (RandUtil.nextInt(500) == 0) {
			entity.setHealth(HEALTH_MAP.get(entity.getUniqueID()));
			//		HEALTH_MAP.put(entity.getUniqueID(), entity.getHealth());
			//	}
		}
	}
}
