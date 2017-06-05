package com.teamwizardry.wizardry.api.util;

/**
 * Created by LordSaad.
 */
public class StructUV {

	public double minU = 0, minV = 0, maxU = 0, maxV = 0;

	public StructUV(double minU, double minV, double maxU, double maxV) {
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	public StructUV(double minU, double minV, double maxU, double maxV, double textureWidth, double textureHeight) {
		this.minU = minU / textureWidth;
		this.minV = minV / textureHeight;
		this.maxU = maxU / textureWidth;
		this.maxV = maxV / textureHeight;
	}

}
