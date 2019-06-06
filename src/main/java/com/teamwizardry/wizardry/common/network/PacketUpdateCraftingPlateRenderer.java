package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketUpdateCraftingPlateRenderer extends PacketBase {

	@Save
	public BlockPos pos;
	@Save
	public int slot;

	public PacketUpdateCraftingPlateRenderer() {
	}

	public PacketUpdateCraftingPlateRenderer(BlockPos pos, int slot) {

		this.pos = pos;
		this.slot = slot;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handle(@NotNull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;
		if (!world.isBlockLoaded(pos)) return;

		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileCraftingPlate) {
			TileCraftingPlate plate = (TileCraftingPlate) entity;
			if (plate.renderHandler != null) {
				((TileCraftingPlateRenderer) plate.renderHandler).update(slot);
			}
		}
	}
}
