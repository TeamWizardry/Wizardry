package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.api.entity.fairy.FairyData;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Demoniaque on 8/25/2016.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderFairy extends RenderLiving<EntityFairy> {

	public RenderFairy(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityFairy entity) {
		return false;
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityFairy entity) {
		return null;
	}

	@SubscribeEvent
	public static void renderFairy(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;

		GlStateManager.pushMatrix();

		List<EntityFairy> fairies = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityFairy.class, Minecraft.getMinecraft().player.getEntityBoundingBox().grow(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16));
		fairies.sort(Comparator.comparingDouble(o -> o.getDistanceSq(Minecraft.getMinecraft().player)));
		Collections.reverse(fairies);

		for (EntityFairy fairy : fairies) {
			if (fairy.isInvisible() || fairy.isDead) continue;

			FairyData dataFairy = fairy.getDataFairy();
			if (dataFairy == null) return;

			dataFairy.render(
					fairy.world,
					new Vec3d(fairy.posX, fairy.posY + (dataFairy.isDepressed ? fairy.height : 0), fairy.posZ),
					new Vec3d(fairy.lastTickPosX, fairy.lastTickPosY, fairy.lastTickPosZ),
					event.getPartialTicks());
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void doRender(@NotNull EntityFairy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
