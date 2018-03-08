package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.client.render.block.TileHaloInfuserRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@TileRegister("halo_infuser")
@TileRenderer(TileHaloInfuserRenderer.class)
public class TileHaloInfuser extends TileMod {

	@Module
	public ModuleInventory haloInv = new ModuleInventory(1);

	public ItemStack getHalo() {
		return haloInv.getHandler().getStackInSlot(0);
	}

	public void setHalo(ItemStack inv) {
		this.haloInv.getHandler().setStackInSlot(0, inv);
	}

	public ItemStack extractHalo() {
		return haloInv.getHandler().extractItem(0, 1, false);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 25;
	}
}
