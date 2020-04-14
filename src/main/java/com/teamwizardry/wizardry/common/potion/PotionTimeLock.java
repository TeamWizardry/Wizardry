package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.common.core.WizardryNemezManager;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
// import com.teamwizardry.wizardry.common.module.effects.ModuleEffectTimeLock;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
/*
@Mod.EventBusSubscriber
public class PotionTimeLock extends PotionBase {

	public PotionTimeLock() {
		super("time_lcok", true, 0x00003C);
	}

	@SubscribeEvent
	public static void dropItem(ItemTossEvent event) {
		if (event.getPlayer().isPotionActive(ModPotions.TIME_LOCK)) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void itemPick(PlayerEvent.ItemPickupEvent event) {
		if (event.player.isPotionActive(ModPotions.TIME_LOCK)) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void tickPlayer(TickEvent.WorldTickEvent event) {
		if (event.side != Side.SERVER) return;
		if (event.phase != TickEvent.Phase.END) return;

		for (UUID caster : ModuleEffectTimeLock.timeLockedEntities.keySet()) {

			NemezTracker tracker = WizardryNemezManager.getOrCreateNemezDrive(event.world, caster);
			for (UUID uuid : ModuleEffectTimeLock.timeLockedEntities.get(caster)) {
				List<EntityLivingBase> entities = event.world.getEntities(EntityLivingBase.class, input -> {
					if (input != null) {
						return input.getUniqueID().equals(uuid);
					}
					return false;
				});
				if (entities.size() == 1) {
					EntityLivingBase e = entities.get(0);

					if (e.isPotionActive(ModPotions.TIME_LOCK)) {
						tracker.trackEntity(e);
					}
				}
			}
		}
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
*/