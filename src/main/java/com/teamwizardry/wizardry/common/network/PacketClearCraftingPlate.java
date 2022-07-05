package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketClearCraftingPlate extends PacketBase {

	@Save
	private BlockPos pos;

	public PacketClearCraftingPlate() {
	}

	public PacketClearCraftingPlate(BlockPos pos) {

		this.pos = pos;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;
		if (!world.isBlockLoaded(pos)) return;

		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileCraftingPlate) {
			TileCraftingPlate plate = (TileCraftingPlate) entity;

			// DEBUG
//			if (plate.renderHandler != null) {
//				System.out.println("Map has size " + ((TileCraftingPlateRenderer) plate.renderHandler).hoveringStacks.size());
//			}

			for (int i = 0; i < plate.realInventory.getHandler().getSlots(); i++) {
				plate.realInventory.getHandler().setStackInSlot(i, ItemStack.EMPTY);

				if (plate.renderHandler != null) {
					((TileCraftingPlateRenderer) plate.renderHandler).update(i, ItemStack.EMPTY);
				}
			}
		}
	}
}
