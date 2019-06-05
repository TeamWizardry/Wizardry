package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@PacketRegister(Side.CLIENT)
public class PacketBreakBlock extends PacketBase {

	@Save
	public BlockPos pos;

	public PacketBreakBlock() {
	}

	public PacketBreakBlock(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handle(@Nonnull MessageContext ctx) {
		Minecraft.getMinecraft().player.world.destroyBlock(pos, true);
	}
}
