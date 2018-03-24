package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/27/2016.
 */
public class BlockCloud extends BlockMod {

	public static final PropertyBool HAS_LIGHT_VALUE = PropertyBool.create("has_light_value");

	public BlockCloud() {
		super("cloud", Material.CLOTH);
		setHardness(0.5f);
		setSoundType(SoundType.CLOTH);
		setDefaultState(blockState.getBaseState().withProperty(HAS_LIGHT_VALUE, false));
		setLightOpacity(0);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = world.getBlockState(pos.offset(side));
		return state != iblockstate;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HAS_LIGHT_VALUE);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HAS_LIGHT_VALUE, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(HAS_LIGHT_VALUE) ? 1 : 0;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		boolean hasLightValue = state.getValue(HAS_LIGHT_VALUE);
		for (int i = 1; i < pos.getY(); i++) {
			if (hasLightValue) {
				if (!world.isAirBlock(pos.down(i))) {
					hasLightValue = false;
					state = state.withProperty(HAS_LIGHT_VALUE, false);
					break;
				}
			}
		}
		for (int i = 1; i < 255 - pos.getY(); i++)
			if (world.getBlockState(pos.up(i)) == getDefaultState().withProperty(HAS_LIGHT_VALUE, true))
				world.setBlockState(pos.up(i), getDefaultState().withProperty(HAS_LIGHT_VALUE, false), 2);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		boolean isLowest = true;
		for (int i = 1; i < pos.getY(); i++) {
			if (!world.isAirBlock(pos.down(i)))
				isLowest = false;
		}
		if (isLowest) {
			for (int i = 1; i < 255 - pos.getY(); i++)
				if (world.getBlockState(pos.up(i)) == getDefaultState().withProperty(HAS_LIGHT_VALUE, true))
					world.setBlockState(pos.up(i), getDefaultState().withProperty(HAS_LIGHT_VALUE, false), 2);
		}
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return state.getValue(HAS_LIGHT_VALUE) ? 15 : 0;
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		// NO-OP
	}

	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
		return true;
	}

	@Override
	public void onLanded(World worldIn, Entity entityIn) {
		if (entityIn.motionY < -0.5) {
			entityIn.motionY = -entityIn.motionY * 0.625;

			if (!(entityIn instanceof EntityLivingBase))
				entityIn.motionY *= 0.8;
		} else entityIn.motionY = 0.0;
	}
}
