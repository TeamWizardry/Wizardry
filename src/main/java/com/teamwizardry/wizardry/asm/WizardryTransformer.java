package com.teamwizardry.wizardry.asm;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Created by LordSaad.
 */
public class WizardryTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.equals("aax")) {
			return doMagic(basicClass, true);
		}
		if (name.equals("net.minecraft.entity.player")) {
			return doMagic(basicClass, false);
		}

		return basicClass;
	}

	public byte[] doMagic(byte[] bytes, boolean obfuscated) {
		String targetMethod = "";

		if (obfuscated) targetMethod = "n";
		else targetMethod = "onUpdate";

		return bytes;
	}
}
