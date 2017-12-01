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

public class ItemFakeHaloHead extends ItemModArmor implements IFakeHalo, IHalo {

	public ItemFakeHaloHead() {
		super("halo_fake", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		CapManager manager = new CapManager(entityIn);
		if (manager.getMaxMana() != ConfigValues.crudeHaloBufferSize)
			manager.setMaxMana(ConfigValues.crudeHaloBufferSize);
		if (manager.getMaxBurnout() != ConfigValues.crudeHaloBufferSize)
			manager.setMaxBurnout(ConfigValues.crudeHaloBufferSize);
		if (manager.getMana() > ConfigValues.crudeHaloBufferSize) manager.setMana(ConfigValues.crudeHaloBufferSize);
		if (manager.getBurnout() > ConfigValues.crudeHaloBufferSize)
			manager.setBurnout(ConfigValues.crudeHaloBufferSize);
		if (!manager.isBurnoutEmpty()) manager.removeBurnout(manager.getMaxBurnout() * 0.001);
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

}
