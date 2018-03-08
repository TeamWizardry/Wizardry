package com.teamwizardry.wizardry.api;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

/**
 * Created by Demoniaque.
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
		this.moveForward = 0;
		this.moveStrafe = 0;
		this.jump = false;
		this.sneak = false;
		this.leftKeyDown = false;
		this.rightKeyDown = false;

		mc.mouseHelper.deltaX = mc.mouseHelper.deltaY = 0;

	}
}
