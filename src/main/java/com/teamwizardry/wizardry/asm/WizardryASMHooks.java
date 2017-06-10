package com.teamwizardry.wizardry.asm;

import com.teamwizardry.wizardry.api.events.EntityMoveWithHeadingEvent;
import com.teamwizardry.wizardry.api.events.EntityPostMoveEvent;
import com.teamwizardry.wizardry.api.events.PlayerClipEvent;
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
}
