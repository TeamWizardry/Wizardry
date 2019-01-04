package com.teamwizardry.wizardry.common.core.version.manifest;

import com.google.common.io.Files;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
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
	
	public static final String MANIFEST_FILENAME = "wizManifest.json";

	public static ManifestHandler INSTANCE = new ManifestHandler();
	private HashMap<String, HashMap<String, String>> internalManifestMap = new HashMap<>();
	private HashMap<String, HashMap<String, String>> externalManifestMap = new HashMap<>();
	private HashMap<String, HashMap<String, String>> fileToMod = new HashMap<>();
	private boolean generatedNewManifest = false;

	private ManifestHandler() {
	}
	
	public ManifestUpgrader startUpgrade(File directory) {
		ManifestUpgrader upgrader = new ManifestUpgrader(directory);
		upgrader.startUpgradeManifest();
		return upgrader;
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
			File externalManifest = new File(directory, MANIFEST_FILENAME);
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
			ManifestUtils.writeJsonToFile(ManifestUtils.generateManifestJson(internalManifestMap), externalManifest);
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
							addItemToManifest(category, entry.getKey(), Files.getNameWithoutExtension(fileName), sb.toString().hashCode() + "");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void generateFile(File directory, String category, String key) {
		String modId = fileToMod.get(category).get(key);
		if( modId == null ) {
			// NOTE: If some bad state occurred in ManifestHandler.processComparisons()
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Expected file " + key + ".json in " + category + " in config folder! Report this to the devs on Github!");
			return;
		}
		
		InputStream stream = LibrarianLib.PROXY.getResource(modId, category + "/" + key + ".json");
		if (stream == null) {
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not read under " + category + " in " + key + " from mod jar! Report this to the devs on Github!");
			return;
		}

		try {
			FileUtils.copyInputStreamToFile(stream, new File(directory + "/" + category + "/", key + ".json"));
			if (ConfigValues.debugInfo)
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
			File externalManifest = new File(directory, MANIFEST_FILENAME);
			if (!externalManifest.exists()) {

				if (!externalManifest.createNewFile()) {
					Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create manifest file! Customizations to recipes and modules will be reset every time you load the game!");
					return;
				}

				ManifestUtils.writeJsonToFile(ManifestUtils.generateManifestJson(internalManifestMap), externalManifest);
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
			ManifestUtils.loadManifestFile(externalManifest, externalManifestMap, ConfigValues.debugInfo);
			Wizardry.logger.info("    >  |____________________________________/");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addItemToManifest(String category, String modId, String id, File file) {
		internalManifestMap.putIfAbsent(category, new HashMap<>());

		try {
			String mintContents = Files.toString(file, Charset.defaultCharset());

			internalManifestMap.get(category).put(id, mintContents.hashCode() + "");
			setItemModId(category, id, modId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addItemToManifest(String category, String modId, String id, String hash) {
		internalManifestMap.putIfAbsent(category, new HashMap<>());
		internalManifestMap.get(category).put(id, hash);
		setItemModId(category, id, modId);
	}
	
	private void setItemModId(String category, String id, String modId) {
		fileToMod.putIfAbsent(category, new HashMap<>());
		String prevModId = fileToMod.get(category).put(id, modId);
		if( prevModId != null )
			Wizardry.logger.warn("    > File name conflict for " + category + "/" + id + ".json occurring in mods '" + modId + "' and '" + prevModId + "'. Some stuff wont be available." );
	}
}
