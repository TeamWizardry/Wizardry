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
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketStaffPearlSwap extends PacketBase {

	@Save
	public int staffSlot;

	public PacketStaffPearlSwap(int staffSlot) {
		this.staffSlot = staffSlot;
	}

	public PacketStaffPearlSwap() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
		if (beltStack.isEmpty()) return;
		ItemStack staff = player.inventory.getStackInSlot(staffSlot);
		if (staff.isEmpty()) return;

		IPearlBelt belt = (IPearlBelt) beltStack.getItem();

		int scrollSlot = ItemNBTHelper.getInt(staff, "scroll_slot", -1);
		if (scrollSlot != -1) {

			int count = belt.getPearlCount(beltStack);
			if (count <= 0) return;

			ItemStack output = belt.removePearl(beltStack, scrollSlot);
			if (output.isEmpty()) return;

			ItemStack infusedPearl = new ItemStack(ModItems.PEARL_NACRE);
			if (staff.hasTagCompound()) infusedPearl.setTagCompound(staff.getTagCompound());

			belt.addPearl(beltStack, infusedPearl);

			if (output.hasTagCompound())
				staff.setTagCompound(output.getTagCompound());
		}
	}
}
