package com.teamwizardry.wizardry.common.spell;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Modifier;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Handles loading Modules from yaml resources. Relies heavily on a cohesive
 * structure:
 * 
 * <pre>
 * </pre>
 * 
 * These individual tags must be in any order, but the nesting structure must be
 * preserved. The use of {@code []} may be used to inline lists of values
 * beginning with -, while <code>{}</code> may be used to inline other values,
 * but ultimately the structure of the yaml must be as written.
 */
public class ModifierLoader extends YamlLoader
{
    private static final String folder =  Wizardry.MODID + "/modifier";
    
    /**
     * Unconstructable
     */
    private ModifierLoader() {}
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/module/} and any subfolders
     * 
     * @see #loadModules(InputStream, Function, Function)
     */
    public static void loadModifiers(IReloadableResourceManager resourceManager)
    {
        YamlLoader.loadYamls(resourceManager, folder, ModifierLoader::compileModifier);
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
    public static List<Modifier> loadModules(InputStream file, Function<ResourceLocation, Pattern> patternSupplier, Function<ResourceLocation, Item> itemSupplier)
    {
        return YamlLoader.loadYamls(file, patternSupplier, itemSupplier, ModifierLoader::compileModifier);
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
    private static Modifier compileModifier(Map<String, Object> yaml, Function<ResourceLocation, Pattern> patternSupplier, Function<ResourceLocation, Item> itemSupplier)
    {
        return new Modifier();
    }
}
