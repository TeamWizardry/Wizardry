package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by Saad on 8/27/2016.
 */
public class ItemJar extends ItemWizardry {

	public ItemJar() {
		super("jar");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			if (playerIn.isSneaking()) {
				if (ItemNBTHelper.getBoolean(itemStackIn, "fairy_inside", false)) {
					ItemNBTHelper.setBoolean(itemStackIn, "fairy_inside", false);
					int color = ItemNBTHelper.getInt(itemStackIn, "fairy_color", 0xFFFFFF);
					EntityFairy entity = new EntityFairy(worldIn, Color.getColor("idk", color), ItemNBTHelper.getInt(itemStackIn, "fairy_age", 0));
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					entity.setSad(true);
					worldIn.spawnEntityInWorld(entity);
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}
}
