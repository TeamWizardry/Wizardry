package com.teamwizardry.wizardry.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by LordSaad.
 */
public class PacketSyncModuleRegistry extends PacketBase {

	@Save
	private String json = null;

	public PacketSyncModuleRegistry() {
	}

	public PacketSyncModuleRegistry(File file) {
		JsonParser parser = new JsonParser();
		try {
			JsonElement element = parser.parse(new FileReader(file));
			JsonObject obj = element.getAsJsonObject();

			Gson gson = new Gson();
			json = gson.toJson(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (json == null) return;

		Gson gson = new Gson();
		ModuleRegistry.INSTANCE.processModules(gson.fromJson(json, JsonObject.class));
	}
}
