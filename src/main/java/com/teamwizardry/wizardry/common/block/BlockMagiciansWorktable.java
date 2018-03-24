package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 6/12/2016.
 */
public class BlockMagiciansWorktable extends BlockModContainer {
	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
	public static final PropertyBool ISLEFTSIDE = PropertyBool.create("is_left_side");

	public BlockMagiciansWorktable() {
		super("magicians_worktable", Material.WOOD);
		setHardness(2.0F);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ISLEFTSIDE, true));
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing placerFacing = placer.getHorizontalFacing();
		EnumFacing offsetDir = placerFacing.rotateY();
		BlockPos part2Pos = pos.offset(offsetDir);
		Block block = worldIn.getBlockState(part2Pos).getBlock();
		if (block.isReplaceable(worldIn, part2Pos)) {
			worldIn.setBlockState(part2Pos, getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, false));
			return getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, true);
		} else {
			block = worldIn.getBlockState(part2Pos.offset(offsetDir.getOpposite(), 2)).getBlock();
			part2Pos = part2Pos.offset(offsetDir.getOpposite(), 2);
			if (block.isReplaceable(worldIn, part2Pos)) {
				worldIn.setBlockState(part2Pos, getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, true));
			} else {
				return Blocks.AIR.getDefaultState();
			}
			return getDefaultState().withProperty(FACING, placerFacing.getOpposite()).withProperty(ISLEFTSIDE, false);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		//The TE is linked here because it hasn't yet been created in Block#onBlockPlaced()
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity instanceof TileMagiciansWorktable)
			((TileMagiciansWorktable) entity).linkedTable = getOtherTableBlock(state, pos);
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		worldIn.setBlockToAir(getOtherTableBlock(state, pos));
	}

	private BlockPos getOtherTableBlock(IBlockState tablePart, BlockPos tablePartPos) {
		return tablePartPos.offset(tablePart.getValue(ISLEFTSIDE) ? tablePart.getValue(FACING).rotateYCCW() : tablePart.getValue(FACING).rotateY());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		playerIn.openGui(Wizardry.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ISLEFTSIDE);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ISLEFTSIDE, (meta & 4) != 0).withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = state.getValue(FACING).getHorizontalIndex();
		return state.getValue(ISLEFTSIDE) ? (facing | 4) : facing;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return (layer == BlockRenderLayer.CUTOUT) || (layer == BlockRenderLayer.TRANSLUCENT);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileMagiciansWorktable();
	}
}
