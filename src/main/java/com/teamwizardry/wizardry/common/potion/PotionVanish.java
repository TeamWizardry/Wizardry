package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.api.events.EntityRenderShadowAndFireEvent;
import com.teamwizardry.wizardry.api.events.EntityRenderToPlayerEvent;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class PotionVanish extends PotionMod {

	private Function2<Render, Object[], Object> bindEntityTextureHandler = MethodHandleHelper.wrapperForMethod(Render.class, new String[]{"d", "func_180548_c", "bindEntityTexture"}, Entity.class);

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
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 1f, 1);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 1f, 1);
	}

	@Override
	public void performEffect(@NotNull EntityLivingBase entity, int amplifier) {
		boolean walked = entity.posX != entity.prevPosX || entity.posY != entity.prevPosY || entity.posZ != entity.prevPosZ;
		if (walked) {
			List<Entity> entities = entity.world.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getPosition()).expand(amplifier * 10, amplifier * 10, amplifier * 10));
			for (Entity entity1 : entities) {
				if (entity1 == null) return;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderModelEvent(EntityRenderToPlayerEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.isPotionActive(this)) {
			PotionEffect effect = player.getActivePotionEffect(this);
			if (effect == null) return;
			float time = effect.getDuration();
			boolean walked = player.posX != player.prevPosX || player.posY != player.prevPosY || player.posZ != player.prevPosZ;
			if (walked) {
				event.override = true;

				//if ((Boolean) bindEntityTextureHandler.invoke(event.renderLivingBase, new Object[]{event.entity})) return;
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

				Minecraft.getMinecraft().player.sendChatMessage(1f - (3f / time) + " - " + time);
				GlStateManager.color(1, 1, 1, MathHelper.clamp(1f - (3f / time), 0, 1));

				event.renderLivingBase.getMainModel().render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);

				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		} else {
			if (event.entity != null && event.entity.isPotionActive(this)) {
				boolean walked = event.entity.posX != event.entity.prevPosX || event.entity.posY != event.entity.prevPosY || event.entity.posZ != event.entity.prevPosZ;
				if (!walked) event.override = true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderShadowAndFire(EntityRenderShadowAndFireEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.isPotionActive(this)) {
			boolean walked = player.posX != player.prevPosX || player.posY != player.prevPosY || player.posZ != player.prevPosZ;
			if (walked) event.override = true;
		} else {
			if (event.entity instanceof EntityLivingBase && ((EntityLivingBase) event.entity).isPotionActive(this)) {
				boolean walked = event.entity.posX != event.entity.prevPosX || event.entity.posY != event.entity.prevPosY || event.entity.posZ != event.entity.prevPosZ;
				if (!walked) event.override = true;
			}
		}
	}
}
