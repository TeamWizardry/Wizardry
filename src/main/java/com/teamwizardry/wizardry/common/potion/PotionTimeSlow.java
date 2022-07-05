package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.forgeevents.EntityUpdateEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.events.EntityMoveEvent;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class PotionTimeSlow extends PotionBase {

	public PotionTimeSlow() {
		super("time_slow", false, 0xE8CA0D);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);

		if (timeScale(entityLivingBaseIn) == 0 && entityLivingBaseIn instanceof EntityCreature) {
			((EntityLiving) entityLivingBaseIn).setNoAI(true);

			if(entityLivingBaseIn instanceof EntityCreeper) {
				((EntityCreeper)entityLivingBaseIn).setCreeperState(-1);
			}
		}

		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_IN, SoundCategory.NEUTRAL, 1f, 1);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (timeScale(entityLivingBaseIn) == 0 && entityLivingBaseIn instanceof EntityCreature) {
			((EntityLiving) entityLivingBaseIn).setNoAI(false);
		}

		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_OUT, SoundCategory.NEUTRAL, 1f, 1);
	}

	public static float timeScale(Entity entity) {
		// TODO: 10/6/18 make this apply to more than just the potion?

		if (entity instanceof EntityLivingBase) {
			PotionEffect effect = ModPotions.TIME_SLOW.getEffect((EntityLivingBase) entity);
			if (effect != null)
				if(effect.getAmplifier() >= 19) return 0f;  // if 20 (or over for some reason) just return 0
				else return 1f / (effect.getAmplifier() + 1.5f);
		}

		return -1f;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void fall(LivingFallEvent event) {
		float scale = timeScale(event.getEntity());

		if (scale >= 0)
			event.setDistance(event.getDistance() * scale);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void entityPreUpdate(EntityUpdateEvent event) {
		Entity entity = event.getEntity();
		float scale = timeScale(entity);

		if (!entity.hasNoGravity() && scale > 0) {
			double gravity = entity instanceof EntityLivingBase ? -0.08 : -0.04;

			entity.motionY -= gravity * (1 - scale);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void entityMove(EntityMoveEvent event) {
		float scale = timeScale(event.entity);

		if (scale >= 0) {
			event.x *= scale;
			event.y *= scale;
			event.z *= scale;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityItemPickup(PlayerEvent.ItemPickupEvent e) {
		if(timeScale(e.player) == 0) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityJump(LivingEvent.LivingJumpEvent event) {
		stopEvent(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemUse(LivingEntityUseItemEvent.Start event) {
		stopEvent(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemTickUse(LivingEntityUseItemEvent.Tick.Start event) {
		stopEvent(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onAttack(PlayerInteractEvent.LeftClickEmpty e) {
		if(timeScale(e.getEntityPlayer()) == 0) {
			e.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onSpell(SpellCastEvent e) {
		if(timeScale(e.getSpellData().getCaster(e.getWorld())) == 0) {
			e.setSpellData(new SpellData());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase e = event.getEntityLiving();
		if(e.isPotionActive(ModPotions.TIME_SLOW) && timeScale(e) == 0) {
			boolean shouldFreeze = true;
			if(e.isDead || e.getHealth() <= 0) {
				shouldFreeze = false;
			}
			if(e instanceof EntityDragon && ((EntityDragon) e).getPhaseManager().getCurrentPhase().getType() == PhaseList.DYING) {
				shouldFreeze = false;
			}
			if(shouldFreeze) {
				handleImportantEntityTicks(e);
				event.setCanceled(true);
			}
		}
	}

	private static void handleImportantEntityTicks(EntityLivingBase e) {
		if (e.hurtTime > 0) {
			e.hurtTime--;
		}
		if (e.hurtResistantTime > 0) {
			e.hurtResistantTime--;
		}

		e.prevLimbSwingAmount = e.limbSwingAmount;
		e.prevRenderYawOffset = e.renderYawOffset;
		e.prevRotationPitch = e.rotationPitch;
		e.prevRotationYaw = e.rotationYaw;
		e.prevRotationYawHead = e.rotationYawHead;
		e.prevSwingProgress = e.swingProgress;
		e.prevDistanceWalkedModified = e.distanceWalkedModified;
		e.prevCameraPitch = e.cameraPitch;

		if(e.isPotionActive(ModPotions.TIME_SLOW) && timeScale(e) == 0) {
			PotionEffect pe = e.getActivePotionEffect(ModPotions.TIME_SLOW);
			if (!pe.onUpdate(e)) {
				if (!e.world.isRemote) {
					e.removePotionEffect(ModPotions.TIME_SLOW);
				}
			}
		}

		if(e instanceof EntityDragon) {
			IPhase phase = ((EntityDragon) e).getPhaseManager().getCurrentPhase();
			if(phase.getType() != PhaseList.HOLDING_PATTERN && phase.getType() != PhaseList.DYING) {
				((EntityDragon) e).getPhaseManager().setPhase(PhaseList.HOLDING_PATTERN);
			}
		}
	}

//
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public static void onAttack(PlayerInteractEvent.LeftClickBlock e) {
//		if(timeScale(e.getEntityPlayer()) == 0) {
//			e.setResult(Event.Result.DENY);
//		}
//	}
////
//	@SubscribeEvent
//	public static void onRC(PlayerInteractEvent.RightClickItem e) {
//		if(timeScale(e.getEntityPlayer()) == 0) {
//			e.setResult(Event.Result.DENY);
//		}
//	}
//
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public static void onRC(PlayerInteractEvent.RightClickEmpty e) {
//		if(timeScale(e.getEntityPlayer()) == 0) {
//			e.setCanceled(true);
//		}
//	}
//
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public static void onRC(PlayerInteractEvent.RightClickBlock e) {
//		if(timeScale(e.getEntityPlayer()) == 0) {
//			e.setCanceled(true);
//		}
//	}

	private static void stopEvent(LivingEvent event) {
		PotionEffect effect = ModPotions.TIME_SLOW.getEffect(event.getEntityLiving());
		if (effect == null)
			return;
		if (timeScale(event.getEntity()) == 0) {
			event.setCanceled(true);
			event.setResult(Event.Result.DENY);
		}
	}
}
