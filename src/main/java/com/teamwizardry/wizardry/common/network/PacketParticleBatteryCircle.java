package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.wizardry.lib.LibParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class PacketParticleBatteryCircle extends PacketBase {

    private BlockPos center;

    public PacketParticleBatteryCircle() {
    }

    public PacketParticleBatteryCircle(BlockPos center) {
        this.center = center;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        center = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(center.toLong());
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;

        World world = Minecraft.getMinecraft().player.world;

        double angle = Math.toRadians(ThreadLocalRandom.current().nextDouble(360)) * Math.PI * 2;
        double x = Math.cos(angle) * 6;
        double z = Math.sin(angle) * 6;
        LibParticles.CLUSTER_DRAPE(world, new Vec3d(center.getX() + x + 0.5, center.getY() + ThreadLocalRandom.current().nextDouble(20), center.getZ() + z + 0.5));
    }
}
