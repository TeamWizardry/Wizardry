package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.ICape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

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

	@NotNull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (ItemNBTHelper.verifyExistence(stack, "owner")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "owner");
			if (uuid != null) {
				Entity owner = player.world.getPlayerEntityByUUID(uuid);
				tooltip.add("owner: " + (owner == null ? "null" : owner.getName()));
			}
		}

		if (ItemNBTHelper.verifyExistence(stack, "thief")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "thief");
			if (uuid != null) {
				Entity thief = player.world.getPlayerEntityByUUID(uuid);
				tooltip.add("thief: " + (thief == null ? "null" : thief.getName()));
			}
		}
		tooltip.add("time: " + ItemNBTHelper.getInt(stack, "time", 0));
		tooltip.add("buffer: " + ItemNBTHelper.getInt(stack, "buffer", 0));
		tooltip.add("tick: " + ItemNBTHelper.getInt(stack, "tick", 0));
	}
}
