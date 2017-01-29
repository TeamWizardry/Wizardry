package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends ItemWizardry implements Infusable, Explodable, INacreColorable {

	public ItemNacrePearl() {
		super("nacre_pearl");
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) return;

		colorableOnUpdate(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL))
			ItemNBTHelper.setInt(entityItem.getEntityItem(), "tick", ItemNBTHelper.getInt(entityItem.getEntityItem(), "tick", 0) + 1);
		if (!entityItem.world.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		int ticks = ItemNBTHelper.getInt(stack, "tick", 0) / 20;
		TextFormatting formatting;
		if (ticks <= 20 || ticks >= 100) formatting = TextFormatting.RED;
		else if (ticks <= 45 || ticks >= 75) formatting = TextFormatting.GOLD;
		else formatting = TextFormatting.GREEN;
		tooltip.add("Quality: " + formatting + ticks);
	}
}
