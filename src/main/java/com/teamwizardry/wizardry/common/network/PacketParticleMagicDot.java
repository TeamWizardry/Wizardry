package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.wizardry.lib.LibParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketParticleMagicDot extends PacketBase {

    private Vec3d pos;
    private float scale;

    public PacketParticleMagicDot() {
    }

    public PacketParticleMagicDot(Vec3d pos, float scale) {
        this.pos = pos;
        this.scale = scale;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        scale = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(pos.xCoord);
        buf.writeDouble(pos.yCoord);
        buf.writeDouble(pos.zCoord);
        buf.writeFloat(scale);
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;

        World world = Minecraft.getMinecraft().player.world;

        LibParticles.MAGIC_DOT(world, pos, scale);
    }
}
