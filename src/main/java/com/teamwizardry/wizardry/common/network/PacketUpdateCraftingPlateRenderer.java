package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.client.render.block.TileCraftingPlateRenderer;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
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
	public void handle(@NotNull MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				TileCraftingPlate plate = (TileCraftingPlate) world.getTileEntity(pos);
				if (plate == null) return;
				if (plate.renderHandler != null)
					((TileCraftingPlateRenderer) plate.renderHandler).update(slot);

			}
		});
	}
}
