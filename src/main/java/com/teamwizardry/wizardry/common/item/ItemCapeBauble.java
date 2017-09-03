package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.ICape;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 8/30/2016.
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
		tickCape(itemstack, player);
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if (ItemNBTHelper.verifyExistence(stack, "owner")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "owner");
			if (uuid != null) {
				Entity owner = world.getPlayerEntityByUUID(uuid);
				tooltip.add("owner: " + (owner == null ? "null" : owner.getName()));
			}
		}

		if (ItemNBTHelper.verifyExistence(stack, "thief")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "thief");
			if (uuid != null) {
				Entity thief = world.getPlayerEntityByUUID(uuid);
				tooltip.add("thief: " + (thief == null ? "null" : thief.getName()));
			}
		}
		tooltip.add("time: " + ItemNBTHelper.getInt(stack, "time", 0));
		tooltip.add("buffer: " + ItemNBTHelper.getInt(stack, "buffer", 0));
		tooltip.add("tick: " + ItemNBTHelper.getInt(stack, "tick", 0));
	}
}
