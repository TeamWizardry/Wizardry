package com.teamwizardry.wizardry.asm;

import com.teamwizardry.wizardry.api.events.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Demoniaque.
 */
public class WizardryASMHooks {

	public static boolean playerClipEventHook(boolean hasNoClip, EntityPlayer player) {
		PlayerClipEvent event = new PlayerClipEvent(hasNoClip, player);
		MinecraftForge.EVENT_BUS.post(event);

		return event.noClip;
	}

	public static EntityMoveEvent entityPreMoveHook(Entity entity, MoverType type, double x, double y, double z) {
		EntityMoveEvent event = new EntityMoveEvent(entity, type, x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static EntityTravelEvent travel(EntityLivingBase entity, float strafe, float vertical, float forward) {
		EntityTravelEvent event = new EntityTravelEvent(entity, strafe, vertical, forward);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static boolean entityRenderShadowAndFire(Entity entity) {
		EntityRenderShadowAndFireEvent event = new EntityRenderShadowAndFireEvent(entity);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.override;
	}

	public static float slipperyHook(float prev, Entity entity) {
		SlipperinessEvent event = new SlipperinessEvent(entity, prev);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getSlipperiness();
	}
}
