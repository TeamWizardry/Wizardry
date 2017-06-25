package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer;
import com.teamwizardry.wizardry.common.tile.TileLight;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by LordSaad.
 */
public class BlockLight extends BlockModContainer {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

	public BlockLight() {
		super("light", Material.CLOTH);
		setHardness(0f);
		setSoundType(SoundType.CLOTH);
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @NotNull IBlockAccess worldIn, @NotNull BlockPos pos) {
		return NULL_AABB;
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return null;
	}

	@Override
	public ModCreativeTab getCreativeTab() {
		return null;
	}

	@NotNull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@NotNull
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, @NotNull IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	public int getLightValue(@NotNull IBlockState state, IBlockAccess world, @NotNull BlockPos pos) {
		return 15;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState iBlockState) {
		return new TileLight();
	}
}
