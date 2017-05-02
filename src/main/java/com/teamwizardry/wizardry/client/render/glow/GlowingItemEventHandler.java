package com.teamwizardry.wizardry.client.render.glow;

import com.google.common.base.Objects;
import com.teamwizardry.librarianlib.features.shader.ShaderHelper;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.client.core.WizardryClientMethodHandles;
import com.teamwizardry.wizardry.client.fx.Shaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GlowingItemEventHandler {
	private static final GlowingItemEventHandler INSTANCE = new GlowingItemEventHandler();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@SubscribeEvent(receiveCanceled = true)
	public void onRenderHand(RenderHandEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		boolean flag = (entity instanceof EntityLivingBase) && ((EntityLivingBase) entity).isPlayerSleeping();

		EntityRenderer render = mc.entityRenderer;

		ItemStack stackMain = WizardryClientMethodHandles.getStackMainHand(render.itemRenderer);
		ItemStack stackOff = WizardryClientMethodHandles.getStackOffHand(render.itemRenderer);
		if (((stackMain == null) || !(stackMain.getItem() instanceof IGlowOverlayable)) &&
				((stackOff == null) || !(stackOff.getItem() instanceof IGlowOverlayable)))
			return;

		if ((mc.playerController != null) && (mc.gameSettings.thirdPersonView == 0) && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
			GlStateManager.pushMatrix();
			render.enableLightmap();
			render(e.getPartialTicks(), false, !e.isCanceled());
			render.disableLightmap();
			GlStateManager.popMatrix();

			render(e.getPartialTicks(), true, false);
		}

		GlStateManager.disableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		e.setCanceled(true);
	}

	private void render(float partialTicks, boolean overlay, boolean renderNonOverlays) {
		ItemRenderer render = Minecraft.getMinecraft().getItemRenderer();

		AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().player;
		float f = abstractclientplayer.getSwingProgress(partialTicks);
		EnumHand enumhand = Objects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
		float f1 = abstractclientplayer.prevRotationPitch + ((abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks);
		float f2 = abstractclientplayer.prevRotationYaw + ((abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks);
		boolean flag = true;
		boolean flag1 = true;

		if (abstractclientplayer.isHandActive()) {
			ItemStack itemstack = abstractclientplayer.getActiveItemStack();

			if ((itemstack != null) && (itemstack.getItem() == Items.BOW)) {
				EnumHand enumhand1 = abstractclientplayer.getActiveHand();
				flag = enumhand1 == EnumHand.MAIN_HAND;
				flag1 = !flag;
			}
		}

		rotateAroundXAndY(f1, f2);
		setLightmap();
		rotateArm(partialTicks);
		GlStateManager.enableRescaleNormal();

		float prevProgMain = WizardryClientMethodHandles.getPrevEquipMainHand(render);
		float prevProgOff = WizardryClientMethodHandles.getPrevEquipOffHand(render);
		float progMain = WizardryClientMethodHandles.getEquipMainHand(render);
		float progOff = WizardryClientMethodHandles.getEquipOffHand(render);
		ItemStack stackMain = WizardryClientMethodHandles.getStackMainHand(render);
		ItemStack stackOff = WizardryClientMethodHandles.getStackOffHand(render);

		if (flag && (stackMain != null) && ((stackMain.getItem() instanceof IGlowOverlayable) || (!overlay && renderNonOverlays))) {
			if (!overlay || !(stackMain.getItem() instanceof IGlowOverlayable) || ((IGlowOverlayable) stackMain.getItem()).useOverlay(stackMain)) {
				if (overlay && (stackMain.getItem() instanceof IGlowOverlayable)) {
					IGlowOverlayable item = (IGlowOverlayable) stackMain.getItem();
					if (item.useShader(stackMain))
						ShaderHelper.INSTANCE.useShader(Shaders.rawColor);
					if (item.disableLighting(stackMain))
						GlStateManager.disableLighting();
				}

				float f3 = (enumhand == EnumHand.MAIN_HAND) ? f : 0.0F;
				float f5 = 1.0F - (prevProgMain + ((progMain - prevProgMain) * partialTicks));
				render.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.MAIN_HAND, f3, overlay ? GlowingOverlayHelper.overlayStack(stackMain) : stackMain, f5);

				if (overlay && (stackMain.getItem() instanceof IGlowOverlayable)) {
					IGlowOverlayable item = (IGlowOverlayable) stackMain.getItem();
					if (item.useShader(stackMain))
						ShaderHelper.INSTANCE.releaseShader();
					if (item.disableLighting(stackMain))
						GlStateManager.enableLighting();
				}
			}
		}

		if (flag1 && (stackOff != null) && ((stackOff.getItem() instanceof IGlowOverlayable) || (!overlay && renderNonOverlays))) {
			if (!overlay || !(stackOff.getItem() instanceof IGlowOverlayable) || ((IGlowOverlayable) stackOff.getItem()).useOverlay(stackOff)) {
				if (overlay && (stackOff.getItem() instanceof IGlowOverlayable)) {
					IGlowOverlayable item = (IGlowOverlayable) stackOff.getItem();
					if (item.useShader(stackOff))
						ShaderHelper.INSTANCE.useShader(Shaders.rawColor);
					if (item.disableLighting(stackOff))
						GlStateManager.disableLighting();
				}

				float f4 = (enumhand == EnumHand.OFF_HAND) ? f : 0.0F;
				float f6 = 1.0F - (prevProgOff + ((progOff - prevProgOff) * partialTicks));
				render.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.OFF_HAND, f4, overlay ? GlowingOverlayHelper.overlayStack(stackOff) : stackOff, f6);

				if (overlay && (stackOff.getItem() instanceof IGlowOverlayable)) {
					IGlowOverlayable item = (IGlowOverlayable) stackOff.getItem();
					if (item.useShader(stackOff))
						ShaderHelper.INSTANCE.releaseShader();
					if (item.disableLighting(stackOff))
						GlStateManager.enableLighting();
				}
			}
		}

		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}

	private void rotateAroundXAndY(float angle, float angleY) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private void setLightmap() {
		AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().player;
		int i = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
		float f = (i & 65535);
		float f1 = (i >> 16);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
	}

	private void rotateArm(float partTicks) {
		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
		float f = entityplayersp.prevRenderArmPitch + ((entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * partTicks);
		float f1 = entityplayersp.prevRenderArmYaw + ((entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * partTicks);
		GlStateManager.rotate((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
	}
}
