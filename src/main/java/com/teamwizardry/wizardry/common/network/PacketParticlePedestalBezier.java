package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketParticlePedestalBezier extends PacketBase {

    @Save
    private BlockPos pedestal;
    @Save
    private BlockPos center;

    public PacketParticlePedestalBezier() {
    }

    public PacketParticlePedestalBezier(BlockPos pedestal, BlockPos center) {
        this.pedestal = pedestal;
        this.center = center;
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;
        if (Minecraft.getMinecraft().player == null) return;

        World world = Minecraft.getMinecraft().player.world;

        LibParticles.COLORFUL_BATTERY_BEZIER(world, pedestal, center);
    }
}
