package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.client.render.block.TileManaMagnetRenderer;
import com.teamwizardry.wizardry.common.tile.TileManaMagnet;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 9/4/2016.
 */
public class BlockManaMagnet extends BlockModContainer {

	private static final AxisAlignedBB AABB_MANA_MAGNET = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.5625, 0.9375);

	public BlockManaMagnet() {
		super("mana_magnet", Material.WOOD);
		setHardness(2.0f);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB_MANA_MAGNET;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileManaMagnet.class, new TileManaMagnetRenderer());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileManaMagnet();
	}
}
