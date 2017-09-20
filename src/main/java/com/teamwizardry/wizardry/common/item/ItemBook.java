package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.advancement.IPickupAchievement;
import com.teamwizardry.wizardry.common.advancement.ModAdvancements;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() != ModItems.BOOK) return ActionResult.newResult(EnumActionResult.FAIL, stack);
		int slot = Utils.getSlotFor(playerIn, stack);
		if (slot == -1) return ActionResult.newResult(EnumActionResult.FAIL, stack);
		if (worldIn.isRemote)
			playerIn.openGui(Wizardry.instance, 1, worldIn, slot, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	public Advancement getAdvancementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		return ModAdvancements.BOOK;
	}
}
