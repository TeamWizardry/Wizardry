package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.events.EntityRenderShadowAndFireEvent;
import com.teamwizardry.wizardry.common.network.PacketVanishPotion;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
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

		PotionEffect pe = entityLivingBaseIn.getActivePotionEffect(ModPotions.VANISH);
		if (pe == null) return;
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, pe.getDuration(), 100, true, true));
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
	public void ai(LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) return;
		if (!(event.getEntityLiving() instanceof EntityLiving)) return;

		EntityLivingBase potentialPotion = ((EntityLiving) event.getEntityLiving()).getAttackTarget();
		if (potentialPotion != null && potentialPotion.isPotionActive(this)) {
			((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void hud(RenderGameOverlayEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().player.isPotionActive(this)) event.setCanceled(true);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void doRenderOverride(RenderLivingEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		EntityLivingBase entity = event.getEntity();

		boolean theyWalked = new Vec3d(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ).distanceTo(new Vec3d(event.getEntity().prevPosX, event.getEntity().prevPosY, event.getEntity().prevPosZ)) > 0.15;

		boolean renderingSelf = event.getEntity().getEntityId() == player.getEntityId();

		boolean hide = false;

		if (event.getEntity().isPotionActive(this)) event.setCanceled(true);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderShadowAndFire(EntityRenderShadowAndFireEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean iWalked = new Vec3d(player.posX, player.posY, player.posZ).distanceTo(new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)) > 0.2;
		if (!(event.entity instanceof EntityLivingBase)) return;

		if (((EntityLivingBase) event.entity).isPotionActive(this))
			event.override = true;
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
