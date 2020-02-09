package com.teamwizardry.wizardry.common.spell;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.ModuleShape;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternEffect;

import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles loading Modules from yaml resources. Relies heavily on a cohesive
 * structure:
 * 
 * <pre>
 * module: modid:pattern
 * name: moduleName
 * item: modid:item
 * color:
 *   primary: integer (base 8, 10, or 16)
 *   secondary: integer (base 8, 10, or 16)
 * attributes:
 *   mana:
 *     min: integer (default 0)
 *     max: integer (default 2^32-1)
 *   burnout:
 *     ... (repeat for all relevant attributes)
 * tags:
 * - tagOne
 * - tagTwo
 * - ...
 * hiddenTags:
 * - hiddenTagOne
 * - hiddenTagTwo
 * - ...
 * </pre>
 * 
 * These individual tags must be in any order, but the nesting structure must be
 * preserved. The use of {@code []} may be used to inline lists of values
 * beginning with -, while <code>{}</code> may be used to inline other values,
 * but ultimately the structure of the yaml must be as written.
 */
@SuppressWarnings("unchecked")
public class ModuleLoader
{
    private static final String MODULE = "module";
    private static final String NAME = "name";
    private static final String ITEM = "item";
    private static final String COLOR = "color";
    private static final String PRIMARY = "primary";
    private static final String SECONDARY = "secondary";
    private static final String ATTRIBUTES = "attributes";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String TAGS = "tags";
    private static final String HIDDEN = "hiddenTags";
    
    private static final Yaml yaml = new Yaml();
    private static final Logger LOGGER = LogManager.getLogger();
    
    private static final String folder = Wizardry.MODID + "/module";
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/module/} and any subfolders
     * 
     * @see #loadModules(InputStream, Function, Function)
     */
    public static void loadModules(IResourceManager resourceManager)
    {
        List<Module> shapes = new LinkedList<>();
        
        for (ResourceLocation file : resourceManager.getAllResourceLocations(folder, n -> n.endsWith(".yaml")))
        {
            try
            {
                for (IResource resource : resourceManager.getAllResources(file))
                    shapes.addAll(loadModules(resource.getInputStream(),
                                              GameRegistry.findRegistry(Pattern.class)::getValue,
                                              ForgeRegistries.ITEMS::getValue));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        shapes.forEach(LOGGER::info);
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
        return StreamSupport.stream(yaml.loadAll(file).spliterator(), false)
                            .map(map -> compileModule(
                                    (Map<String, Object>) map,
                                    patternSupplier,
                                    itemSupplier))
                            .collect(Collectors.toList());
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
    private static Module compileModule(Map<String, Object> yaml, Function<ResourceLocation, Pattern> patternSupplier, Function<ResourceLocation, Item> itemSupplier)
    {
        // Straightforward components
        Pattern pattern = patternSupplier.apply(new ResourceLocation((String) yaml.get(MODULE)));
        String name = (String) yaml.get(NAME);
        Item item = itemSupplier.apply(new ResourceLocation((String) yaml.get(ITEM)));
        List<String> tags = (List<String>) yaml.get(TAGS);
        List<String> hiddenTags = (List<String>) yaml.get(HIDDEN);
        // Attributes
        Map<String, Map<String, Integer>> attributeMap = (Map<String, Map<String, Integer>>) yaml.get(ATTRIBUTES);
        Map<String, Range<Integer>> attributeRanges = new HashMap<>();
        attributeMap.entrySet().stream()
                .forEach(attribute -> {
                    int min = attribute.getValue().getOrDefault(MIN, 0);
                    int max = attribute.getValue().getOrDefault(MAX, Integer.MAX_VALUE);
                    attributeRanges.put(attribute.getKey(), Range.between(min, max));
                });

        if (pattern instanceof PatternEffect)
        {
            // Colors
            Map<String, Integer> colorMap = (Map<String, Integer>) yaml.get(COLOR);
            Color primary = new Color(colorMap.get(PRIMARY));
            Color secondary = new Color(colorMap.get(SECONDARY));
            return new ModuleEffect(pattern, name, item, primary, secondary, attributeRanges, tags, hiddenTags);
        }
        // Only pattern types are Shapes and Effects, so if not an Effect...
        return new ModuleShape(pattern, name, item, attributeRanges, tags, hiddenTags);
    }
}
