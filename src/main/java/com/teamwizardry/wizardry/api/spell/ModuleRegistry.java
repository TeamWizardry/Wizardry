package com.teamwizardry.wizardry.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.teamwizardry.wizardry.proxy.CommonProxy.createModuleRegistryFile;

/**
 * Created by LordSaad.
 */
public class ModuleRegistry {

	public static ModuleRegistry INSTANCE = new ModuleRegistry();

	public ArrayList<Module> modules = new ArrayList<>();

	private JsonObject object;

	private Deque<Module> left = new ArrayDeque<>();

	private ModuleRegistry() {
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), Module.class, RegisterModule.class, (clazz, info) -> {
			try {
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance();
				if (object instanceof Module) registerModule((Module) object);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public void registerModule(Module module) {
		modules.add(module);
	}

	@Nullable
	public Module getModule(String id) {
		for (Module module : modules) if (module.getID().equals(id)) return module.copy();
		return null;
	}

	@Nullable
	public Module getModule(ItemStack itemStack) {
		for (Module module : modules)
			if (ItemStack.areItemStacksEqual(itemStack, module.getItemStack())) {
				return module.copy();
			}
		return null;
	}

	@Nonnull
	public ArrayList<Module> getModules(ModuleType type) {
		ArrayList<Module> modules = new ArrayList<>();
		for (Module module : this.modules) if (module.getModuleType() == type) modules.add(module.copy());

		modules.sort(Comparator.comparing(Module::getReadableName));
		return modules;
	}

	public void processModules() {
		String[] typeArray = new String[]{"shapes", "events", "effects", "modifiers"};

		HashSet<Module> processed = new HashSet<>();

		for (String type : typeArray) {
			if (object.has(type) && object.get(type).isJsonArray()) {
				JsonArray array = object.getAsJsonArray(type);
				for (JsonElement element : array) {
					if (!element.isJsonObject()) {
						Wizardry.logger.error("WARNING! An element in the module_registry.json is NOT a JsonObject: " + element.toString());
						continue;
					}
					JsonObject moduleObject = element.getAsJsonObject();
					if (!moduleObject.has("id")) {
						Wizardry.logger.error("WARNING! An element in the module_registry.json does NOT have an 'id' key! Unknown module to hook for element: " + element.toString());
						continue;
					}

					if (!moduleObject.has("item")) {
						Wizardry.logger.error("WARNING! An element in the module_registry.json does NOT have an 'item' key! Unknown item to use for element: " + element.toString());
						continue;
					}
					String id = moduleObject.getAsJsonPrimitive("id").getAsString();

					Module module = getModule(id);
					if (module == null) {
						Wizardry.logger.error("WARNING! Could not find a module with id '" + id + "'" + " from module_registry.json");
						continue;
					}

					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
					if (item == null) {
						Wizardry.logger.error("WARNING! Item for module " + module.getReadableName() + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "' in module_registry.json");
						continue;
					}

					ItemStack stack = new ItemStack(item);

					module.setItemStack(stack);

					String[] keys = new String[]{"mana_drain", "burnout_fill", "cooldown_time", "chargeup_time", "primary_color", "secondary_color"};
					int i = 0;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isNumber()) {
						module.setManaDrain(moduleObject.getAsJsonPrimitive(keys[i]).getAsDouble());
					}
					i = 1;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isNumber()) {
						module.setBurnoutFill(moduleObject.getAsJsonPrimitive(keys[i]).getAsDouble());
					}
					i = 2;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isNumber()) {
						module.setCooldownTime(moduleObject.getAsJsonPrimitive(keys[i]).getAsInt());
					}
					i = 3;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isNumber()) {
						module.setChargeupTime(moduleObject.getAsJsonPrimitive(keys[i]).getAsInt());
					}
					i = 4;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isString()) {
						module.setPrimaryColor(new Color(Integer.parseInt(moduleObject.getAsJsonPrimitive(keys[i]).getAsString(), 16)));
					}
					i = 5;
					if (moduleObject.has(keys[i]) && moduleObject.get(keys[i]).isJsonPrimitive() && moduleObject.getAsJsonPrimitive(keys[i]).isString()) {
						module.setSecondaryColor(new Color(Integer.parseInt(moduleObject.getAsJsonPrimitive(keys[i]).getAsString(), 16)));
					}

					processed.add(module);
				}
			}
		}

		primary:
		for (Module module1 : modules) {
			for (Module module2 : processed)
				if (module1.getID().equals(module2.getID())) continue primary;

			left.add(module1);
		}

		if (!left.isEmpty()) {
			Wizardry.logger.error("Missing modules detected in module_registry.json:");
			for (Module module : left) Wizardry.logger.error("    - " + module.getID());
			long currentTime = System.currentTimeMillis();

			HashMap<ModuleType, JsonArray> arrays = new HashMap<>();
			while (!left.isEmpty()) {
				Module module = left.pop();
				Wizardry.logger.info("Attempting to add default module " + module.getID() + " into module_registry.json");

				JsonArray array;

				if (arrays.containsKey(module.getModuleType())) {
					array = arrays.get(module.getModuleType());
				} else {
					array = object.getAsJsonArray(module.getModuleType().name().toLowerCase());
					arrays.put(module.getModuleType(), array);
				}

				if (object.has(module.getModuleType().name().toLowerCase()) && object.get(module.getModuleType().name().toLowerCase()).isJsonArray()) {

					JsonArray originalArray = CommonProxy.originalModuleRegistryObject.get(module.getModuleType().name().toLowerCase()).getAsJsonArray();

					for (JsonElement element : originalArray) {
						if (!element.isJsonObject()) continue;
						JsonObject originalObject = element.getAsJsonObject();
						if (originalObject.has("id") && originalObject.get("id").isJsonPrimitive()) {
							if (module.getID().equals(originalObject.get("id").getAsString())) {
								array.add(originalObject);
								Wizardry.logger.info(module.getID() + " added successfully into module_registry.json!");
								break;
							}
						}
					}
				}
			}

			Set<ModuleType> typesIncluded = arrays.keySet();
			for (ModuleType type : ModuleType.values()) {
				if (typesIncluded.contains(type)) continue;

				Wizardry.logger.info("Adding original modules of type " + type.name() + " back into new config.");
				arrays.put(type, object.getAsJsonArray(type.name().toLowerCase()));
			}

			File config = new File(CommonProxy.directory.getPath() + "/" + Wizardry.MODID, "module_registry.json");

			if (config.exists())
				if (!config.delete()) {
					Wizardry.logger.error("SOMETHING WENT WRONG! Could not delete old module_registry.json");
					return;
				} else Wizardry.logger.info("Old module_registry.json deleted successfully!");


			if (!createModuleRegistryFile(config.getParentFile())) {
				Wizardry.logger.error("SOMETHING WENT WRONG! Could not create new module_registry.json");
				return;
			} else Wizardry.logger.info("New module_registry.json has been created successfully!");

			JsonParser parser = new JsonParser();
			try {
				JsonElement element = parser.parse(new FileReader(config));
				JsonObject obj = element.getAsJsonObject();

				setJsonObject(obj);
				Wizardry.logger.info("module_registry.json updated successfully! It took " + (System.currentTimeMillis() - currentTime) / 1000.0 + " seconds.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Wizardry.logger.info("No missing modules found! Your module_registry.json is update to date! huzzah! (ﾉ≧∀≦)ﾉ・‥…━━━★");
		}

		modules.clear();
		modules.addAll(processed);
	}

	public void setJsonObject(JsonObject object) {
		this.object = object;
	}
}
