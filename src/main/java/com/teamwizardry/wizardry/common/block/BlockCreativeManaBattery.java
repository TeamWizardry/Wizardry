package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.structure.WizardryStructure;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCreativeManaBattery extends BlockModContainer implements IStructure {

	public BlockCreativeManaBattery() {
		super("creative_mana_battery", Material.GLASS);
		setSoundType(SoundType.GLASS);
		getItemForm().setMaxStackSize(1);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileManaBattery();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isCreative() && playerIn.isSneaking()) {
			buildStructure(worldIn, pos);
		} else {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (!(tile instanceof TileManaBattery)) return false;
			else {
				((TileManaBattery) tile).revealStructure = !((TileManaBattery) tile).revealStructure;
				tile.markDirty();
			}
		}
		return true;
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return 15;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public WizardryStructure getStructure() {
		return ModStructures.structureManager.getStructure(ModStructures.MANA_BATTERY);
	}
}
