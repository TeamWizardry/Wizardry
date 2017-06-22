package com.teamwizardry.wizardry.asm;

import com.teamwizardry.wizardry.api.events.*;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by LordSaad.
 */
public class WizardryASMHooks {

	public static boolean playerClipEventHook(boolean isSpectator, EntityPlayer player) {
		if (isSpectator) return true;

		PlayerClipEvent event = new PlayerClipEvent(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.noClip;
	}

	public static boolean entityPreMoveHook(Entity entity, MoverType type, double x, double y, double z) {
		EntityPostMoveEvent event = new EntityPostMoveEvent(entity, type, x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.override;
	}

	public static boolean entityMoveWithHeading(EntityLivingBase entity, float strafe, float forward) {
		EntityMoveWithHeadingEvent event = new EntityMoveWithHeadingEvent(entity, strafe, forward);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.override;
	}

	public static boolean entityRenderModelToPlayer(RenderLivingBase renderLivingBase, EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		EntityRenderToPlayerEvent event = new EntityRenderToPlayerEvent(renderLivingBase, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.override;
	}

	public static boolean entityRenderShadowAndFire(Entity entity) {
		EntityRenderShadowAndFireEvent event = new EntityRenderShadowAndFireEvent(entity);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.override;
	}
}
