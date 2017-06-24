package com.teamwizardry.wizardry.api.spell.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
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
 * Created by LordSaad.
 */
public class ModuleRegistry {

	public static ModuleRegistry INSTANCE = new ModuleRegistry();

	public ArrayList<Module> modules = new ArrayList<>();

	private File directory;

	private Deque<Module> left = new ArrayDeque<>();

	private ModuleRegistry() {
	}

	public void registerModule(Module module) {
		modules.add(module);
	}

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

	public void loadUnprocessedModules() {
		modules.clear();
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

	public void processModules() {
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting module registration processing.");

		HashSet<Module> processed = new HashSet<>();

		for (Module module : modules) {
			File file = new File(directory, module.getID() + ".json");

			if (!file.exists()) {
				Wizardry.logger.error("  > SOMETHING WENT WRONG! " + module.getID() + ".json does NOT exist. Ignoring module...");
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

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
			if (item == null) {
				Wizardry.logger.error("    > WARNING! Item for module " + module.getID() + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "' from " + file.getName() + ".json");
				continue;
			}

			int itemMeta = 0;
			if (moduleObject.has("item_meta") && moduleObject.get("item_meta").isJsonPrimitive() && moduleObject.getAsJsonPrimitive("item_meta").isNumber()) {
				itemMeta = moduleObject.getAsJsonPrimitive("item_meta").getAsInt();
			}

			ItemStack stack = new ItemStack(item, 1, itemMeta);

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
