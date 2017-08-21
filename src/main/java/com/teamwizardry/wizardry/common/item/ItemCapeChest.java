package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.ICape;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 8/30/2016.
 */
public class ItemCapeChest extends ItemModArmor implements ICape {

	public ItemCapeChest() {
		super("cape", ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof EntityPlayer && EntityEquipmentSlot.CHEST.getIndex() == itemSlot)
			tickCape(stack, (EntityLivingBase) entityIn);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (ItemNBTHelper.verifyExistence(stack, "owner")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "owner");
			if (uuid != null) {
				Entity owner = null;
				if (worldIn != null) {
					owner = worldIn.getPlayerEntityByUUID(uuid);
				}
				tooltip.add("owner: " + (owner == null ? "null" : owner.getName()));
			}
		}

		if (ItemNBTHelper.verifyExistence(stack, "thief")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "thief");
			if (uuid != null) {
				Entity thief = worldIn != null ? worldIn.getPlayerEntityByUUID(uuid) : null;
				tooltip.add("thief: " + (thief == null ? "null" : thief.getName()));
			}
		}
		tooltip.add("time: " + ItemNBTHelper.getInt(stack, "time", 0));
		tooltip.add("buffer: " + ItemNBTHelper.getInt(stack, "buffer", 0));
		tooltip.add("tick: " + ItemNBTHelper.getInt(stack, "tick", 0));
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}
}
