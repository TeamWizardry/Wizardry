package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.common.network.PacketSyncWizardryWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class SpellTicker {

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player.world.isRemote) return;

		WizardryWorld cap = WizardryWorldCapability.get(event.player.world);
		if (cap == null) return;

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(cap.serializeNBT()), event.player.world.provider.getDimension());
	}

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;
		if (event.phase != TickEvent.Phase.END) return;

		World world = event.world;
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap == null) return;

		//PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(worldCap.serializeNBT()), world.provider.getDimension())
		worldCap.getSpellObjectManager().tick(onChange -> {
		});
	}

}
