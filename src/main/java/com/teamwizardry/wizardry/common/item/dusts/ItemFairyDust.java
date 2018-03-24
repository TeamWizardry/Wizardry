package com.teamwizardry.wizardry.common.item.dusts;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.common.entity.EntityJumpPad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/28/2016.
 */
public class ItemFairyDust extends ItemMod {

	public ItemFairyDust() {
		super("fairy_dust");
		setMaxStackSize(64);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer entityPlayer, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		EntityJumpPad pad = new EntityJumpPad(world);
		pad.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		world.spawnEntity(pad);
		entityPlayer.getHeldItem(hand).setCount(entityPlayer.getHeldItem(hand).getCount() - 1);
		return EnumActionResult.SUCCESS;
	}
}
