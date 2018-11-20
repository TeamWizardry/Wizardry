package com.teamwizardry.wizardry.common.core.version;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ManifestHandler {

	public static ManifestHandler INSTANCE = new ManifestHandler();
	private HashMap<String, HashMap<String, String>> internalManifestMap = new HashMap<>();
	private HashMap<String, HashMap<String, String>> externalManifestMap = new HashMap<>();
	private boolean generatedNewManifest = false;

	private ManifestHandler() {
	}

	public void processComparisons(File directory, String... categories) {
		boolean change = false;

		if (generatedNewManifest) {
			for (String category : categories) {
				HashMap<String, String> subExternalManifestMap = externalManifestMap.get(category);
				for (Map.Entry<String, String> entry : subExternalManifestMap.entrySet()) {
					deleteFile(directory, category, entry.getKey());
					generateFile(directory, category, entry.getKey());
				}
			}
			return;
		}

		for (String category : categories) {
			HashMap<String, String> subInternalManifestMap = internalManifestMap.get(category);
			HashMap<String, String> subExternalManifestMap = externalManifestMap.get(category);
			for (Map.Entry<String, String> entry : subInternalManifestMap.entrySet()) {
				if (!subExternalManifestMap.containsKey(entry.getKey())) {
					generateFile(directory, category, entry.getKey());
					change = true;
					continue;
				}

				if (!entry.getValue().equals(subExternalManifestMap.get(entry.getKey()))) {
					deleteFile(directory, category, entry.getKey());
					generateFile(directory, category, entry.getKey());
					change = true;
				}
			}

			for (Map.Entry<String, String> entry : subExternalManifestMap.entrySet()) {
				if (!subInternalManifestMap.containsKey(entry.getKey())) {
					deleteFile(directory, category, entry.getKey());
					change = true;
				}
			}
		}

		if (change) {
			File externalManifest = new File(directory, "manifest.json");
			if (!externalManifest.exists()) {

				try {
					if (!externalManifest.createNewFile()) {
						Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create manifest file! Customizations to recipes and modules will be reset every time you load the game!");
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writeJsonToFile(generateInternalManifestJson(), externalManifest);
			externalManifestMap.putAll(internalManifestMap);
			Wizardry.logger.info("    > Successfully generated new manifest file");
		}
	}

	public void loadNewInternalManifest(String... categories) {
		Map<String, ModContainer> modList = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> entry : modList.entrySet() ) {
			for (String category : categories) {
	
				try {
					for (String fileName : ManaRecipes.getResourceListing(entry.getKey(), category)) {
						if (fileName.isEmpty()) continue;
	
						InputStream stream = LibrarianLib.PROXY.getResource(entry.getKey(), category + "/" + fileName);
						if (stream == null) {
							Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not read " + fileName + " in " + category + " from mod jar! Report this to the devs on Github!");
							continue;
						}
						try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()))) {
							StringBuilder sb = new StringBuilder();
							String line;
							while ((line = br.readLine()) != null) {
								sb.append(line);
								sb.append('\n');
							}
							addItemToManifest(category, Files.getNameWithoutExtension(fileName), sb.toString().hashCode() + "");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void generateFile(File directory, String category, String key) {
		InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, category + "/" + key + ".json");
		if (stream == null) {
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not read under " + category + " in " + key + " from mod jar! Report this to the devs on Github!");
			return;
		}

		try {
			FileUtils.copyInputStreamToFile(stream, new File(directory + "/" + category + "/", key + ".json"));
			Wizardry.logger.info("    > " + category + " in " + key + " copied successfully from mod jar.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteFile(File directory, String category, String key) {
		File file = new File(directory + "/" + category + "/", key + ".json");
		if (!file.exists()) return;

		if (!file.delete()) {
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not delete " + category + " file " + key + " from folder!");
		} else {
			Wizardry.logger.info("    > " + category + " in " + key + " successfully deleted.");
		}
	}

	/**
	 * WARNING
	 * Don't run unless internal manifest map is done adding items
	 */
	public void loadExternalManifest(@Nonnull File directory) {
		try {
			File externalManifest = new File(directory, "manifest.json");
			if (!externalManifest.exists()) {

				if (!externalManifest.createNewFile()) {
					Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create manifest file! Customizations to recipes and modules will be reset every time you load the game!");
					return;
				}

				writeJsonToFile(generateInternalManifestJson(), externalManifest);
				externalManifestMap.putAll(internalManifestMap);
				generatedNewManifest = true;
				Wizardry.logger.info("    > Successfully generated new manifest file");
				return;
			}

			if (!externalManifest.canRead()) {
				Wizardry.logger.error("    > SOMETHING WENT WRONG! Can't read manifest file! Customizations to recipes and modules will be reset every time you load the game!");
				return;
			}

			Wizardry.logger.info("    > Found manifest file. Reading...");
			JsonElement element = new JsonParser().parse(new FileReader(externalManifest));

			if (element != null && element.isJsonObject()) {

				for (Map.Entry<String, JsonElement> categorySet : element.getAsJsonObject().entrySet()) {
					String category = categorySet.getKey();
					JsonElement categoryElement = categorySet.getValue();

					externalManifestMap.putIfAbsent(category, new HashMap<>());
					Wizardry.logger.info("    >  |");
					Wizardry.logger.info("    >  |_ Category found: " + category);

					if (categoryElement.isJsonArray()) {
						for (JsonElement element1 : categoryElement.getAsJsonArray()) {
							if (!element1.isJsonObject()) continue;

							JsonObject externalObject = element1.getAsJsonObject();

							if (!externalObject.has("id") || !externalObject.has("hash")) continue;

							String id = externalObject.getAsJsonPrimitive("id").getAsString();
							String hash = externalObject.getAsJsonPrimitive("hash").getAsString();

							externalManifestMap.get(category).put(id, hash);
							Wizardry.logger.info("    >  | |_ " + id + ": " + hash);
						}
					}
				}
			}
			Wizardry.logger.info("    >  |____________________________________/");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addItemToManifest(String category, String id, File file) {
		internalManifestMap.putIfAbsent(category, new HashMap<>());

		try {
			String mintContents = Files.toString(file, Charset.defaultCharset());

			internalManifestMap.get(category).put(id, mintContents.hashCode() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addItemToManifest(String category, String id, String hash) {
		internalManifestMap.putIfAbsent(category, new HashMap<>());
		internalManifestMap.get(category).put(id, hash);
	}

	public JsonObject generateInternalManifestJson() {
		JsonObject jsonManifest = new JsonObject();

		for (Map.Entry<String, HashMap<String, String>> categoryEntry : internalManifestMap.entrySet()) {
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

	public void writeJsonToFile(JsonObject object, File file) {
		try (JsonWriter writer = new JsonWriter(Files.newWriter(file, Charset.defaultCharset()))) {
			Streams.write(object, writer);
		} catch (IOException e) {
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create or write to file! Customizations to recipes and modules will be reset every time you load the game!");
			e.printStackTrace();
		}
	}
}
