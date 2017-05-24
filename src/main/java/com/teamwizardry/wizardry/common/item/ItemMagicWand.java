package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IManaFaucet;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.block.TileManaSink;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class ItemMagicWand extends ItemMod implements IGlowOverlayable {

	public ItemMagicWand() {
		super("magic_wand");
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, NBT.TAG_OVERLAY), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItemMainhand();

		if (player.isSneaking()) {
			if (!worldIn.isRemote) {
				EntityFairy fairy = new EntityFairy(worldIn, Color.RED, 10);
				fairy.setPosition(player.posX, player.posY, player.posZ);
				worldIn.spawnEntity(fairy);
			}
		}

		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile == null || (!(tile instanceof TileManaSink) && !(tile instanceof IStructure) && !(tile instanceof IManaFaucet))) {
			ItemNBTHelper.removeEntry(stack, "link_block");
			return EnumActionResult.PASS;
		}

		if (tile instanceof IManaFaucet) {
			if (player.isSneaking()) {
				ItemNBTHelper.setLong(stack, "link_block", pos.toLong());
			}
		} else if (tile instanceof TileManaSink) {
			if (player.isSneaking()) {
				if (ItemNBTHelper.verifyExistence(stack, "link_block")) {
					BlockPos sink = BlockPos.fromLong(ItemNBTHelper.getLong(stack, "link_block", 0));
					if (sink.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= ConfigValues.manaBatteryDistance) {
						((TileManaSink) tile).faucetPos = sink;
						tile.markDirty();
						ItemNBTHelper.removeEntry(stack, "link_block");
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}

		return EnumActionResult.PASS;
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
