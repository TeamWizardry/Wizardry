package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Demoniaque.
 */
public class PacketSyncModules extends PacketBase {

	@Save
	private ArrayList<Module> modules;

	public PacketSyncModules() {
	}

	public PacketSyncModules(ArrayList<Module> modules) {
		this.modules = modules;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		ModuleRegistry.INSTANCE.modules = modules;
	}
}
