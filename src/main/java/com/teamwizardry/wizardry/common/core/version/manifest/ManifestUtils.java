package com.teamwizardry.wizardry.common.core.version.manifest;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.teamwizardry.wizardry.Wizardry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ManifestUtils {
	private ManifestUtils() {}
	
	public static JsonObject generateManifestJson(HashMap<String, HashMap<String, String>> manifestMap) {
		JsonObject jsonManifest = new JsonObject();

		for (Map.Entry<String, HashMap<String, String>> categoryEntry : manifestMap.entrySet()) {
			String category = categoryEntry.getKey();

			JsonArray categoryArray = new JsonArray();
			for (Map.Entry<String, String> entry : categoryEntry.getValue().entrySet()) {
				JsonObject entryObject = new JsonObject();
				entryObject.addProperty("id", entry.getKey());
				entryObject.addProperty("hash", entry.getValue());

				categoryArray.add(entryObject);
			}

			jsonManifest.add(category, categoryArray);
		}

		return jsonManifest;
	}
	
	public static void loadManifestFile(File externalManifest, HashMap<String, HashMap<String, String>> manifestMap, boolean verbose) throws IOException {
		JsonElement element = new JsonParser().parse(new FileReader(externalManifest));

		if (element != null && element.isJsonObject()) {

			for (Map.Entry<String, JsonElement> categorySet : element.getAsJsonObject().entrySet()) {
				String category = categorySet.getKey();
				JsonElement categoryElement = categorySet.getValue();

				manifestMap.putIfAbsent(category, new HashMap<>());
				if( verbose ) {
					Wizardry.LOGGER.info("    >  |");
					Wizardry.LOGGER.info("    >  |_ Category found: " + category);
				}

				if (categoryElement.isJsonArray()) {
					for (JsonElement element1 : categoryElement.getAsJsonArray()) {
						if (!element1.isJsonObject()) continue;

						JsonObject externalObject = element1.getAsJsonObject();

						if (!externalObject.has("id") || !externalObject.has("hash")) continue;

						String id = externalObject.getAsJsonPrimitive("id").getAsString();
						String hash = externalObject.getAsJsonPrimitive("hash").getAsString();

						manifestMap.get(category).put(id, hash);
						if( verbose ) {
							Wizardry.LOGGER.info("    >  | |_ " + id + ": " + hash);
						}
					}
				}
			}
		}
	}
	
	public static void writeJsonToFile(JsonObject object, File file) {
		try (JsonWriter writer = new JsonWriter(Files.newWriter(file, Charset.defaultCharset()))) {
			Streams.write(object, writer);
		} catch (IOException e) {
			Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not create or write to file! Customizations to recipes and modules will be reset every time you load the game!");
			e.printStackTrace();
		}
	}
}
