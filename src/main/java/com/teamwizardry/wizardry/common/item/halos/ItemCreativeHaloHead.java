package com.teamwizardry.wizardry.common.item.halos;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCreativeHaloHead extends ItemModArmor implements IHalo {

	public ItemCreativeHaloHead() {
		super("halo_creative", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) return;

		try (ManaManager.CapManagerBuilder mgr = ManaManager.forObject(entityIn)) {
			mgr.setMaxMana(ConfigValues.creativeHaloBufferSize);
			mgr.setMaxBurnout(ConfigValues.creativeHaloBufferSize);
			mgr.setMana(ConfigValues.creativeHaloBufferSize);
			mgr.setBurnout(0);
		}
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

	@Override
	public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.addAll(getHaloTooltip(stack));
	}

	private boolean isInitialized; { isInitialized = true; }

	@Nonnull
	@Override
	public Item setMaxDamage(int maxDamageIn) {
		if (!isInitialized)
			return this;
		return super.setMaxDamage(maxDamageIn);
	}
}
