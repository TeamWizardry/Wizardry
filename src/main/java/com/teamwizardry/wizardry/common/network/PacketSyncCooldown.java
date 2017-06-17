package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.client.core.CooldownHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

public class PacketSyncCooldown extends PacketBase {

	@Save
	private boolean resetMain;
	@Save
	private boolean resetOff;

	public PacketSyncCooldown() {
	}

	public PacketSyncCooldown(boolean resetMain, boolean resetOff) {
		this.resetMain = resetMain;
		this.resetOff = resetOff;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		CooldownHandler.INSTANCE.setResetMain(resetMain);
		CooldownHandler.INSTANCE.setResetOff(resetOff);
	}
}
