package com.teamwizardry.wizardry.common.spell;

import com.google.common.base.Joiner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.*;
import net.minecraft.item.Item;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        if(!(resourceManager instanceof SimpleReloadableResourceManager))
            return;
        List<Module> shapes = new LinkedList<>();
        for (String resourceNamespace : resourceManager.getResourceNamespaces()) {
            if(resourceNamespace.equals("minecraft") || resourceNamespace.equals("forge"))
                continue;
            FallbackResourceManager fallbackResourceManager = ((SimpleReloadableResourceManager) resourceManager).namespaceResourceManagers.get(resourceNamespace);
            for (IResourcePack pack : fallbackResourceManager.resourcePacks){
                if(pack.getResourceNamespaces(ResourcePackType.SERVER_DATA).isEmpty() || !(pack instanceof ResourcePack))
                    continue;
                if(pack instanceof ModFileResourcePack){
                    for (ResourceLocation resourceLocation : loadModulesFromModFile(ResourcePackType.SERVER_DATA, folder, Integer.MAX_VALUE, file -> file.endsWith(".yaml"), ((ModFileResourcePack) pack).getModFile())) {
                        LOGGER.info("Found Module file: " + resourceLocation);
                        try {
                            shapes.addAll(loadModules(pack.getResourceStream(ResourcePackType.SERVER_DATA, resourceLocation),
                                    GameRegistry.findRegistry(Pattern.class)::getValue,
                                    ForgeRegistries.ITEMS::getValue));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (ResourceLocation resourceLocation :  pack.getAllResourceLocations(ResourcePackType.SERVER_DATA, (String) pack.getResourceNamespaces(ResourcePackType.SERVER_DATA).toArray()[0], folder, Integer.MAX_VALUE, file -> file.endsWith(".yaml"))) {
                        LOGGER.info("Found Module file: " + resourceLocation);
                        try {
                            shapes.addAll(loadModules(pack.getResourceStream(ResourcePackType.SERVER_DATA, resourceLocation),
                                    GameRegistry.findRegistry(Pattern.class)::getValue,
                                    ForgeRegistries.ITEMS::getValue));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }


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

    /**
     * Copy of {@link ModFileResourcePack#getAllResourceLocations(ResourcePackType, String, String, int, Predicate)}
     * but seeing as that does not work well with a pathIn with subfolders, this is necessary
     *
     * @param type If it's either a data folder {@link ResourcePackType#SERVER_DATA} or an assets folder {@link ResourcePackType#CLIENT_RESOURCES}
     * @param pathIn the path in which to look for Resource Locations
     * @param maxDepth the amount of recursion in regards to subfolders
     * @param filter A predicate to test the file name against
     * @param modFile The Mod file in which to search
     * @return A {@link Collection<ResourceLocation>} constructed from the values in the yaml
     */
    public static Collection<ResourceLocation> loadModulesFromModFile(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter, ModFile modFile)
    {
        try
        {
            Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);
            return Files.walk(root).
                    map(path -> root.relativize(path.toAbsolutePath())).
                    filter(path -> path.getNameCount() > 1 && path.getNameCount() - 1 <= maxDepth). // Make sure the depth is within bounds, ignoring domain
                    filter(path -> !path.toString().endsWith(".mcmeta")). // Ignore .mcmeta files
                    filter(path -> path.toString().startsWith(inputPath.toString())). // Make sure the target path is inside this one (again ignoring domain)
                    filter(path -> filter.test(path.getFileName().toString())). // Test the file name against the predicate
                    filter(path -> path.toString().startsWith(inputPath.toString())).
                    // Finally we need to form the RL, so use the first name as the domain, and the rest as the path
                    // It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
                    // Join the path names ourselves to force forward slashes
                    //
                    map(path -> new ResourceLocation(path.getName(0).toString(), Joiner.on('/').join(path.subpath(1,Math.min(maxDepth, path.getNameCount()))))).
                    collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }
}
