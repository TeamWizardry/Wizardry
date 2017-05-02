package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.achievement.IPickupAchievement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 6/12/2016.
 */
public class ItemBook extends ItemMod implements IPickupAchievement {

	public ItemBook() {
		super("book");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		playerIn.openGui(Wizardry.instance, 1, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	public Achievement getAchievementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		return Achievements.BOOK;
	}
}
