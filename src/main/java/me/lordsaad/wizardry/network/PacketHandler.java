package me.lordsaad.wizardry.network;

import me.lordsaad.wizardry.network.packets.PacketUpdateSavedPage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum PacketHandler {
    INSTANCE;

    public SimpleNetworkWrapper network;
    private int id = 0;

    PacketHandler() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("Wizardry");

        register(PacketUpdateSavedPage.class, Side.SERVER);
    }

    public static SimpleNetworkWrapper net() {
        return INSTANCE.network;
    }

    @SuppressWarnings("unchecked")
    private void register(Class clazz, Side targetSide) {
        network.registerMessage(PacketBase.Handler.class, clazz, id++, targetSide);
    }
}
