package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.item.ICape;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Demoniaque on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemCapeBauble extends ItemModBauble implements ICape {

	public ItemCapeBauble() {
		super("cape");
		setMaxStackSize(1);
	}

	@Optional.Method(modid = "baubles")
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		tickCape(itemstack);
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(getCapeTooltip(stack));
	}
}
