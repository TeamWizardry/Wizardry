package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.crafting.CraftingPlateRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketUpdateCraftingPlateSlot extends PacketBase {

	@Save
	private BlockPos pos;
	@Save
	private ItemStack stack;

	public PacketUpdateCraftingPlateSlot() {
	}

	public PacketUpdateCraftingPlateSlot(BlockPos pos, ItemStack stack) {

		this.pos = pos;
		this.stack = stack;
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

			if (!plate.isInventoryEmpty() && CraftingPlateRecipeManager.doesRecipeExistForItem(stack)) {

				ItemHandlerHelper.insertItem(plate.input.getHandler(), stack, false);

			} else if (!(CraftingPlateRecipeManager.doesRecipeExistForItem(stack))) {

				for (int i = 0; i < plate.realInventory.getHandler().getSlots(); i++) {
					if (plate.realInventory.getHandler().getStackInSlot(i).isEmpty()) {
						plate.realInventory.getHandler().insertItem(i, stack, false);

						if (plate.renderHandler != null) {
							((TileCraftingPlateRenderer) plate.renderHandler).update(i, stack);
						}
						break;
					}
				}
			}
		}
	}
}
