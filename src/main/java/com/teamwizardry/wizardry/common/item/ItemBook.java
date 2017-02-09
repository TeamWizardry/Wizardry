package com.teamwizardry.wizardry.common.item;

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
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 6/12/2016.
 */
public class ItemBook extends ItemWizardry implements IPickupAchievement {

	public ItemBook() {
		super("book");
		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.openGui(Wizardry.instance, 1, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public Achievement getAchievementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		return Achievements.BOOK;
	}
}
