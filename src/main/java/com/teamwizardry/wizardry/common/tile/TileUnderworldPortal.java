package com.teamwizardry.wizardry.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class TileUnderworldPortal extends TileEntity {
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderFace(EnumFacing p_184313_1_) {
		return true;
	}
}
