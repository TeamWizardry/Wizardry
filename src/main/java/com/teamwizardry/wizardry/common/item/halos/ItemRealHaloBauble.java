package com.teamwizardry.wizardry.common.item.halos;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.IFakeHalo;
import com.teamwizardry.wizardry.api.item.IHalo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemRealHaloBauble extends ItemModBauble implements IFakeHalo, IHalo {

	public ItemRealHaloBauble() {
		super("halo_real");
		setMaxStackSize(1);
	}

	@Override
	public void onWornTick(@NotNull ItemStack stack, @NotNull EntityLivingBase player) {
		CapManager manager = new CapManager(player);
		if (manager.getMaxMana() != ConfigValues.realHaloBufferSize)
			manager.setMaxMana(ConfigValues.realHaloBufferSize);
		if (manager.getMaxBurnout() != ConfigValues.realHaloBufferSize)
			manager.setMaxBurnout(ConfigValues.realHaloBufferSize);
		if (!manager.isManaFull()) manager.addMana(manager.getMaxMana() * 0.001);
		if (!manager.isBurnoutEmpty()) manager.removeBurnout(manager.getMaxBurnout() * 0.001);
		if (manager.getMana() > ConfigValues.realHaloBufferSize) manager.setMana(ConfigValues.realHaloBufferSize);
		if (manager.getBurnout() > ConfigValues.realHaloBufferSize) manager.setBurnout(ConfigValues.realHaloBufferSize);
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@NotNull ItemStack itemStack) {
		return BaubleType.HEAD;
	}
}
