package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
	@SideOnly(Side.CLIENT)
	public void handle(@NotNull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (world == null) return;
		if (!world.isBlockLoaded(pos)) return;

		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileCraftingPlate) {
			TileCraftingPlate plate = (TileCraftingPlate) entity;

			if (plate.hasInput()) {
				plate.input.getHandler().extractItem(slot, player.isSneaking() ? 64 : 1, false);

			} else if (plate.hasOutput()) {
				plate.output.getHandler().extractItem(slot, player.isSneaking() ? 64 : 1, false);

			} else {
				plate.realInventory.getHandler().extractItem(slot, player.isSneaking() ? 64 : 1, false);
				if (plate.renderHandler != null) {
					((TileCraftingPlateRenderer) plate.renderHandler).update(slot, ItemStack.EMPTY);
				}
			}
		}
	}
}
