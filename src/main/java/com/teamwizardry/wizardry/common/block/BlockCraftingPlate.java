package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.client.render.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate.ClusterObject;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 6/10/2016.
 */
public class BlockCraftingPlate extends BlockModContainer implements IManaSink {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875);

	public BlockCraftingPlate() {
		super("crafting_plate", Material.ROCK);
		setHardness(1.0F);
		setLightLevel(15);
		setSoundType(SoundType.STONE);
		TileMod.registerTile(TileCraftingPlate.class, "crafting_plate");
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingPlate.class, new TileCraftingPlateRenderer());
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileCraftingPlate();
	}

	private TileCraftingPlate getTE(World world, BlockPos pos) {
		return (TileCraftingPlate) world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileCraftingPlate plate = getTE(worldIn, pos);
			if (plate.isCrafting) return false;
			if ((heldItem != null) && (heldItem.stackSize > 0)) {
				ItemStack stack = heldItem.copy();
				stack.stackSize = 1;
				--heldItem.stackSize;
				plate.inventory.add(new ClusterObject(stack, worldIn, plate.random));
				playerIn.openContainer.detectAndSendChanges();

			} else if (plate.output != null) {
				playerIn.setHeldItem(hand, plate.output.copy());
				plate.output = null;
				playerIn.openContainer.detectAndSendChanges();

			} else if (!plate.inventory.isEmpty()) {
				playerIn.setHeldItem(hand, plate.inventory.remove(plate.inventory.size() - 1).stack);
				playerIn.openContainer.detectAndSendChanges();
			}
		}
		worldIn.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
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
