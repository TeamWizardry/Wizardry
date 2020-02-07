package com.teamwizardry.wizardry.common.spell;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternRegistry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
    private static final FilenameFilter filter = (dir, name) -> name.endsWith(".yaml");
    
    public static boolean forgeLoaded = false;
    
    public static void loadModules()
    {
        File modulesDir = new File("../src/main/resources/assets/wizardry/modules");
        File shapesDir = new File(modulesDir, "shapes");
        List<Module> shapes = Arrays.stream(shapesDir.list(filter))
                                    .map(File::new)
                                    .map(ModuleLoader::loadModules)
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList());
        shapes.forEach(LOGGER::info);
    }
    
    public static List<Module> loadModules(File file)
    {
        List<Module> modules = new LinkedList<>();
        FileInputStream stream;
        try { stream = new FileInputStream(file); }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return modules;
        }
        yaml.loadAll(stream).forEach(map -> 
            modules.add(compileModule((Map<String, Object>) map)));
        return modules;
    }

    /**
     * Helper method to compile the map from parsing a module yaml
     * 
     * @param yaml The parsed yaml
     * @return A {@link Module} constructed from the values in the yaml
     */
    private static Module compileModule(Map<String, Object> yaml)
    {
        // Straightforward components
        Pattern pattern = PatternRegistry.getPattern((String) yaml.get(MODULE));
        String name = (String) yaml.get(NAME);
        Item item = forgeLoaded ? ForgeRegistries.ITEMS.getValue(new ResourceLocation((String) yaml.get(ITEM))) : null;
        List<String> tags = (List<String>) yaml.get(TAGS);
        List<String> hiddenTags = (List<String>) yaml.get(HIDDEN);
        // Colors
        Map<String, Integer> colorMap = (Map<String, Integer>) yaml.get(COLOR);
        Color primary = new Color(colorMap.get(PRIMARY));
        Color secondary = new Color(colorMap.get(SECONDARY));
        // Attributes
        Map<String, Map<String, Integer>> attributeMap = (Map<String, Map<String, Integer>>) yaml.get(ATTRIBUTES);
        Map<String, Range<Integer>> attributeRanges = new HashMap<>();
        attributeMap.entrySet().stream()
                .forEach(attribute -> {
                    int min = attribute.getValue().getOrDefault(MIN, 0);
                    int max = attribute.getValue().getOrDefault(MAX, Integer.MAX_VALUE);
                    attributeRanges.put(attribute.getKey(), Range.between(min, max));
                });

        return new Module(pattern, name, item, primary, secondary, attributeRanges, tags, hiddenTags);
    }
}
