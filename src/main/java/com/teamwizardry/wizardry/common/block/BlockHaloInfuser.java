package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.common.tile.TileHaloInfuser;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 6/10/2016.
 */
public class BlockHaloInfuser extends BlockModContainer implements IStructure {

	private static final AxisAlignedBB AABB_CRAFTING_PLATE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);

	public BlockHaloInfuser() {
		super("halo_infuser", Material.WOOD);
		setHardness(2.0F);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return 15;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileHaloInfuser();
	}

	private TileHaloInfuser getTE(IBlockAccess world, BlockPos pos) {
		return (TileHaloInfuser) world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		//if (isStructureComplete(worldIn, pos)) {
			TileHaloInfuser infuser = getTE(worldIn, pos);
			if (infuser == null) return heldItem.isEmpty();

			if (!infuser.getHalo().isEmpty()) {
				playerIn.setHeldItem(hand, infuser.extractHalo());
				playerIn.openContainer.detectAndSendChanges();
			} else if (heldItem.getItem() == ModItems.FAKE_HALO) {
				infuser.setHalo(heldItem);
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
				playerIn.openContainer.detectAndSendChanges();
			}
		//}
		return heldItem.isEmpty();
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB_CRAFTING_PLATE;
	}

	@Override
	public CachedStructure getStructure() {
		return ModStructures.INSTANCE.structures.get("crafting_altar");
	}

	@Override
	public Vec3i offsetToCenter() {
		return new Vec3i(4, 1, 4);
	}
}
