package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.client.gui.pearlswap.GuiPearlSwap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PacketRegister(Side.CLIENT)
public class PacketUpdatePearlGUI extends PacketBase {

	@Save
	public int index = -1;
	@Save
	public int originalPearlCount;
	@Save
	public int newPearlCount;
	@Save
	@Nullable
	public NBTTagCompound swappableNBT;
	@Save
	@Nullable
	public NBTTagCompound storageNBT;

	public PacketUpdatePearlGUI() {
	}

	public PacketUpdatePearlGUI(int originalPearlCount, int newPearlCount, @Nullable NBTTagCompound swappableNBT, @Nullable NBTTagCompound storageNBT) {
		this.originalPearlCount = originalPearlCount;
		this.newPearlCount = newPearlCount;
		this.swappableNBT = swappableNBT;
		this.storageNBT = storageNBT;
	}

	public PacketUpdatePearlGUI(int originalPearlCount, int newPearlCount, int index, @Nullable NBTTagCompound swappableNBT, @Nullable NBTTagCompound storageNBT) {
		this.originalPearlCount = originalPearlCount;
		this.newPearlCount = newPearlCount;
		this.index = index;
		this.swappableNBT = swappableNBT;
		this.storageNBT = storageNBT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handle(@NotNull MessageContext ctx) {
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (swappableNBT != null) {
			ItemStack heldItem = player.getHeldItemMainhand();
			if (heldItem.getItem() instanceof IPearlSwappable) {
				heldItem.setTagCompound(swappableNBT);
			}
		}

		if (storageNBT != null) {
			ItemStack heldItem = player.getHeldItemMainhand();
			if (heldItem.getItem() instanceof IPearlStorageHolder) {
				heldItem.setTagCompound(storageNBT);
			} else {
				ItemStack stack = BaublesSupport.getItem(player, IPearlStorageHolder.class);
				if (stack.getItem() instanceof IPearlStorageHolder) {
					stack.setTagCompound(storageNBT);
				}
			}
		}

		if (currentScreen instanceof GuiPearlSwap) {
			((GuiPearlSwap) currentScreen).update(originalPearlCount, newPearlCount, index);
		}
	}
}
