package com.teamwizardry.wizardry.common.item.halos;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemCreativeHaloHead extends ItemModArmor implements IHalo {

	public ItemCreativeHaloHead() {
		super("halo_creative", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		CapManager manager = new CapManager(entityIn).setManualSync(true);

		manager.setMaxMana(ConfigValues.creativeHaloBufferSize);
		manager.setMaxBurnout(ConfigValues.creativeHaloBufferSize);
		manager.setMana(ConfigValues.creativeHaloBufferSize);
		manager.setBurnout(0);

		if (manager.isSomethingChanged())
			manager.sync();
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

}
