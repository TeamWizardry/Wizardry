package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.fluid.FluidMana;
import com.teamwizardry.wizardry.common.fluid.FluidNacre;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassOrb extends ItemMod {

	public ItemGlassOrb() {
		super("glass_orb");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());
		if (state.getBlock() == FluidNacre.instance.getBlock()) {
			ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE);
			entityItem.setEntityItemStack(newStack);
		} else if (state.getBlock() == FluidMana.instance.getBlock()) {
			ItemStack newStack = new ItemStack(ModItems.MANA_ORB);
			entityItem.setEntityItemStack(newStack);
		}
		return super.onEntityItemUpdate(entityItem);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
