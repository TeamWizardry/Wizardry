package com.teamwizardry.wizardry.common.spell.loading;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Joiner;

import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;

/**
 * Handles loading data from yaml resources. Relies heavily on a cohesive
 * structure.
 * 
 * @see ModifierLoader
 * @see ModuleLoader
 */
public class YamlLoader
{
    private static final Yaml yaml = new Yaml();
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * DO NOT CONSTRUCT
     */
    protected YamlLoader() {}
    
    /**
     * Reads all .yaml files under the given folder and in any subfolders
     * 
     * @see #loadModifiers(InputStream, Function, Function)
     */
    protected static <T> List<T> loadYamls(IReloadableResourceManager resourceManager, String folder, Function<Map<String, Object>, T> compiler)
    {
        if(!(resourceManager instanceof SimpleReloadableResourceManager))
            return new LinkedList<>();
        List<T> objects = new LinkedList<>();
        for (String resourceNamespace : resourceManager.getResourceNamespaces()) {
            if(resourceNamespace.equals("minecraft") || resourceNamespace.equals("forge"))
                continue;
            FallbackResourceManager fallbackResourceManager = ((SimpleReloadableResourceManager) resourceManager).namespaceResourceManagers.get(resourceNamespace);
            for (IResourcePack pack : fallbackResourceManager.resourcePacks){
                if(pack.getResourceNamespaces(ResourcePackType.SERVER_DATA).isEmpty() || !(pack instanceof ResourcePack))
                    continue;
                if(pack instanceof ModFileResourcePack){
                    for (ResourceLocation resourceLocation : loadYamlsFromModFile(ResourcePackType.SERVER_DATA, folder, Integer.MAX_VALUE, file -> file.endsWith(".yaml"), ((ModFileResourcePack) pack).getModFile())) {
                        LOGGER.info("Found Yaml file: " + resourceLocation);
                        try {
                            objects.addAll(loadYamls(pack.getResourceStream(ResourcePackType.SERVER_DATA, resourceLocation),
                                    compiler));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (ResourceLocation resourceLocation :  pack.getAllResourceLocations(ResourcePackType.SERVER_DATA, (String) pack.getResourceNamespaces(ResourcePackType.SERVER_DATA).toArray()[0], folder, Integer.MAX_VALUE, file -> file.endsWith(".yaml"))) {
                        LOGGER.info("Found Yaml file: " + resourceLocation);
                        try {
                            objects.addAll(loadYamls(pack.getResourceStream(ResourcePackType.SERVER_DATA, resourceLocation),
                                    compiler));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        return objects;
    }
    
    /**
     * Creates a list from an input stream, using the given compliation function
     * 
     * @param file            the input stream to read yamls from
     * @param compiler        the function that takes the parsed yaml and produces an object
     * @return the List of objects compiled from the input yaml stream
     */
    @SuppressWarnings("unchecked")
    protected static <T> List<T> loadYamls(InputStream file, Function<Map<String, Object>, T> compiler)
    {
        return StreamSupport.stream(yaml.loadAll(file).spliterator(), false)
                            .map(map -> compiler.apply((Map<String, Object>) map))
                            .collect(Collectors.toList());
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
    protected static Collection<ResourceLocation> loadYamlsFromModFile(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter, ModFile modFile)
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
