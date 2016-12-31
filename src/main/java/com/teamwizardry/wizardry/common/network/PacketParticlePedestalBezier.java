package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.wizardry.lib.LibParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketParticlePedestalBezier extends PacketBase {

    private BlockPos pedestal;
    private BlockPos center;

    public PacketParticlePedestalBezier() {
    }

    public PacketParticlePedestalBezier(BlockPos pedestal, BlockPos center) {
        this.pedestal = pedestal;
        this.center = center;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        center = BlockPos.fromLong(buf.readLong());
        pedestal = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(center.toLong());
        buf.writeLong(pedestal.toLong());
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;

        World world = Minecraft.getMinecraft().player.world;

        LibParticles.COLORFUL_BATTERY_BEZIER(world, pedestal, center);
    }
}
