package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.network.MessageUpdateCapabilities.CapsMessageHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Saad on 8/17/2016.
 */
public class WizardryPacketHandler {

	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Wizardry.MODID);

	public static void registerMessages() {
		INSTANCE.registerMessage(CapsMessageHandler.class, MessageUpdateCapabilities.class, 0, Side.CLIENT);
	}
}
