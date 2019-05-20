package com.teamwizardry.wizardry.common.module.effects.bounce;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketAddBouncyBlock extends PacketBase {

	@Save
	public int dimension;
	@Save
	public BlockPos pos;
	@Save
	public int time;

	public PacketAddBouncyBlock() {
	}

	public PacketAddBouncyBlock(World world, BlockPos pos, int time) {

		this.dimension = world.provider.getDimension();
		this.pos = pos;
		this.time = time;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		if (pos == null) return;
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
		BounceManager.INSTANCE.forBlock(world, pos, time);
	}
}
