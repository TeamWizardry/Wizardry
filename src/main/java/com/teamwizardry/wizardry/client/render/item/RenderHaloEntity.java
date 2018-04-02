package com.teamwizardry.wizardry.client.render.item;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.awt.*;

public class RenderHaloEntity implements LayerRenderer<EntityLivingBase> {

	private ModelRenderer modelRenderer;

	public RenderHaloEntity(ModelRenderer modelRenderer) {
		this.modelRenderer = modelRenderer;
	}

	@Override
	public void doRenderLayer(@Nonnull EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (BaublesSupport.getItem(entitylivingbaseIn, ModItems.FAKE_HALO, ModItems.CREATIVE_HALO, ModItems.REAL_HALO).isEmpty())
			return;
		ItemStack halo = BaublesSupport.getItem(entitylivingbaseIn, ModItems.FAKE_HALO, ModItems.CREATIVE_HALO, ModItems.REAL_HALO);

		// TODO: Remove these once we have a cosmetics system
		if (halo.getItem() == ModItems.FAKE_HALO && !ClientConfigValues.renderCrudeHalo) return;
		if (halo.getItem() == ModItems.REAL_HALO && !ClientConfigValues.renderRealHalo) return;
		if (halo.getItem() == ModItems.CREATIVE_HALO && !ClientConfigValues.renderCreativeHalo) return;
		
		if (halo.getItem() == ModItems.FAKE_HALO) {
			GlStateManager.pushMatrix();

			if (entitylivingbaseIn.isSneaking()) GlStateManager.translate(0.0f, 0.2f, 0.0f);

			boolean flag = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombieVillager;

			if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof EntityVillager)) {
				GlStateManager.translate(0.0f, 0.5f * scale, 0.0f);
				GlStateManager.scale(0.7f, 0.7f, 0.7f);
				GlStateManager.translate(0.0f, 16.0f * scale, 0.0f);
			}

			if (flag) GlStateManager.translate(0.0f, 0.1875f, 0.0f);

			this.modelRenderer.postRender(0.0625f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

			GlStateManager.translate(0.0f, -0.25f, 0.0f);
			GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
			GlStateManager.scale(0.625f, -0.625f, -0.625f);

			Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, halo, ItemCameraTransforms.TransformType.HEAD);

			GlStateManager.popMatrix();
		} else {
			ParticleBuilder glitter = new ParticleBuilder(3);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
			glitter.disableMotionCalculation();
			glitter.disableRandom();

			ParticleSpawner.spawn(glitter, entitylivingbaseIn.world, new InterpCircle(entitylivingbaseIn.getPositionVector().addVector(0, entitylivingbaseIn.height + (entitylivingbaseIn.isSneaking() ? 0.2 : 0.4), 0), new Vec3d(0, 1, 0), 0.3f, RandUtil.nextFloat(), RandUtil.nextFloat()), 10, 0, (aFloat, particleBuilder) -> {
				if (RandUtil.nextInt(10) != 0)
					if (halo.getItem() == ModItems.CREATIVE_HALO)
						glitter.setColor(ColorUtils.changeColorAlpha(new Color(0xd600d2), RandUtil.nextInt(60, 100)));
					else glitter.setColor(ColorUtils.changeColorAlpha(Color.YELLOW, RandUtil.nextInt(60, 100)));
				else glitter.setColor(ColorUtils.changeColorAlpha(Color.WHITE, RandUtil.nextInt(60, 100)));
				glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
				glitter.setLifetime(10);
				glitter.setScaleFunction(new InterpFadeInOut(0.5f, 0.5f));
				glitter.setMotion(new Vec3d(entitylivingbaseIn.motionX / 2.0, (entitylivingbaseIn.motionY + 0.0784) / 2.0, entitylivingbaseIn.motionZ / 2.0));
			});
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
