package me.lordsaad.wizardry.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import me.lordsaad.wizardry.network.packets.PacketUpdateSavedPage;

public enum PacketHandler {
	INSTANCE;
	
	public SimpleNetworkWrapper network;
	private int id = 0;
	
	private PacketHandler() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Wizardry");
		
		register(PacketUpdateSavedPage.class, Side.SERVER);
	}
	
	@SuppressWarnings("unchecked")
	private void register(Class clazz, Side targetSide) {
		network.registerMessage(PacketBase.Handler.class, clazz, id++, targetSide);
	}
	
	public static SimpleNetworkWrapper net() {
		return INSTANCE.network;
	}
}
