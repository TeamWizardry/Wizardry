package com.teamwizardry.wizardry.common.network.belt;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@PacketRegister(Side.SERVER)
public class PacketStaffPearlSwap extends PacketBase {

	@Save
	public UUID playerUUID;
	@Save
	public int staffSlot;

	public PacketStaffPearlSwap(UUID playerUUID, int staffSlot) {
		this.playerUUID = playerUUID;
		this.staffSlot = staffSlot;
	}

	public PacketStaffPearlSwap() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
		EntityPlayer player = players.getPlayerByUUID(playerUUID);

		ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
		if (beltStack.isEmpty()) return;
		ItemStack staff = player.inventory.getStackInSlot(staffSlot);
		if (staff.isEmpty()) return;

		IPearlBelt belt = (IPearlBelt) beltStack.getItem();

		int scrollSlot = ItemNBTHelper.getInt(staff, "scroll_slot", -1);
		if (scrollSlot >= 0) {

			int count = belt.getPearlCount(beltStack) - 1;
			if (count == 0) return;

			ItemStack infusedPearl = new ItemStack(ModItems.PEARL_NACRE);
			if (staff.hasTagCompound()) infusedPearl.setTagCompound(staff.getTagCompound());

			ItemStack output = belt.removePearl(beltStack, scrollSlot);
			if (output.isEmpty()) return;

			belt.addPearl(beltStack, infusedPearl);

			staff.setTagCompound(output.getTagCompound());
		}
	}
}
