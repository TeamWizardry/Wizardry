package com.teamwizardry.wizardry.api.spell.module;

import com.google.gson.*;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterOverrideDefaults;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler.OverrideMethod;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Demoniaque.
 */
public class ModuleRegistry {

	public final static ModuleRegistry INSTANCE = new ModuleRegistry();

	public ArrayList<ModuleInstance> modules = new ArrayList<>();
	public HashMap<String, ModuleFactory> IDtoModuleFactory = new HashMap<>();
	public HashMap<String, OverrideDefaultMethod> IDtoOverrideDefaultMethod = new HashMap<>();

	private ModuleRegistry() {
	}

	public ModuleInstance getModule(String id) {
		for (ModuleInstance module : modules) if (module.getNBTKey().equals(id)) return module;
		return null;
	}

	@Nullable
	public ModuleInstance getModule(ItemStack itemStack) {
		for (ModuleInstance module : modules)
			if (ItemStack.areItemsEqual(itemStack, module.getItemStack())) {
				return module;
			}
		return null;
	}

	@Nonnull
	public ArrayList<ModuleInstance> getModules(ModuleType type) {
		ArrayList<ModuleInstance> modules = new ArrayList<>();
		for (ModuleInstance module : this.modules) if (module.getModuleType() == type) modules.add(module);

		modules.sort(Comparator.comparing(ModuleInstance::getReadableName));
		return modules;
	}

	public void loadUnprocessedModules() {
		IDtoModuleFactory.clear();
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), IModule.class, RegisterModule.class, (clazz, info) -> {
			try {
				String id = info.getString("ID");

				if (IModule.class.isAssignableFrom(clazz)) {
					ModuleFactory entry = new ModuleFactory(id, clazz);
					IDtoModuleFactory.put(id, entry);
				}
			} catch (ModuleInitException exc) {
				Wizardry.LOGGER.error("Error occurred while registering a module class '" + clazz + "'.", exc);
			}
			return null;
		});
	}

	public void loadOverrideDefaults() {
		IDtoOverrideDefaultMethod.clear();
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), Object.class, RegisterOverrideDefaults.class, (clazz, info) -> {
			try {
				// Create instance
				Constructor<?> ctor = clazz.getConstructor();
				Object obj = ctor.newInstance();

				// Register all overrides
				registerOverrideDefaults(clazz, obj);
			} catch (ModuleInitException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exc) {
				Wizardry.LOGGER.error("Error occurred while registering an override generics '" + clazz + "'.", exc);
			}

			return null;
		});
	}

	private void registerOverrideDefaults(Class<?> clazz, Object obj) throws ModuleInitException {
		HashMap<String, OverrideMethod> overrides = ModuleOverrideHandler.getOverrideMethodsFromClass(clazz, false);

		for (Entry<String, OverrideMethod> override : overrides.entrySet()) {
			// Throw error if another method with same override name is already existing
			OverrideDefaultMethod methodEntry = IDtoOverrideDefaultMethod.get(override.getKey());
			if (methodEntry != null)
				throw new ModuleInitException("Override '" + override.getKey() + "' is already existing at '" + methodEntry.obj.getClass() + "'. Duplicate entry found in '" + clazz + "'.");

			// Register at ID table
			methodEntry = new OverrideDefaultMethod(override.getKey(), override.getValue(), obj);
			IDtoOverrideDefaultMethod.put(override.getKey(), methodEntry);
		}
	}

	public void loadModules(File directory) {
		Wizardry.LOGGER.info(" _______________________________________________________________________\\\\");
		Wizardry.LOGGER.info(" | Starting module registration");

		modules.clear();

		String[] files = directory.list();
		for (String fName : files) {
			File file = new File(directory, fName);

			if (ConfigValues.debugInfo) {
				Wizardry.LOGGER.info(" | |");
				Wizardry.LOGGER.info(" | |_ Parsing module configuration " + fName);
			}

			if (!file.exists()) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! " + file.getName() + " does NOT exist.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! " + file.getName() + " does NOT exist.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}

			if (!file.canRead()) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Something is preventing me from reading " + file.getName());
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Something is preventing me from reading " + file.getName());
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
			}

			JsonElement element;
			try {
				element = new JsonParser().parse(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}

			if (element == null) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Could not parse " + fName + ". Invalid json.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Could not parse " + fName + ". Invalid json.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}

			if (!element.isJsonObject()) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! " + fName + "'s json is NOT a Json Object.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! " + fName + "'s json is NOT a Json Object.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}
			JsonObject moduleObject = element.getAsJsonObject();

			// Get Class ID
			if (!moduleObject.has("reference_module_id") || !moduleObject.get("reference_module_id").isJsonPrimitive()) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! No valid 'reference_module_id' key found in " + file.getName() + ". Unknown module class to use for element.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! No valid 'reference_module_id' key found in " + file.getName() + ". Unknown module class to use for element.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}

			String moduleClassID = moduleObject.get("reference_module_id").getAsString();
			ModuleFactory moduleClassFactory = IDtoModuleFactory.get(moduleClassID);
			if (moduleClassFactory == null) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Referenced type " + moduleClassID + " is unknown.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Referenced type " + moduleClassID + " is unknown.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}

			// Get Name
			if (!moduleObject.has("sub_module_id") || !moduleObject.get("sub_module_id").isJsonPrimitive()) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! No valid 'sub_module_id' key found in " + file.getName() + ". Unknown name to use for element.");
					Wizardry.LOGGER.error("| |___ Failed to parse " + fName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! No valid 'sub_module_id' key found in " + file.getName() + ". Unknown name to use for element.");
					Wizardry.LOGGER.error("|___ Failed to parse " + fName);
				}
				continue;
			}

			String moduleName = moduleObject.get("sub_module_id").getAsString();

			Wizardry.LOGGER.info(" | | |_ Registering module " + moduleName + " of class " + moduleClassID);

			// Get optional icon
			ResourceLocation icon = null;
			if (moduleObject.has("icon")) {
				if (!moduleObject.get("icon").isJsonPrimitive()) {
					if (ConfigValues.debugInfo) {
						Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Field 'icon' has an invalid type in " + file.getName() + ". It is expected to be a string.");
						Wizardry.LOGGER.error("| |___ Failed to register module " + moduleName);
					} else {
						Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Field 'icon' has an invalid type in " + file.getName() + ". It is expected to be a string.");
						Wizardry.LOGGER.error("|___ Failed to register module " + moduleName);
					}
					continue;
				}

				String iconID = moduleObject.get("icon").getAsString();
				icon = new ResourceLocation(iconID);
			}

			// Retrieve module using optional parameters
			IModule moduleClass;
			try {
				if (moduleObject.has("parameters")) {
					if (!moduleObject.get("parameters").isJsonObject()) {
						if (ConfigValues.debugInfo) {
							Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Field 'parameters' has an invalid type in " + file.getName() + ". It is expected to be an object.");
							Wizardry.LOGGER.error("| |___ Failed to register module " + moduleName);
						} else {
							Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Field 'parameters' has an invalid type in " + file.getName() + ". It is expected to be an object.");
							Wizardry.LOGGER.error("|___ Failed to register module " + moduleName);
						}
						continue;
					}

					JsonObject parameters = moduleObject.getAsJsonObject("parameters");
					HashMap<String, Object> keyToValues = new HashMap<>();
					loadModuleClassParameters(keyToValues, null, parameters);

					// Test for mappability. Remove unmappable parameters with a warning output
					Iterator<Entry<String, Object>> iter = keyToValues.entrySet().iterator();
					while (iter.hasNext()) {
						Entry<String, Object> pair = iter.next();
						if (!moduleClassFactory.hasConfigField(pair.getKey())) {
							if (ConfigValues.debugInfo)
								Wizardry.LOGGER.warn("| | |_ WARNING: Parameter field '" + pair.getKey() + "' is not supported by type '" + moduleClassID + "'. Field is ignored.");
							else
								Wizardry.LOGGER.warn("| |_ WARNING: Parameter field '" + pair.getKey() + "' is not supported by type '" + moduleClassID + "'. Field is ignored.");
							iter.remove();
						}
					}

					// Retrieve or create an instance for each parameter set
					moduleClass = moduleClassFactory.getInstance(keyToValues);
				} else {
					moduleClass = moduleClassFactory.getInstance();
				}
			} catch (ModuleInitException exc) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! " + exc.getMessage());
					Wizardry.LOGGER.error("| |___ Failed to register module " + moduleName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! " + exc.getMessage());
					Wizardry.LOGGER.error("|___ Failed to register module " + moduleName);
				}
				continue;
			}


			if (!moduleObject.has("item")) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! No 'item' key found in " + file.getName() + ". Unknown item to use for element.");
					Wizardry.LOGGER.error("| |___ Failed to register module " + moduleName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! No 'item' key found in " + file.getName() + ". Unknown item to use for element.");
					Wizardry.LOGGER.error("|___ Failed to register module " + moduleName);
				}
				continue;
			}

			Color primaryColor = Color.WHITE;
			Color secondaryColor = Color.WHITE;
			int itemMeta = 0;
			DefaultHashMap<Attribute, AttributeRange> attributeRanges = new DefaultHashMap<>(AttributeRange.BACKUP);

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
			if (item == null || item.getRegistryName() == null) {
				if (ConfigValues.debugInfo) {
					Wizardry.LOGGER.error("| | |_ SOMETHING WENT WRONG! Item for module " + moduleName + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "'");
					Wizardry.LOGGER.error("| |___ Failed to register module " + moduleName);
				} else {
					Wizardry.LOGGER.error("| |_ SOMETHING WENT WRONG! Item for module " + moduleName + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "'");
					Wizardry.LOGGER.error("|___ Failed to register module " + moduleName);
				}
				continue;
			} else if (ConfigValues.debugInfo) {
				Wizardry.LOGGER.info(" | | |_ Found Item " + item.getRegistryName().toString());
			}

			attributeRanges.put(AttributeRegistry.BURNOUT_MULTI, new AttributeRange(1, Integer.MAX_VALUE));
			attributeRanges.put(AttributeRegistry.MANA_MULTI, new AttributeRange(1, Integer.MAX_VALUE));
			attributeRanges.put(AttributeRegistry.POWER_MULTI, new AttributeRange(1, Integer.MAX_VALUE));
			for (Entry<String, JsonElement> entry : moduleObject.entrySet()) {
				switch (entry.getKey()) {
					case "meta": {
						itemMeta = entry.getValue().getAsJsonPrimitive().getAsInt();
						if (ConfigValues.debugInfo)
							Wizardry.LOGGER.info(" | | |_ Found Item Meta:          " + itemMeta);
						break;
					}
					case "primary_color": {
						primaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						if (ConfigValues.debugInfo)
							Wizardry.LOGGER.info(" | | |_ Found Primary Color:      " + primaryColor.getRGB());
						break;
					}
					case "secondary_color": {
						secondaryColor = new Color(Integer.parseInt(entry.getValue().getAsJsonPrimitive().getAsString(), 16));
						if (ConfigValues.debugInfo)
							Wizardry.LOGGER.info(" | | |_ Found Secondary Color:    " + secondaryColor.getRGB());
						break;
					}
					default: {
						Attribute attribute = AttributeRegistry.getAttributeFromName(entry.getKey());
						if (attribute != null) {
							if (ConfigValues.debugInfo)
								Wizardry.LOGGER.info(" | | |_ Found base " + attribute.toString() + " values:");
							JsonObject baseAttrib = entry.getValue().getAsJsonObject();

							float min = 0;
							if (baseAttrib.has("min") && baseAttrib.get("min").isJsonPrimitive()) {
								min = baseAttrib.get("min").getAsInt();
								if (min < 0) {
									if (ConfigValues.debugInfo)
										Wizardry.LOGGER.info(" | | | |_ Minimum value for " + attribute.toString() + " was " + min + ", must be a positive integer. Setting to 0");
									min = 0;
								}

								if (ConfigValues.debugInfo) Wizardry.LOGGER.info(" | | | |_ Minimum: " + min);
							}

							float max = Integer.MAX_VALUE;
							if (baseAttrib.has("max") && baseAttrib.get("max").isJsonPrimitive()) {
								max = baseAttrib.get("max").getAsFloat();
								if (max < min) {
									if (ConfigValues.debugInfo)
										Wizardry.LOGGER.info(" | | | |_ Maximum value for " + attribute.toString() + " was " + max + ", must be greater than min. Setting to min, " + min);
									max = min;
								}
								if (max > Integer.MAX_VALUE) {
									if (ConfigValues.debugInfo)
										Wizardry.LOGGER.info(" | | | |_ Maximum maximum value is " + Integer.MAX_VALUE + ", max was " + max + ". Setting to " + Integer.MAX_VALUE);
									max = Integer.MAX_VALUE;
								}

								if (ConfigValues.debugInfo) Wizardry.LOGGER.info(" | | | |_ Maximum: " + max);

							}

							attributeRanges.put(attribute, new AttributeRange(min, max));
						}
					}
				}
			}

			ModuleInstance module = ModuleInstance.createInstance(moduleClass, moduleClassFactory, moduleName, icon, new ItemStack(item, 1, itemMeta), primaryColor, secondaryColor, attributeRanges);

			if (moduleObject.has("modifiers") && moduleObject.get("modifiers").isJsonArray()) {
				Wizardry.LOGGER.info(" | | |___ Found Modifiers. About to process them");

				JsonArray attributeModifiers = moduleObject.getAsJsonArray("modifiers");
				for (JsonElement attributeModifier : attributeModifiers) {
					if (attributeModifier.isJsonObject()) {
						String attributeName = "NULL";
						AttributeRegistry.Attribute attribute = null;
						Operation operator = null;
						float amount = 0;
						JsonObject modifier = attributeModifier.getAsJsonObject();
						if (modifier.has("attribute") && modifier.get("attribute").isJsonPrimitive() && modifier.getAsJsonPrimitive("attribute").isString()) {
							attribute = AttributeRegistry.getAttributeFromName(modifier.getAsJsonPrimitive("attribute").getAsString());
							if (attribute != null)
								attributeName = attribute.getShortName();
						}
						if (modifier.has("operation") && modifier.get("operation").isJsonPrimitive() && modifier.getAsJsonPrimitive("operation").isString())
							operator = Operation.valueOf(modifier.get("operation").getAsJsonPrimitive().getAsString().toUpperCase());
						if (modifier.has("amount") && modifier.get("amount").isJsonPrimitive() && modifier.getAsJsonPrimitive("amount").isNumber())
							amount = modifier.get("amount").getAsFloat();
						if (ConfigValues.debugInfo)
							Wizardry.LOGGER.info(" | | | |_ Loading AttributeModifier for " + file.getName() + ": " + operator + " -> " + attributeName + ", " + amount);
						if (attribute != null && operator != null) {
							module.addAttribute(new AttributeModifier(attribute, amount, operator));
							if (ConfigValues.debugInfo)
								Wizardry.LOGGER.info(" | | | | |_ AttributeModifier registered successfully");
						} else {
							if (ConfigValues.debugInfo)
								Wizardry.LOGGER.error("| | | | |_ Failed to register AttributeModifier!");
							else Wizardry.LOGGER.error("| | | |_ Failed to register AttributeModifier!");
						}
					}
				}

				Wizardry.LOGGER.info(" | | |___ Modifiers Registered Successfully.");
			}

			modules.add(module);
			Wizardry.LOGGER.info(" | |_ Module " + moduleName + " registered successfully!");
		}

		modules.sort(Comparator.comparing(ModuleInstance::getNBTKey));

		Wizardry.LOGGER.info(" |");
		Wizardry.LOGGER.info(" | Module registration processing complete! (ᵔᴥᵔ)");
		Wizardry.LOGGER.info(" |_______________________________________________________________________//");
	}

	private void loadModuleClassParameters(Map<String, Object> parameters, String prefix, JsonObject from) {
		for (Entry<String, JsonElement> entry : from.entrySet()) {
			JsonElement elem = entry.getValue();

			String name;
			if (prefix != null && !prefix.isEmpty())
				name = prefix + "." + entry.getKey();
			else
				name = entry.getKey();

			if (elem.isJsonPrimitive()) {
				// ... add key value
				parameters.put(name, getJsonValue(name, elem.getAsJsonPrimitive()));
			} else if (elem.isJsonObject()) {
				// ... recur into method
				loadModuleClassParameters(parameters, name, elem.getAsJsonObject());
			} else {
				Wizardry.LOGGER.warn("| | |_ WARNING! Ignoring parameter '" + name + "' having an invalid type. It is expected to be either a primitive or an object.");
			}
		}
	}

	private Object getJsonValue(String key, JsonPrimitive elem) {
		// TODO: Move to utils
		if (elem.isString())
			return elem.getAsString();
		else if (elem.isNumber())
			return elem.getAsNumber();
		else if (elem.isBoolean())
			return elem.getAsBoolean();
		// ... TODO: Add more data types.

		String value = elem.getAsString();
		Wizardry.LOGGER.warn("| | |_ WARNING! Using fallback as string for parameter '" + key + "' having value '" + value + "'.");
		return value;
	}

	public void copyMissingModules(File directory) {
		for (ModuleInstance module : modules) {
			File file = new File(directory + "/modules/", module.getNBTKey() + ".json");
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "modules/" + module.getNBTKey() + ".json");
			if (stream == null) {
				Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not read module " + module.getNBTKey() + " from mod jar! Report this to the devs on Github!");
				continue;
			}

			try {
				FileUtils.copyInputStreamToFile(stream, file);
				Wizardry.LOGGER.info("    > Module " + module.getNBTKey() + " copied successfully from mod jar.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copyAllModules(File directory) {
		Map<String, ModContainer> modList = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> entry : modList.entrySet()) {
			for (ModuleInstance module : modules) {
				InputStream stream = LibrarianLib.PROXY.getResource(entry.getKey(), "wizmodules/" + module.getNBTKey() + ".json");
				if (stream == null) {
					Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not read module " + module.getNBTKey() + " from mod jar of '" + entry.getKey() + "'! Report this to the devs on Github!");
					continue;
				}

				try {
					FileUtils.copyInputStreamToFile(stream, new File(directory + "/wizmodules/", module.getNBTKey() + ".json"));
					Wizardry.LOGGER.info("    > Module " + module.getNBTKey() + " copied successfully from mod jar.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	Map<String, OverrideDefaultMethod> getDefaultOverrides() {
		return Collections.unmodifiableMap(IDtoOverrideDefaultMethod);
	}

	/////////////////

	static class OverrideDefaultMethod {
		private final String overrideName;
		private final OverrideMethod method;
		private final Object obj;

		OverrideDefaultMethod(String overrideName, OverrideMethod method, Object obj) {
			super();
			this.overrideName = overrideName;
			this.method = method;
			this.obj = obj;
		}

		String getOverrideName() {
			return overrideName;
		}

		OverrideMethod getMethod() {
			return method;
		}

		Object getObj() {
			return obj;
		}
	}

}
