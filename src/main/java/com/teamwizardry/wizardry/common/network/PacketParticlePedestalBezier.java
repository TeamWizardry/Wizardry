package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

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

        ParticleBuilder helix = new ParticleBuilder(20);
        helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
        helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

        ParticleSpawner.spawn(helix, world, new InterpBezier3D(new Vec3d(pedestal).addVector(0.5, 0.5, 0.5),
                        new Vec3d(center).addVector(0.5, 0.5, 0.5), new Vec3d(0, 1, 0), new Vec3d(0, -1, 0)),
                ThreadLocalRandom.current().nextInt(1, 3),
                0, (aFloat, particleBuilder) -> {
                    helix.setColor(new Color(ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(10, 255)));
                    helix.setScale(ThreadLocalRandom.current().nextFloat());
                });
    }
}
