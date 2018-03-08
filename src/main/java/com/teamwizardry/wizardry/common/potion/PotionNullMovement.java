package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.api.NullMovementInput;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionNullMovement extends PotionBase {

	public PotionNullMovement() {
		super("null_movement", true, 0x111111);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		entityLivingBaseIn.getEntityData().setFloat("rot_yaw", entityLivingBaseIn.rotationYaw);
		entityLivingBaseIn.getEntityData().setFloat("rot_pitch", entityLivingBaseIn.rotationPitch);
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		entityLivingBaseIn.getEntityData().removeTag("rot_yaw");
		entityLivingBaseIn.getEntityData().removeTag("rot_pitch");
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (player == null) return;
		if (player.isPotionActive(ModPotions.NULL_MOVEMENT)) {
			//player.rotationYaw = player.getEntityData().getFloat("rot_yaw");
			//player.rotationPitch = player.getEntityData().getFloat("rot_pitch");
			//player.prevRotationYaw = player.getEntityData().getFloat("rot_yaw");
			//player.prevRotationPitch = player.getEntityData().getFloat("rot_pitch");

			if (!(player.movementInput instanceof NullMovementInput))
				player.movementInput = new NullMovementInput(player.movementInput);
		} else if (!(player.movementInput instanceof MovementInputFromOptions))
			player.movementInput = new MovementInputFromOptions(mc.gameSettings);
	}

	@SubscribeEvent
	public void onAnotherTick(TickEvent.PlayerTickEvent event) {
		if (event.player.isPotionActive(ModPotions.NULL_MOVEMENT)) {
			//event.player.rotationYaw = event.player.getEntityData().getFloat("rot_yaw");
			//event.player.rotationPitch = event.player.getEntityData().getFloat("rot_pitch");
			//event.player.prevRotationYaw = event.player.getEntityData().getFloat("rot_yaw");
			//event.player.prevRotationPitch = event.player.getEntityData().getFloat("rot_pitch");
		}
	}
}
