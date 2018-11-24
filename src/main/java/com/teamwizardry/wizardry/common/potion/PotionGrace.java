package com.teamwizardry.wizardry.common.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.init.ModPotions;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class PotionGrace extends PotionBase
{
	public PotionGrace()
	{
		super("grace", false, 0xDD5B23);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, UUID.randomUUID().toString(), 0.2, 2);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems()
	{
		return new ArrayList<>();
	}

	@SubscribeEvent
	public void onCrit(CriticalHitEvent event)
	{
		if (event.getEntityPlayer().getActivePotionEffect(ModPotions.GRACE) != null)
		{
			event.setDamageModifier(1.5F);
			event.setResult(Result.ALLOW);
		}
		event.getEntityPlayer().removePotionEffect(ModPotions.GRACE);
	}
}
