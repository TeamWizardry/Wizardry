package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.events.EntityRenderShadowAndFireEvent;
import com.teamwizardry.wizardry.common.network.PacketVanishPotion;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
// TODO: other player testing, proper in/out fading
@SuppressWarnings({"rawtypes", "unused"})
public class PotionVanish extends PotionBase {

	private static final Animator ANIMATOR = new Animator();

	public float alpha = 1f;

	public PotionVanish() {
		super("vanish", false, 0xA9F3A9);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);

		if (!(entityLivingBaseIn instanceof EntityPlayer))
			PacketHandler.NETWORK.sendToAll(new PacketVanishPotion(entityLivingBaseIn.getEntityId(), 0, 100));
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);

		if (!(entityLivingBaseIn instanceof EntityPlayer))
			PacketHandler.NETWORK.sendToAll(new PacketVanishPotion(entityLivingBaseIn.getEntityId()));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderItem(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if (player.isPotionActive(this)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void ai(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) return;

		EntityLivingBase potentialPotion = event.getEntityLiving().getRevengeTarget();
		if (potentialPotion != null && potentialPotion.isPotionActive(this)) {
			event.getEntityLiving().setRevengeTarget(null);
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void doRenderOverride(RenderLivingEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		EntityLivingBase entity = event.getEntity();

		boolean theyWalked = new Vec3d(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ).distanceTo(new Vec3d(event.getEntity().prevPosX, event.getEntity().prevPosY, event.getEntity().prevPosZ)) > 0.15;

		boolean renderingSelf = event.getEntity().getEntityId() == player.getEntityId();

		boolean override = false;
		boolean hide = false;

		if (player.isPotionActive(this)) {
			if (renderingSelf) {
				hide = true;
				new BasicAnimation<>(this, "alpha").to(0f).ease(Easing.easeOutQuint).duration(5).addTo(ANIMATOR);
			} else {
				new BasicAnimation<>(this, "alpha").to(1f).ease(Easing.easeOutQuint).duration(5).addTo(ANIMATOR);
			}
		}

		if (!renderingSelf && event.getEntity().isPotionActive(this)) {
			override = true;
			if (theyWalked) {
				new BasicAnimation<>(this, "alpha").to(1f).ease(Easing.easeOutQuint).duration(5).addTo(ANIMATOR);
			} else {
				hide = true;
				new BasicAnimation<>(this, "alpha").to(0f).ease(Easing.easeOutQuint).duration(5).addTo(ANIMATOR);
			}
		}

		if (!override) return;


		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, alpha);
		//GlStateManager.disableCull();
		event.getRenderer().getMainModel().swingProgress = entity.swingProgress;
		boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		event.getRenderer().getMainModel().isRiding = shouldSit;
		event.getRenderer().getMainModel().isChild = entity.isChild();

		try {
			float f = (float) ClientStuff.interpolateRotation.invoke(event.getRenderer(), new Object[]{entity.prevRenderYawOffset, entity.renderYawOffset, ClientTickHandler.getPartialTicks()});
			float f1 = (float) ClientStuff.interpolateRotation.invoke(event.getRenderer(), new Object[]{entity.prevRotationYawHead, entity.rotationYawHead, ClientTickHandler.getPartialTicks()});
			float f2 = f1 - f;

			if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
				EntityLivingBase entitylivingbase = (EntityLivingBase) entity.getRidingEntity();
				f = (float) ClientStuff.interpolateRotation.invoke(event.getRenderer(), new Object[]{entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, ClientTickHandler.getPartialTicks()});
				f2 = f1 - f;
				float f3 = MathHelper.wrapDegrees(f2);

				if (f3 < -85.0F) {
					f3 = -85.0F;
				}

				if (f3 >= 85.0F) {
					f3 = 85.0F;
				}

				f = f1 - f3;

				if (f3 * f3 > 2500.0F) {
					f += f3 * 0.2F;
				}
				f2 = f1 - f; // Forge: Fix MC-1207
			}

			float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * ClientTickHandler.getPartialTicks();
			ClientStuff.renderLivingAt.invoke(event.getRenderer(), new Object[]{entity, event.getX(), event.getY(), event.getZ()});
			float f8 = (float) ClientStuff.handleRotationFloat.invoke(event.getRenderer(), new Object[]{entity, ClientTickHandler.getPartialTicks()});
			ClientStuff.applyRotations.invoke(event.getRenderer(), new Object[]{entity, f8, f, ClientTickHandler.getPartialTicks()});
			float f4 = event.getRenderer().prepareScale(entity, ClientTickHandler.getPartialTicks());
			float f5 = 0.0F;
			float f6 = 0.0F;

			if (!entity.isRiding()) {
				f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * ClientTickHandler.getPartialTicks();
				f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - ClientTickHandler.getPartialTicks());

				if (entity.isChild()) {
					f6 *= 3.0F;
				}

				if (f5 > 1.0F) {
					f5 = 1.0F;
				}
			}

			GlStateManager.enableAlpha();
			event.getRenderer().getMainModel().setLivingAnimations(entity, f6, f5, ClientTickHandler.getPartialTicks());
			event.getRenderer().getMainModel().setRotationAngles(f6, f5, f8, f2, f7, f4, entity);

			if ((boolean) ClientStuff.renderOutlines.invoke(event.getRenderer())) {
				// TODO: boolean flag1 = this.setScoreTeamColor(entity);
				GlStateManager.enableColorMaterial();
				// TODO: GlStateManager.enableOutlineMode(this.getTeamColor(entity));

				if (!(boolean) ClientStuff.renderMarker.invoke(event.getRenderer())) {
					ClientStuff.renderModel.invoke(event.getRenderer(), new Object[]{entity, f6, f5, f8, f2, f7, f4});
				}

				if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
					ClientStuff.renderLayers.invoke(event.getRenderer(), new Object[]{entity, f6, f5, ClientTickHandler.getPartialTicks(), f8, f2, f7, f4});
				}

				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();

				// TODO: if (flag1) {
				// TODO: 	this.unsetScoreTeamColor();
				// TODO: }
			} else {
				boolean flag = (boolean) ClientStuff.setDoRenderBrightness.invoke(event.getRenderer(), new Object[]{entity, ClientTickHandler.getPartialTicks()});
				ClientStuff.renderModel.invoke(event.getRenderer(), new Object[]{entity, f6, f5, f8, f2, f7, f4});

				if (flag) {
					ClientStuff.unsetBrightness.invoke(event.getRenderer(), new Object[]{});
				}

				GlStateManager.depthMask(true);

				if (!hide)
					if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
						ClientStuff.renderLayers.invoke(event.getRenderer(), new Object[]{entity, f6, f5, ClientTickHandler.getPartialTicks(), f8, f2, f7, f4});
					}
			}

			GlStateManager.disableRescaleNormal();
		} catch (Exception exception) {
			Wizardry.LOGGER.error("Couldn\'t render entity", exception);
		}

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		event.setCanceled(true);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderShadowAndFire(EntityRenderShadowAndFireEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean iWalked = new Vec3d(player.posX, player.posY, player.posZ).distanceTo(new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)) > 0.2;
		if (!(event.entity instanceof EntityLivingBase)) return;

		if (event.entity.getEntityId() != player.getEntityId()) {
			if (((EntityLivingBase) event.entity).isPotionActive(this))
				event.override = true;
			else if (player.isPotionActive(this) && !iWalked) {
				event.override = true;
			}
		} else event.override = true;
	}

	private static class ClientStuff {
		public static Function2<RenderLivingBase, Object[], Object> interpolateRotation = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "interpolateRotation", "func_77034_a", float.class, float.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> renderLivingAt = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "renderLivingAt", "func_77039_a", EntityLivingBase.class, double.class, double.class, double.class);
		public static Function2<RenderLivingBase, Object[], Object> handleRotationFloat = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "handleRotationFloat", "func_77044_a", EntityLivingBase.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> applyRotations = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "applyRotations", "func_77043_a", EntityLivingBase.class, float.class, float.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> renderModel = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "renderModel", "func_77036_a", EntityLivingBase.class, float.class, float.class, float.class, float.class, float.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> renderLayers = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "renderLayers", "func_177093_a", EntityLivingBase.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> setDoRenderBrightness = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "setDoRenderBrightness", "func_177090_c", EntityLivingBase.class, float.class);
		public static Function2<RenderLivingBase, Object[], Object> unsetBrightness = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "unsetBrightness", "func_177091_f");
		public static Function2<RenderLivingBase, Object[], Object> setBrightness = MethodHandleHelper.wrapperForMethod(RenderLivingBase.class, "setBrightness", "func_177092_a", EntityLivingBase.class, float.class, boolean.class);
		public static Function1<Render, Object> renderOutlines = MethodHandleHelper.wrapperForGetter(Render.class, "renderOutlines", "field_188301_f", "e");
		public static Function1<RenderLivingBase, Object> renderMarker = MethodHandleHelper.wrapperForGetter(RenderLivingBase.class, "renderMarker", "field_188323_j", "bookmarkIndex");
		public static Function1<RenderLivingBase, Object> layerRenderers = MethodHandleHelper.wrapperForGetter(RenderLivingBase.class, "layerRenderers", "field_177097_h", "a");
	}
}
