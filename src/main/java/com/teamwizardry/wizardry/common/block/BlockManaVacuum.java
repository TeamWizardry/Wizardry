package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.structure.WizardryStructure;
import com.teamwizardry.wizardry.common.tile.TileManaVacuum;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockManaVacuum extends BlockModContainer implements IStructure
{
	public BlockManaVacuum()
	{
		super("mana_vacuum", Material.IRON);
		setHardness(2);
		setResistance(15);
		getItemForm().setMaxStackSize(1);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileManaVacuum();
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return FULL_BLOCK_AABB;
	}
	
	@Override
	public WizardryStructure getStructure() {
		return ModStructures.structureManager.getStructure(ModStructures.CRAFTING_PLATE);
	}
}
