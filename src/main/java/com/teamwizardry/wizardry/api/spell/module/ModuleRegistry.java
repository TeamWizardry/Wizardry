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
		Wizardry.logger.info(" _______________________________________________________________________\\\\");
		Wizardry.logger.info(" | Starting module registration");

		HashSet<Module> processed = new HashSet<>();

		for (Module module : modules) {
			Wizardry.logger.info(" | |");
			Wizardry.logger.info(" | | Registering module " + module.getID());

			File file = new File(directory, module.getID() + ".json");

			if (!file.exists()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! " + file.getName() + " does NOT exist.");
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
				continue;
			}

			if (!file.canRead()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Something is preventing me from reading " + file.getName());
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
			}

			JsonElement element;
			try {
				element = new JsonParser().parse(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}

			if (element == null) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Could not parse " + file.getName() + ". Invalid json.");
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
				continue;
			}

			if (!element.isJsonObject()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! " + file.getName() + "'s json is NOT a Json Object.");
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
				continue;
			}
			JsonObject moduleObject = element.getAsJsonObject();

			if (!moduleObject.has("item")) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! No 'item' key found in " + file.getName() + ". Unknown item to use for element.");
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
				continue;
			}

			Color primaryColor = Color.WHITE;
			Color secondaryColor = Color.WHITE;
			int cooldownTime = 0;
			int chargeupTime = 0;
			double manaDrain = 0;
			double burnoutFill = 0;
			float powerMultiplier = 1;
			float manaMultiplier = 1;
			float burnoutMultiplier = 0;
			int itemMeta = 0;

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
			if (item == null) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Item for module " + module.getID() + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "'");
				Wizardry.logger.error("| |___ Failed to register module " + module.getID());
				continue;
			} else {
				Wizardry.logger.error("| | |_ Found Item " + item.getUnlocalizedName());
			}

			for (Map.Entry<String, JsonElement> entry : moduleObject.entrySet()) {
				switch (entry.getKey()) {
					case "item_meta": {
						itemMeta = entry.getValue().getAsJsonPrimitive().getAsInt();
						Wizardry.logger.info(" | | |_ Found Item Meta:          " + itemMeta);
						break;
					}
					case "mana_drain": {
						manaDrain = entry.getValue().getAsJsonPrimitive().getAsDouble();
						Wizardry.logger.info(" | | |_ Found Mana Drain:         " + manaDrain);
						break;
					}
					case "burnout_fill": {
						burnoutFill = entry.getValue().getAsJsonPrimitive().getAsDouble();
						Wizardry.logger.info(" | | |_ Found Burnout Fill:       " + burnoutFill);
						break;
					}
					case "cooldown_time": {
						cooldownTime = entry.getValue().getAsJsonPrimitive().getAsInt();
						Wizardry.logger.info(" | | |_ Found Cooldown Time:      " + cooldownTime);
						break;
					}
					case "chargeup_time": {
						chargeupTime = entry.getValue().getAsJsonPrimitive().getAsInt();
						Wizardry.logger.info(" | | |_ Found Chargeup Time:      " + chargeupTime);
						break;
					}
					case "primary_color": {
						primaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						Wizardry.logger.info(" | | |_ Found Primary Color:      " + primaryColor.getRGB());
						break;
					}
					case "secondary_color": {
						secondaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						Wizardry.logger.info(" | | |_ Found Secondary Color:    " + secondaryColor.getRGB());
						break;
					}
					case "power_multiplier": {
						powerMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						Wizardry.logger.info(" | | |_ Found Power Multiplier:   " + powerMultiplier);
						break;
					}
					case "mana_multiplier": {
						manaMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						Wizardry.logger.info(" | | |_ Found Mana Multiplier:    " + manaMultiplier);
						break;
					}
					case "burnout_multiplier": {
						burnoutMultiplier = entry.getValue().getAsJsonPrimitive().getAsFloat();
						Wizardry.logger.info(" | | |_ Found Burnout Multiplier: " + burnoutMultiplier);
						break;
					}
				}
			}

			module.init(new ItemStack(item, 1, itemMeta), manaDrain, burnoutFill, primaryColor, secondaryColor, powerMultiplier, manaMultiplier, burnoutMultiplier, cooldownTime, chargeupTime);

			if (moduleObject.has("modifiers") && moduleObject.get("modifiers").isJsonArray()) {
				Wizardry.logger.info(" | | |___ Found Modifiers. About to process them");


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
						Wizardry.logger.info(" | | | |_ Loading AttributeModifier for " + file.getName() + ": " + operator + " -> " + attribute + ", " + amount);
						if (attribute != null && operator != null) {
							module.addAttribute(new AttributeModifier(attribute, amount, operator));
							Wizardry.logger.info(" | | | | |_ AttributeModifier registered successfully");
						} else {
							Wizardry.logger.error("| | | | |_ Failed to register AttributeModifier!");
						}
					}
				}

				Wizardry.logger.info(" | | |___ Modifiers Registered Successfully.");
			}

			processed.add(module);
			Wizardry.logger.info(" | |_ Module " + module.getID() + " registered successfully!");
		}

		primary:
		for (Module module1 : modules) {
			for (Module module2 : processed)
				if (module1.getID().equals(module2.getID())) continue primary;

			left.add(module1);
		}

		if (!left.isEmpty()) {
			Wizardry.logger.error("|");
			Wizardry.logger.error("|_ Missing or ignored modules detected in modules directory:");
			for (Module module : left) Wizardry.logger.error("| |_ " + module.getID());
		}

		modules.clear();
		modules.addAll(processed);

		Wizardry.logger.info(" |");
		Wizardry.logger.info(" | Module registration processing complete! (ᵔᴥᵔ)");
		Wizardry.logger.info(" |_______________________________________________________________________//");
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
