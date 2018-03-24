package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.EntityHaloInfusionItem;
import com.teamwizardry.wizardry.common.tile.TileHaloInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class RenderHaloInfusionItem extends Render<EntityHaloInfusionItem> {

	public RenderHaloInfusionItem(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public boolean canRenderName(EntityHaloInfusionItem entity) {
		return false;
	}

	@Override
	public void doRender(@Nonnull EntityHaloInfusionItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if (entity.world == null || entity.getInfuserPos() == null) return;

		TileEntity tile = entity.world.getTileEntity(entity.getInfuserPos());
		if (!(tile instanceof TileHaloInfuser) || ((TileHaloInfuser) tile).getHalo().isEmpty()) {
			return;
		}

		if (!entity.getHaloInfusionItem().getStack().isEmpty() || RandUtil.nextInt(15) == 0)
			entity.getHaloInfusionItem().render(entity.world, entity.getPositionVector().addVector(0, entity.height / 2.0, 0));

		if (entity.getHaloInfusionItem().getStack().isEmpty()) return;

		double depthRadius = 0.5;
		EntityPlayer player = Minecraft.getMinecraft().player;
		Vec3d playerSub = player.getPositionVector().addVector(0, player.eyeHeight, 0).subtract(entity.getPositionVector().addVector(0, entity.height, 0));
		Vec3d restricted = playerSub.scale(depthRadius / playerSub.lengthVector());
		restricted = new Vec3d(restricted.x, restricted.y / 2.0, restricted.z);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(restricted.x, restricted.y + 0.5, restricted.z);
		GlStateManager.scale(0.4, 0.4, 0.4);
		GlStateManager.rotate(Minecraft.getMinecraft().player.rotationYaw * -1, 0, 1, 0);
		GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, 1, 0, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(entity.getHaloInfusionItem().getStack(), ItemCameraTransforms.TransformType.NONE);
		GlStateManager.popMatrix();

	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityHaloInfusionItem entity) {
		return null;
	}
}
