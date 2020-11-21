package com.teamwizardry.wizardry.common.spell.loading;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Module;
import com.teamwizardry.wizardry.common.spell.component.ModuleEffect;
import com.teamwizardry.wizardry.common.spell.component.ModuleShape;

import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles loading Modules from yaml resources. Relies heavily on a cohesive
 * structure:
 *
 * <pre>
 * module: modid:pattern
 * name: moduleName
 * items:
 * - modid:item
 * - modid:item
 * - modid:item
 * ... (repeat for all items in recipe)
 * mana: double
 * burnout: double
 * modifiers:
 *   modifier:
 *      cost: double (default 1.05)
 *      values: (default [1])
 *      - double
 *      - double
 *      - double
 *      - double
 *      - double
 *  ... (repeat for all relevant modifiers)
 * form: string (exclusive to Shapes)
 * action: string (exclusive to Effects)
 * element: string
 * </pre>
 * 
 * These individual tags must be in any order, but the nesting structure must be
 * preserved. The use of {@code []} may be used to inline lists of values
 * beginning with -, while <code>{}</code> may be used to inline other values,
 * but ultimately the structure of the yaml must be as written.
 */
public class ModuleLoader extends YamlLoader
{
    private static final String MODULE = "module";
    private static final String NAME = "name";
    private static final String ITEMS = "items";
    private static final String MANA = "mana";
    private static final String BURNOUT = "burnout";
    private static final String FORM = "form";
    private static final String ACTION = "action";
    private static final String ELEMENT = "element";
    private static final String MODIFIERS = "modifiers";
    private static final String COST = "cost";
    private static final String VALUES = "values";
    
    private static final String folder =  Wizardry.MODID + "/module";
    
    /**
     * Unconstructable
     */
    private ModuleLoader() {}
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/module/} and any subfolders
     * 
     * @see #loadModules(InputStream, Function, Function)
     */
    public static void loadModules(IReloadableResourceManager resourceManager)
    {
        YamlLoader.loadYamls(resourceManager, folder,
                map -> ModuleLoader.compileModule(map,
                                                  GameRegistry.findRegistry(Pattern.class)::getValue,
                                                  ForgeRegistries.ITEMS::getValue))
                  .forEach(ComponentRegistry::addModule);
    }
    
    /**
     * Creates a {@link Module} list from an input stream, using the given
     * supplier functions for both a {@link Pattern} and an {@link Item}
     * 
     * @param file            the input stream to read modules from
     * @param patternSupplier the function used to convert a {@code modid:name}
     *                        string into a {@code Pattern}
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return the List of {@code Module} objects compiled from the input yaml
     *         stream
     */
    public static List<Module> loadModules(InputStream file, Function<ResourceLocation, Pattern> patternSupplier, Function<ResourceLocation, Item> itemSupplier)
    {
        return YamlLoader.loadYamls(file,
                map -> ModuleLoader.compileModule(map, patternSupplier, itemSupplier));
    }

    /**
     * Helper method to compile the map from parsing a module yaml
     * 
     * @param yaml The parsed yaml
     * @param patternSupplier the function used to convert a {@code modid:name}
     *                        string into a {@code Pattern}
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return A {@link Module} constructed from the values in the yaml
     */
    @SuppressWarnings("unchecked")
    private static Module compileModule(Map<String, Object> yaml, Function<ResourceLocation, Pattern> patternSupplier, Function<ResourceLocation, Item> itemSupplier)
    {
        // Straightforward components
        Pattern pattern = patternSupplier.apply(new ResourceLocation((String) yaml.get(MODULE)));
        String name = (String) yaml.get(NAME);
        List<Item> items = ((List<String>) yaml.get(ITEMS)).stream().map(ResourceLocation::new).map(itemSupplier::apply).collect(Collectors.toList());
        double mana = ((Number) yaml.get(MANA)).doubleValue();
        double burnout = ((Number) yaml.get(BURNOUT)).doubleValue();
        String form = (String) yaml.get(FORM);
        String action = (String) yaml.get(ACTION);
        String element = (String) yaml.get(ELEMENT);
        
        // Parse modifier values
        Map<String, Double> modifierCosts = new HashMap<>();
        Map<String, List<Double>> attributeValues = new HashMap<>();
        Map<String, Map<String, Object>> modifierMap = (Map<String, Map<String, Object>>) yaml.get(MODIFIERS);
        for (String attribute : modifierMap.keySet())
        {
            Map<String, Object> modifierValues = modifierMap.get(attribute);
            if (modifierValues.containsKey(COST))
            {
                double cost = (double) modifierValues.get(COST);
                modifierCosts.put(attribute, cost);
            }
            List<Double> values = ((List<Number>) modifierValues.get(VALUES)).stream().map(Number::doubleValue).collect(Collectors.toList());
            attributeValues.put(attribute, values);
        }

        if (pattern instanceof PatternEffect)
        {
            return new ModuleEffect((PatternEffect) pattern, name, items, mana, burnout, action, element, modifierCosts, attributeValues);
        }
        // Only pattern types are Shapes and Effects, so if not an Effect...
        return new ModuleShape((PatternShape) pattern, name, items, mana, burnout, form, element, modifierCosts, attributeValues);
    }
}
