package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSwapPearl extends PacketBase {

	@Save
	public int index;

	public PacketSwapPearl() {
	}

	public PacketSwapPearl(int index) {
		this.index = index;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;

		ItemStack swappable = player.getHeldItemMainhand();

		ItemStack storageStack = BaublesSupport.getItem(player, IPearlStorageHolder.class);
		IPearlStorageHolder holder = (IPearlStorageHolder) storageStack.getItem();
		int originalPearlCount = holder.getPearlCount(storageStack);

		if (!storageStack.isEmpty() && swappable.getItem() instanceof IPearlSwappable) {
			ItemStack pearl = ((IPearlSwappable) swappable.getItem()).swapPearl(swappable, holder.removePearl(storageStack, index, false));
			holder.addPearl(storageStack, pearl, false);
		}

		PacketHandler.NETWORK.sendTo(new PacketUpdatePearlGUI(originalPearlCount, holder.getPearlCount(storageStack), index, swappable.getTagCompound(), storageStack.getTagCompound()), player);
	}
}
