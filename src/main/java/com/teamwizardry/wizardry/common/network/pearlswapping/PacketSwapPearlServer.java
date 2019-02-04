package com.teamwizardry.wizardry.common.network.pearlswapping;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlWheelHolder;
import com.teamwizardry.wizardry.api.util.PearlHandlingUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSwapPearlServer extends PacketBase {
    @Save public int itemSlot;
    @Save public int wheelHolderItemSlot;
    @Save public  int wheelSlot;

    public PacketSwapPearlServer(){}

    public PacketSwapPearlServer(int swappableSlot, int wheelHolderItemSlot, int wheelSlot){
        this.itemSlot = swappableSlot;
        this.wheelHolderItemSlot = wheelHolderItemSlot;
        this.wheelSlot = wheelSlot;
    }

    //if holding an IPearlSwappable, add pearl from currently selected IPearlWheelHolder slot to it, and add the pearl
    //it held before to the belt. If the belt slot selected is null, add null to staff, if staff is null, add null to belt
    @Override
    public void handle(@NotNull MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        if(itemSlot < 0){
            PearlHandlingUtils.swapPearl(player, null,
                   player.inventory.getStackInSlot(wheelHolderItemSlot), wheelSlot);
        } else
            PearlHandlingUtils.swapPearl(player, player.inventory.getStackInSlot(itemSlot),
                BaublesApi.getBaublesHandler(player).getStackInSlot(wheelHolderItemSlot), wheelSlot);
    }
}
