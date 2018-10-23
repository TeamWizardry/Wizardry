package com.teamwizardry.wizardry.api.spell.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.util.DefaultHashMap;
import kotlin.Pair;
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
import java.util.Map.Entry;

/**
 * Created by Demoniaque.
 */
public class ModuleRegistry {

	public final static ModuleRegistry INSTANCE = new ModuleRegistry();

	public ArrayList<ModuleInstance> modules = new ArrayList<>();
	public HashMap<Pair<ModuleInstanceShape, ModuleInstanceEffect>, OverrideConsumer<SpellData, SpellRing, SpellRing>> runOverrides = new HashMap<>();
	public HashMap<Pair<ModuleInstanceShape, ModuleInstanceEffect>, OverrideConsumer<SpellData, SpellRing, SpellRing>> renderOverrides = new HashMap<>();
	public HashMap<String, IModule> IDtoModuleClass = new HashMap<>();

//	private Deque<Module> left = new ArrayDeque<>();

	private ModuleRegistry() {
	}

	public ModuleInstance getModule(String id) {
		for (ModuleInstance module : modules) if (module.getID().equals(id)) return module;
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
		IDtoModuleClass.clear();
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), IModule.class, RegisterModule.class, (clazz, info) -> {
			try {
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance();
				if (object instanceof IModule) {
					IModule moduleClass = (IModule)object;
					IDtoModuleClass.put(moduleClass.getClassID(), moduleClass);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public void loadModules(File directory) {
		Wizardry.logger.info(" _______________________________________________________________________\\\\");
		Wizardry.logger.info(" | Starting module registration");

		modules.clear();
		
		String[] files = directory.list();
		for (String fName : files) {
			File file = new File(directory, fName);
			
			Wizardry.logger.info(" | |");
			Wizardry.logger.info(" | |_ Parsing module configuration " + fName);

			if (!file.exists()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! " + file.getName() + " does NOT exist.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}

			if (!file.canRead()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Something is preventing me from reading " + file.getName());
				Wizardry.logger.error("| |___ Failed to parse " + fName);
			}

			JsonElement element;
			try {
				element = new JsonParser().parse(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}

			if (element == null) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Could not parse " + fName + ". Invalid json.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}

			if (!element.isJsonObject()) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! " + fName + "'s json is NOT a Json Object.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}
			JsonObject moduleObject = element.getAsJsonObject();

			// Get Class ID
			if (!moduleObject.has("type")) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! No 'type' key found in " + file.getName() + ". Unknown item to use for element.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}
			
			String moduleClassID = moduleObject.get("type").getAsString();
			IModule moduleClass = IDtoModuleClass.get(moduleClassID);
			if (moduleClass == null) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Referenced type " + moduleClassID + " is unknown.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}

			// Get Name
			if (!moduleObject.has("name")) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! No 'name' key found in " + file.getName() + ". Unknown name to use for element.");
				Wizardry.logger.error("| |___ Failed to parse " + fName);
				continue;
			}
			
			String moduleName = moduleObject.get("name").getAsString();

			Wizardry.logger.info(" | | |_ Registering module " + moduleName + " of class " + moduleClassID);
			
			// Get optional icon
			ResourceLocation icon = null;
			if (moduleObject.has("icon")) {
				String iconID = moduleObject.get("icon").getAsString();
				icon = new ResourceLocation(iconID);
			}
			
			if (!moduleObject.has("item")) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! No 'item' key found in " + file.getName() + ". Unknown item to use for element.");
				Wizardry.logger.error("| |___ Failed to register module " + moduleName);
				continue;
			}

			Color primaryColor = Color.WHITE;
			Color secondaryColor = Color.WHITE;
			int itemMeta = 0;
			DefaultHashMap<Attribute, AttributeRange> attributeRanges = new DefaultHashMap<>(AttributeRange.BACKUP);

			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(moduleObject.getAsJsonPrimitive("item").getAsString()));
			if (item == null || item.getRegistryName() == null) {
				Wizardry.logger.error("| | |_ SOMETHING WENT WRONG! Item for module " + moduleName + " does not exist '" + moduleObject.getAsJsonPrimitive("item").getAsString() + "'");
				Wizardry.logger.error("| |___ Failed to register module " + moduleName);
				continue;
			} else {
				Wizardry.logger.info(" | | |_ Found Item " + item.getRegistryName().toString());
			}

			attributeRanges.put(AttributeRegistry.BURNOUT_MULTI, new AttributeRange(1, 0, Integer.MAX_VALUE));
			attributeRanges.put(AttributeRegistry.MANA_MULTI, new AttributeRange(1, 0, Integer.MAX_VALUE));
			attributeRanges.put(AttributeRegistry.POWER_MULTI, new AttributeRange(1, 0, Integer.MAX_VALUE));
			for (Entry<String, JsonElement> entry : moduleObject.entrySet()) {
				switch (entry.getKey()) {
					case "meta": {
						itemMeta = entry.getValue().getAsJsonPrimitive().getAsInt();
						Wizardry.logger.info(" | | |_ Found Item Meta:          " + itemMeta);
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
					default:
					{
						Attribute attribute = AttributeRegistry.getAttributeFromName(entry.getKey());
						if (attribute != null)
						{
							Wizardry.logger.info(" | | |_ Found base " + attribute.toString() + " values:");
							JsonObject baseAttrib = entry.getValue().getAsJsonObject();

							double min = 0;
							if (baseAttrib.has("min") && baseAttrib.get("min").isJsonPrimitive())
							{
								min = baseAttrib.get("min").getAsInt();
								if (min < 0)
								{
									Wizardry.logger.info(" | | | |_ Minimum value for " + attribute.toString() + " was " + min + ", must be a positive integer. Setting to 0");
									min = 0;
								}

								Wizardry.logger.info(" | | | |_ Minimum: " + min);
							}
							
							double max = Integer.MAX_VALUE;
							if (baseAttrib.has("max") && baseAttrib.get("max").isJsonPrimitive())
							{
								max = baseAttrib.get("max").getAsDouble();
								if (max < min)
								{
									Wizardry.logger.info(" | | | |_ Maximum value for " + attribute.toString() + " was " + max + ", must be greater than min. Setting to min, " + min);
									max = min;
								}
								if (max > Integer.MAX_VALUE)
								{
									Wizardry.logger.info(" | | | |_ Maximum maximum value is " + Integer.MAX_VALUE + ", max was " + max + ". Setting to " + Integer.MAX_VALUE);
									max = Integer.MAX_VALUE;
								}

								Wizardry.logger.info(" | | | |_ Maximum: " + max);

							}
							
							double base = min;
							if (baseAttrib.has("base") && baseAttrib.get("base").isJsonPrimitive())
							{
								base = baseAttrib.get("base").getAsDouble();
								if (base < min)
								{
									Wizardry.logger.info(" | | | |_ Base value for " + attribute.toString() + " was " + base + ", must be greater than min, " + min + ". Setting to " + min);
									base = min;
								}
								else if (base > max)
								{
									Wizardry.logger.info(" | | | |_ Base value for " + attribute + " was " + base + ", must be less than max, " + max + ". Setting to " + max);
									base = max;
								}

								Wizardry.logger.info(" | | | |_ Base: " + base);
							}
							attributeRanges.put(attribute, new AttributeRange(base, min, max));
						}
					}
				}
			}
			
			ModuleInstance module = ModuleInstance.createInstance(moduleClass, moduleName, icon, new ItemStack(item, 1, itemMeta), primaryColor, secondaryColor, attributeRanges);

			if (moduleObject.has("modifiers") && moduleObject.get("modifiers").isJsonArray()) {
				Wizardry.logger.info(" | | |___ Found Modifiers. About to process them");

				JsonArray attributeModifiers = moduleObject.getAsJsonArray("modifiers");
				for (JsonElement attributeModifier : attributeModifiers) {
					if (attributeModifier.isJsonObject()) {
						String attributeName = "NULL";
						AttributeRegistry.Attribute attribute = null;
						Operation operator = null;
						double amount = 0;
						JsonObject modifier = attributeModifier.getAsJsonObject();
						if (modifier.has("attribute") && modifier.get("attribute").isJsonPrimitive() && modifier.getAsJsonPrimitive("attribute").isString()) {
							attribute = AttributeRegistry.getAttributeFromName(modifier.getAsJsonPrimitive("attribute").getAsString());
							if (attribute != null)
								attributeName = attribute.getShortName();
						}
						if (modifier.has("operation") && modifier.get("operation").isJsonPrimitive() && modifier.getAsJsonPrimitive("operation").isString())
							operator = Operation.valueOf(modifier.get("operation").getAsJsonPrimitive().getAsString().toUpperCase());
						if (modifier.has("amount") && modifier.get("amount").isJsonPrimitive() && modifier.getAsJsonPrimitive("amount").isNumber())
							amount = modifier.get("amount").getAsDouble();
						Wizardry.logger.info(" | | | |_ Loading AttributeModifier for " + file.getName() + ": " + operator + " -> " + attributeName + ", " + amount);
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

			modules.add(module);
//			processed.add(moduleClass);
			Wizardry.logger.info(" | |_ Module " + moduleName + " registered successfully!");
		}

/*		primary:
		for (Module module : modules) {
			for (IModule<?> moduleClass : processed)
				if (module1.getID().equals(module2.getID())) continue primary;

			left.add(module1);
		}

		if (!left.isEmpty()) {
			Wizardry.logger.error("|");
			Wizardry.logger.error("|_ Missing or ignored modules detected in modules directory:");
			for (Module module : left) Wizardry.logger.error("| |_ " + module.getID());
		}
		left.clear(); */

//		modules.clear();
//		modules.addAll(processed);

		modules.sort(Comparator.comparing(ModuleInstance::getID));

		Wizardry.logger.info(" |");
		Wizardry.logger.info(" | Module registration processing complete! (ᵔᴥᵔ)");
		Wizardry.logger.info(" |_______________________________________________________________________//");
	}

	public void loadModuleOverrides()
	{
		for (ModuleInstance effect : getModules(ModuleType.EFFECT))
		{
			if (!(effect instanceof ModuleInstanceEffect))
				continue;

			((ModuleInstanceEffect) effect).runOverrides.forEach((moduleID, override) -> {
				ModuleInstance shape = getModule(moduleID);
				if (shape instanceof ModuleInstanceShape)
				{
					runOverrides.put(new Pair<>((ModuleInstanceShape) shape, (ModuleInstanceEffect) effect), override);
					Wizardry.logger.info(" | Registered " + shape.getReadableName() + " -> " + effect.getReadableName() + " run override.");
				}
			});
			
			((ModuleInstanceEffect) effect).renderOverrides.forEach((moduleID, override) -> {
				ModuleInstance shape = getModule(moduleID);
				if (shape instanceof ModuleInstanceShape)
				{
					renderOverrides.put(new Pair<>((ModuleInstanceShape) shape, (ModuleInstanceEffect) effect), override);
					Wizardry.logger.info(" | Registered " + shape.getReadableName() + " -> " + effect.getReadableName() + " renderSpell override.");
				}
			});
		}
	}
	
	public void copyMissingModules(File directory) {
		for (ModuleInstance module : modules) {
			File file = new File(directory + "/modules/", module.getID() + ".json");
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "modules/" + module.getID() + ".json");
			if (stream == null) {
				Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not read module " + module.getID() + " from mod jar! Report this to the devs on Github!");
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
	
	public void copyAllModules(File directory)
	{
		for (ModuleInstance module : modules)
		{
			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "modules/" + module.getID() + ".json");
			if (stream == null)
			{
				Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not read module " + module.getID() + " from mod jar! Report this to the devs on Github!");
				continue;
			}
			
			try
			{
				FileUtils.copyInputStreamToFile(stream, new File(directory + "/modules/", module.getID() + ".json"));
				Wizardry.logger.info("    > Module " + module.getID() + " copied successfully from mod jar.");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
