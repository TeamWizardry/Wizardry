package com.teamwizardry.wizardry.client.render.item;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ClientConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.awt.*;

public class RenderHaloPlayer implements LayerRenderer<EntityPlayer> {

	private ModelRenderer modelRenderer;

	public RenderHaloPlayer(ModelRenderer modelRenderer) {
		this.modelRenderer = modelRenderer;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (BaublesSupport.getItem(player, ModItems.FAKE_HALO, ModItems.CREATIVE_HALO, ModItems.REAL_HALO).isEmpty())
			return;
		ItemStack halo = BaublesSupport.getItem(player, ModItems.FAKE_HALO, ModItems.CREATIVE_HALO, ModItems.REAL_HALO);

		// TODO: Remove these once we have a cosmetics system
		if (halo.getItem() == ModItems.FAKE_HALO && !ClientConfigValues.renderCrudeHalo) return;
		if (halo.getItem() == ModItems.REAL_HALO && !ClientConfigValues.renderRealHalo) return;
		if (halo.getItem() == ModItems.CREATIVE_HALO && !ClientConfigValues.renderCreativeHalo) return;
		
		if (halo.getItem() == ModItems.FAKE_HALO) {
			GlStateManager.pushMatrix();

			if (player.isSneaking()) GlStateManager.translate(0.0f, 0.2f, 0.0f);

			this.modelRenderer.postRender(0.0625f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

			GlStateManager.translate(0.0f, -0.25f, 0.0f);
			GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
			GlStateManager.scale(0.625f, -0.625f, -0.625f);

			Minecraft.getMinecraft().getItemRenderer().renderItem(player, halo, ItemCameraTransforms.TransformType.HEAD);

			GlStateManager.popMatrix();
		} else {

			Vec3d playerOrigin = player.getPositionVector().addVector(0, player.height + (player.isSneaking() ? 0.2 : 0.4), 0);
			InterpCircle circle = new InterpCircle(Vec3d.ZERO, new Vec3d(0, 1, 0), 0.3f, RandUtil.nextFloat(), RandUtil.nextFloat());

			for (Vec3d origin : circle.list(10)) {
				ParticleBuilder glitter = new ParticleBuilder(3);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
				glitter.disableMotionCalculation();
				glitter.disableRandom();

				ParticleSpawner.spawn(glitter, player.world, new StaticInterp<>(playerOrigin.add(origin)), 1, 0, (aFloat, particleBuilder) -> {
					if (RandUtil.nextInt(10) != 0)
						if (halo.getItem() == ModItems.CREATIVE_HALO)
							glitter.setColor(ColorUtils.changeColorAlpha(new Color(0xd600d2), RandUtil.nextInt(60, 100)));
						else glitter.setColor(ColorUtils.changeColorAlpha(Color.YELLOW, RandUtil.nextInt(60, 100)));
					else glitter.setColor(ColorUtils.changeColorAlpha(Color.WHITE, RandUtil.nextInt(60, 100)));
					glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
					glitter.setLifetime(10);
					glitter.setScaleFunction(new InterpFadeInOut(0.5f, 0.5f));
					//glitter.setMotion(new Vec3d(player.motionX / 2.0, (player.motionY + (player.capabilities.isFlying ? 0 : 0.0784)) / 2.0, player.motionZ / 2.0));

					glitter.setTick(particle -> {
						Vec3d newOrigin = player.getPositionVector().addVector(0, player.height + (player.isSneaking() ? 0.2 : 0.4), 0);
						particle.setPos(newOrigin.add(origin));
					});
				});
			}

		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
