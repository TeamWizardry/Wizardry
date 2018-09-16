package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketAddPearlsToBelt extends PacketBase {

	@Save
	public int slotHolder;

	public PacketAddPearlsToBelt(int slotHolder) {
		this.slotHolder = slotHolder;
	}

	public PacketAddPearlsToBelt() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();

		ItemStack holder = player.inventory.getStackInSlot(slotHolder);
		if (holder.getItem() != ModItems.PEARL_BELT) return;
		IPearlBelt belt = (IPearlBelt) holder.getItem();

		for (ItemStack stack : player.inventory.mainInventory)
			if (stack.getItem() == ModItems.PEARL_NACRE)
				if (ItemNBTHelper.getBoolean(stack, "infused", false))
					if (belt.addPearl(holder, stack.copy())) {
						stack.shrink(1);
					}
	}
}
