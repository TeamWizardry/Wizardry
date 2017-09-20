package com.teamwizardry.wizardry.common.item;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.librarianlib.features.base.item.IGlowingItem;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.block.TileManaFaucet;
import com.teamwizardry.wizardry.api.block.TileManaSink;
import com.teamwizardry.wizardry.client.fx.LibParticles;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMagicWand extends ItemMod implements IGlowingItem {

	public ItemMagicWand() {
		super("magic_wand");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItemMainhand();

		// TODO: are we keeping this stuff? If we are, it needs to be fixed to work serverside too
//		if (GuiScreen.isAltKeyDown()) {
//			for (ItemStack stack1 : player.inventory.mainInventory) {
//				if (stack1.getItem() != ModItems.CAPE) continue;
//				if (!stack1.isEmpty() && stack1.getItem() == ModItems.CAPE) {
//					ItemNBTHelper.setInt(stack1, "time", ItemNBTHelper.getInt(stack1, "time", 0) + 100);
//					player.sendMessage(new TextComponentString(new CapManager(player).getMana() + "/" + new CapManager(player).getMaxMana()));
//				}
//			}
//		}
//
//		if (GuiScreen.isCtrlKeyDown()) {
//			TileEntity tile = worldIn.getTileEntity(pos);
//			if (tile instanceof TileManaSink) {
//				player.sendMessage(new TextComponentString(((TileManaSink) tile).cap.getMana() + "/" + ((TileManaSink) tile).cap.getMaxMana()));
//				return EnumActionResult.PASS;
//			}
//			if (tile instanceof TileManaFaucet) {
//				player.sendMessage(new TextComponentString(((TileManaFaucet) tile).cap.getMana() + "/" + ((TileManaFaucet) tile).cap.getMaxMana()));
//				return EnumActionResult.PASS;
//			}
//			if (!worldIn.isRemote) {
//				EntityFairy fairy = new EntityFairy(worldIn, Color.RED, 10);
//				fairy.setPosition(player.posX, player.posY, player.posZ);
//				worldIn.spawnEntity(fairy);
//				return EnumActionResult.PASS;
//			}
//		}

		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile == null || (!(tile instanceof TileManaSink) && !(tile instanceof IStructure) && !(tile instanceof TileManaFaucet))) {
			ItemNBTHelper.removeEntry(stack, "link_block");
			return EnumActionResult.PASS;
		}

		if (tile instanceof TileManaFaucet) {
			if (player.isSneaking()) {
				if (worldIn.isRemote) {
					LibParticles.STRUCTURE_BEACON(worldIn, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.CYAN);
				}
				ItemNBTHelper.setLong(stack, "link_block", pos.toLong());
			}
		} else if (tile instanceof TileManaSink) {
			if (player.isSneaking()) {
				if (ItemNBTHelper.verifyExistence(stack, "link_block")) {
					BlockPos sink = BlockPos.fromLong(ItemNBTHelper.getLong(stack, "link_block", 0));
					if (sink.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= ConfigValues.manaBatteryLinkDistance) {
						((TileManaSink) tile).faucetPos = sink;
						tile.markDirty();
						ItemNBTHelper.removeEntry(stack, "link_block");
						if (worldIn.isRemote) {
							LibParticles.STRUCTURE_BEACON(worldIn, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.CYAN);
							LibParticles.STRUCTURE_BEACON(worldIn, new Vec3d(sink).addVector(0.5, 0.5, 0.5), Color.CYAN);
						}
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}
		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int packedGlowCoords(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return 0xf000f0;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public IBakedModel transformToGlow(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return IGlowingItem.Helper.wrapperBake(iBakedModel, false, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldDisableLightingForGlow(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return true;
	}
}
