package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.block.TilePearlHolderRenderer;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Saad on 5/7/2016.
 */
public class BlockPearlHolder extends BlockModContainer {

	public BlockPearlHolder() {
		super("pearl_holder", Material.WOOD);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TilePearlHolder.class, new TilePearlHolderRenderer());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (!worldIn.isRemote) {
			TilePearlHolder te = getTE(worldIn, pos);

			if ((te.pearl == null || te.pearl.isEmpty())) {
				if (heldItem.getItem() == ModItems.MANA_ORB || heldItem.getItem() == ModItems.PEARL_NACRE) {
					te.pearl = heldItem.copy();
					te.pearl.setCount(1);
					heldItem.shrink(1);
				} else return false;

			} else {
				ItemStack stack = te.pearl.copy();
				te.pearl = ItemStack.EMPTY;
				if (playerIn.inventory.addItemStackToInventory(stack)) playerIn.openContainer.detectAndSendChanges();
				else {
					EntityItem entityItem = new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), stack);
					worldIn.spawnEntity(entityItem);
				}
			}
			te.markDirty();
		}
		return true;
	}

	private TilePearlHolder getTE(World world, BlockPos pos) {
		return (TilePearlHolder) world.getTileEntity(pos);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TilePearlHolder();
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
