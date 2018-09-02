package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketRemoveItemCraftingPlate extends PacketBase {

	@Save
	private BlockPos pos;
	@Save
	private int slot;

	public PacketRemoveItemCraftingPlate() {
	}

	public PacketRemoveItemCraftingPlate(BlockPos pos, int slot) {

		this.pos = pos;
		this.slot = slot;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();
		if (world == null) return;
		if (!world.isBlockLoaded(pos)) return;

		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileCraftingPlate) {
			TileCraftingPlate plate = (TileCraftingPlate) entity;

			plate.realInventory.getHandler().extractItem(slot, player.isSneaking() ? 64 : 1, false);
		}
	}
}
