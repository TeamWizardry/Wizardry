package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSuccPearlsToStorageHolder extends PacketBase {

	public PacketSuccPearlsToStorageHolder() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;

		ItemStack stack = player.getHeldItemMainhand();

		if (stack.getItem() instanceof IPearlStorageHolder) {
			IPearlStorageHolder holder = (IPearlStorageHolder) stack.getItem();
			int originalPearlCount = holder.getPearlCount(stack);

			holder.succPearls(player);

			PacketHandler.NETWORK.sendTo(new PacketUpdatePearlGUI(originalPearlCount, holder.getPearlCount(stack), null, stack.getTagCompound()), player);
		}
	}
}
