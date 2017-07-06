package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketSyncCape extends PacketBase {

	@Save
	private ItemStack stack;

	public PacketSyncCape() {
	}

	public PacketSyncCape(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		if (!ItemNBTHelper.verifyExistence(stack, "uuid")) {
			UUID uuid = UUID.randomUUID();
			ItemNBTHelper.setUUID(stack, "uuid", uuid);
		}
	}
}
