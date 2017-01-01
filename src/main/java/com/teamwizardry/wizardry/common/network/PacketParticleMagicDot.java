package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketParticleMagicDot extends PacketBase {

    @Save
    private Vec3d pos;
    @Save
    private float scale;

    public PacketParticleMagicDot() {
    }

    public PacketParticleMagicDot(Vec3d pos, float scale) {
        this.pos = pos;
        this.scale = scale;
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;

        World world = Minecraft.getMinecraft().player.world;

        LibParticles.MAGIC_DOT(world, pos, scale);
    }
}
