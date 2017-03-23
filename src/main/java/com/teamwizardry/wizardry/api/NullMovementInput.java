package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

/**
 * Created by LordSaad.
 */
public class NullMovementInput extends MovementInput {
	private MovementInput original;

	public NullMovementInput(MovementInput original) {
		this.original = original;
	}

	@Override
	public void updatePlayerMoveState() {
		original.updatePlayerMoveState();

		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player.isPotionActive(ModPotions.PHASE)) {
			this.moveForward = 0;
			this.moveStrafe = 0;
			this.jump = false;
			this.sneak = false;

			mc.mouseHelper.deltaX = mc.mouseHelper.deltaY = 0;
		} else {
			this.moveForward = original.moveForward;
			this.moveStrafe = original.moveStrafe;
			this.jump = original.jump;
			this.sneak = original.sneak;
		}
	}
}
