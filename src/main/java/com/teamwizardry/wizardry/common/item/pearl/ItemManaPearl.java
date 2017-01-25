package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.common.item.ItemWizardry;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemManaPearl extends ItemWizardry {

	public ItemManaPearl() {
		super("mana_pearl");
		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (stack != null) {
			if (world.getBlockState(pos).getBlock() == ModBlocks.PEDESTAL) {
				TilePedestal pedestal = (TilePedestal) world.getTileEntity(pos);
				if (pedestal != null) {
					pedestal.pearl = stack;
					stack.stackSize--;
					pedestal.markDirty();
				}
			}
		}
		return EnumActionResult.PASS;
	}
}
