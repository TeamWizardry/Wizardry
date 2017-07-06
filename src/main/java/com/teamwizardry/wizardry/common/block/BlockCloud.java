package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/27/2016.
 */
public class BlockCloud extends BlockMod {

	public BlockCloud() {
		super("cloud", Material.CLOTH);
		setHardness(0.5f);
		setSoundType(SoundType.CLOTH);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return canProduceLight(world, pos) ? 15 : 0;
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

	public boolean canProduceLight(IBlockAccess world, BlockPos pos) {
		for (int i = pos.getY(); i > 0; i--)
			if (!world.isAirBlock(pos.down(i))) return false;

		for (int i = pos.getY(); i < 255; i++)
			if (!world.isAirBlock(pos.up(i))) return true;

		return true;
	}
}
