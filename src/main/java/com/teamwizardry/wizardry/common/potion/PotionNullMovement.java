package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.wizardry.api.NullMovementInput;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by LordSaad.
 */
public class PotionNullMovement extends PotionMod {

	public PotionNullMovement() {
		super("null_movement", false, 0xFFFFFF);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (mc.player != null && mc.player.isPotionActive(ModPotions.NULL_MOVEMENT)) {
			if (!(player.movementInput instanceof NullMovementInput))
				player.movementInput = new NullMovementInput(player.movementInput);
		} else if (!(player.movementInput instanceof MovementInputFromOptions))
			player.movementInput = new MovementInputFromOptions(mc.gameSettings);
	}
}
