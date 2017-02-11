package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.common.entity.EntityJumpPad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 8/28/2016.
 */
public class ItemFairyDust extends ItemWizardry {

	public ItemFairyDust() {
		super("fairy_dust");
		setMaxStackSize(64);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		EntityJumpPad pad = new EntityJumpPad(worldIn);
		pad.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		worldIn.spawnEntity(pad);
		stack.stackSize--;
		return EnumActionResult.SUCCESS;
	}
}
