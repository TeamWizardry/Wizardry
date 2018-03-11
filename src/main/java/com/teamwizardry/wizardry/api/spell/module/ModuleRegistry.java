package com.teamwizardry.wizardry.api.spell.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Demoniaque.
 */
public class ModuleRegistry {

	public final static ModuleRegistry INSTANCE = new ModuleRegistry();

	public ArrayList<Module> modules = new ArrayList<>();

	private File directory;

	private Deque<Module> left = new ArrayDeque<>();

	private ModuleRegistry() {
	}

	public Module getModule(String id) {
		for (Module module : modules) if (module.getID().equals(id)) return module;
		return null;
	}

	@Nullable
	public Module getModule(ItemStack itemStack) {
		for (Module module : modules)
			if (ItemStack.areItemsEqual(itemStack, module.getItemStack())) {
				return module;
			}
		return null;
	}

	@Nonnull
	public ArrayList<Module> getModules(ModuleType type) {
		ArrayList<Module> modules = new ArrayList<>();
		for (Module module : this.modules) if (module.getModuleType() == type) modules.add(module);

		modules.sort(Comparator.comparing(Module::getReadableName));
		return modules;
	}

	public void loadUnprocessedModules() {
		modules.clear();
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), Module.class, RegisterModule.class, (clazz, info) -> {
			try {
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance();
				if (object instanceof Module) modules.add((Module) object);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public void processModules() {
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting module registration processing.");

		HashSet<Module> processed = new HashSet<>();

		for (Module rawModule : modules) {
			File file = new File(directory, rawModule.getID() + ".json");

			if (!file.exists()) {
				Wizardry.logger.error("  > SOMETHING WENT WRONG! " + rawModule.getID() + ".json does NOT exist. Ignoring module...");
				continue;
			}

			JsonElement element;
			try {
				element = new JsonParser().parse(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}

			if (element == null) {
				Wizardry.logger.error("  > SOMETHING WENT WRONG! Could not parse " + file.getName() + ".json. Ignoring module...");
				continue;
			}

			if (!element.isJsonObject()) {
				Wizardry.logger.error("    > WARNING! " + file.getName() + ".json does NOT contain a JsonObject. Ignoring module...: " + element.toString());
				continue;
			}
			JsonObject moduleObject = element.getAsJsonObject();

			if (!moduleObject.has("item")) {
				Wizardry.logger.error("    > WARNING! An element in " + file.getName() + ".json does NOT have an 'item' key! Unknown item to use for element, Ignoring module...: " + element.toString());
				continue;
			}

			Color primaryColor = Color.WHITE;
			Color secondaryColor = Color.WHITE;
			int cooldownTime = 0;
			int chargeupTime = 0;
			double manaDrain = 0;
			double burnoutFill = 0;
			ItemStack itemStack = ItemStack.EMPTY;
			float powerMultiplier = 1;
			float manaMultiplier = 1;
			float burnoutMultiplier = 1;
			int itemMeta = 0;

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
			if (item == null) {
				Wizardry.logger.error("    > WARNING! Item for module " + rawModule.getID() + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "' from " + file.getName());
				continue;
			}

			for (Map.Entry<String, JsonElement> entry : moduleObject.entrySet()) {
				switch (entry.getKey()) {
					case "item_meta": {
						itemMeta = entry.getValue().getAsJsonPrimitive().getAsInt();
						break;
					}
					case "mana_drain": {
						manaDrain = entry.getValue().getAsJsonPrimitive().getAsDouble();
						break;
					}
					case "burnout_fill": {
						burnoutFill = entry.getValue().getAsJsonPrimitive().getAsDouble();
						break;
					}
					case "cooldown_time": {
						cooldownTime = entry.getValue().getAsJsonPrimitive().getAsInt();
						break;
					}
					case "chargeup_time": {
						chargeupTime = entry.getValue().getAsJsonPrimitive().getAsInt();
						break;
					}
					case "primary_color": {
						primaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						break;
					}
					case "secondary_color": {
						primaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						break;
					}
					case "power_multiplier": {
						powerMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						break;
					}
					case "mana_multiplier": {
						manaMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						break;
					}
					case "burnout_multiplier": {
						burnoutMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						break;
					}
				}
			}

			rawModule.update(new ItemStack(item, 1, itemMeta), manaDrain, burnoutFill, primaryColor, secondaryColor, powerMultiplier, manaMultiplier, burnoutMultiplier, cooldownTime, chargeupTime);

			if (moduleObject.has("modifiers") && moduleObject.get("modifiers").isJsonArray()) {
				JsonArray attributeModifiers = moduleObject.getAsJsonArray("modifiers");
				for (JsonElement attributeModifier : attributeModifiers) {
					if (attributeModifier.isJsonObject()) {
						String attribute = null;
						Operation operator = null;
						double amount = 0;
						JsonObject modifier = attributeModifier.getAsJsonObject();
						if (modifier.has("attribute") && modifier.get("attribute").isJsonPrimitive() && modifier.getAsJsonPrimitive("attribute").isString())
							attribute = Attributes.getAttributeFromName(modifier.getAsJsonPrimitive("attribute").getAsString());
						if (modifier.has("operation") && modifier.get("operation").isJsonPrimitive() && modifier.getAsJsonPrimitive("operation").isString())
							operator = Operation.valueOf(modifier.get("operation").getAsJsonPrimitive().getAsString().toUpperCase());
						if (modifier.has("amount") && modifier.get("amount").isJsonPrimitive() && modifier.getAsJsonPrimitive("amount").isNumber())
							amount = modifier.get("amount").getAsDouble();
						Wizardry.logger.info("    > Loading AttributeModifier for " + file.getName() + ": " + operator + " -> " + attribute + ", " + amount);
						if (attribute != null && operator != null) {
							module.modifiers.add(new AttributeModifier(attribute, amount, operator));
							Wizardry.logger.info("        > Successfully loaded AttributeModifier");
						}
					}
				}
			}

			processed.add(module);
			Wizardry.logger.info("  > module " + module.getID() + " registered successfully.");
		}

		primary:
		for (Module module1 : modules) {
			for (Module module2 : processed)
				if (module1.getID().equals(module2.getID())) continue primary;

			left.add(module1);
		}

		if (!left.isEmpty()) {
			Wizardry.logger.error("  > Missing or ignored modules detected in modules directory:");
			for (Module module : left) Wizardry.logger.error("    - " + module.getID());
		}

		modules.clear();
		modules.addAll(processed);

		Wizardry.logger.info("> Module registration processing complete! (ᵔᴥᵔ)");
		Wizardry.logger.info("<<========================================================================>>");
	}

	public void copyMissingModulesFromResources(File directory) {
		for (Module module : modules) {
			File file = new File(directory + "/modules/", module.getID() + ".json");
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "modules/" + module.getID() + ".json");
			if (stream == null) {
				Wizardry.logger.fatal("    > SOMETHING WENT WRONG! Could not read module " + module.getID() + " from mod jar! Report this to the devs on Github!");
				continue;
			}

			try {
				FileUtils.copyInputStreamToFile(stream, file);
				Wizardry.logger.info("    > Module " + module.getID() + " copied successfully from mod jar.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
