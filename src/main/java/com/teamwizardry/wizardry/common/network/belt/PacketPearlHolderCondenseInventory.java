package com.teamwizardry.wizardry.common.network.belt;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.item.wheels.IPearlWheelHolder;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketPearlHolderCondenseInventory extends PacketBase {

	@Save
	public int holderSlot;

	public PacketPearlHolderCondenseInventory(int holderSlot) {
		this.holderSlot = holderSlot;
	}

	public PacketPearlHolderCondenseInventory() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {

		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();
		ItemStack holder = player.inventory.getStackInSlot(holderSlot);
		if (holder.getItem() instanceof IPearlWheelHolder) {
			for (ItemStack stack : player.inventory.mainInventory)
				if (stack.getItem() == ModItems.PEARL_NACRE)
					if (ItemNBTHelper.getBoolean(stack, "infused", false)) {
						ItemStack copy = stack.copy();
						copy.setCount(1);
						if (((IPearlWheelHolder) holder.getItem()).addPearl(holder, copy)) {
							stack.shrink(1);
						}
					}
		}
	}
}
