package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 8/30/2016.
 */
public class ItemCape extends ItemModArmor {

	public ItemCape() {
		super("cape", ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		setMaxDamage(0);
	}

    @NotNull
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
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}
}
