package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.ICape;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Demoniaque on 8/30/2016.
 */
public class ItemCapeChest extends ItemModArmor implements ICape {

	public ItemCapeChest() {
		super("cape", ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof EntityPlayer && EntityEquipmentSlot.CHEST.getIndex() == itemSlot)
			tickCape(stack);
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(getCapeTooltip(stack));
	}
}
