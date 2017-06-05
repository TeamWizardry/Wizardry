package com.teamwizardry.wizardry.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketSyncModuleRegistry extends PacketBase {

	@Save
	private String json = null;

	public PacketSyncModuleRegistry() {
	}

	public PacketSyncModuleRegistry(JsonElement json) {
		Gson gson = new Gson();
		this.json = gson.toJson(json);
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (json == null) return;

		Gson gson = new Gson();
		ModuleRegistry.INSTANCE.setJsonObject(gson.fromJson(json, JsonObject.class));
		ModuleRegistry.INSTANCE.processModules();
	}
}
