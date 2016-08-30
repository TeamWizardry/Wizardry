package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Saad on 8/30/2016.
 */
public class ItemCape extends ItemArmor {

	public ItemCape() {
		super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);
		setRegistryName("cape");
		setUnlocalizedName("cape");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		setMaxDamage(0);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemstack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

		if (itemstack == null) {
			playerIn.setItemStackToSlot(EntityEquipmentSlot.CHEST, itemStackIn.copy());
			itemStackIn.stackSize = 0;
			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult(EnumActionResult.FAIL, itemStackIn);
		}
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return null;
	}
}