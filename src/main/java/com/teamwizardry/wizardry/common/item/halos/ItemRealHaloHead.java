package com.teamwizardry.wizardry.common.item.halos;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.IFakeHalo;
import com.teamwizardry.wizardry.api.item.IHalo;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemRealHaloHead extends ItemModArmor implements IFakeHalo, IHalo {

	public ItemRealHaloHead() {
		super("halo_real", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		CapManager manager = new CapManager(entityIn);
		if (manager.getMaxMana() != ConfigValues.realHaloBufferSize)
			manager.setMaxMana(ConfigValues.realHaloBufferSize);
		if (manager.getMaxBurnout() != ConfigValues.realHaloBufferSize)
			manager.setMaxBurnout(ConfigValues.realHaloBufferSize);
		if (!manager.isManaFull()) manager.addMana(manager.getMaxMana() * 0.001);
		if (!manager.isBurnoutEmpty()) manager.removeBurnout(manager.getMaxBurnout() * 0.001);
		if (manager.getMana() > ConfigValues.realHaloBufferSize) manager.setMana(ConfigValues.realHaloBufferSize);
		if (manager.getBurnout() > ConfigValues.realHaloBufferSize) manager.setBurnout(ConfigValues.realHaloBufferSize);
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

}
