package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemManaPearl extends ItemMod {

	public ItemManaPearl() {
		super("mana_pearl");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (stack != null && world.getBlockState(pos).getBlock() instanceof IManaAcceptor) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("link_x", pos.getX());
			compound.setInteger("link_y", pos.getY());
			compound.setInteger("link_z", pos.getZ());
			stack.setTagCompound(compound);
		}
		return EnumActionResult.PASS;
	}
}
