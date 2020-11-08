package com.teamwizardry.wizardry.common.spell.loading;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Modifier;

import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles loading Modifiers from yaml resources. Relies heavily on a cohesive
 * structure:
 * 
 * <pre>
 * name: modifierName
 * items:
 * - modid:item
 * - modid:item
 * - ...
 * </pre>
 * 
 * These individual tags must be in any order, but the nesting structure must be
 * preserved. The use of {@code []} may be used to inline lists of values
 * beginning with -, while <code>{}</code> may be used to inline other values,
 * but ultimately the structure of the yaml must be as written.
 */
public class ModifierLoader extends YamlLoader
{
    private static final String NAME = "name";
    private static final String ITEMS = "items";
    
    private static final String folder =  Wizardry.MODID + "/modifier";
    
    /**
     * Unconstructable
     */
    private ModifierLoader() {}
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/modifier/} and any subfolders
     * 
     * @see #loadModifiers(InputStream, Function, Function)
     */
    public static void loadModifiers(IReloadableResourceManager resourceManager)
    {
        YamlLoader.loadYamls(resourceManager, folder,
                map -> ModifierLoader.compileModifier(map, ForgeRegistries.ITEMS::getValue))
                  .forEach(ComponentRegistry::addModifier);
    }
    
    /**
     * Creates a {@link Modifier} list from an input stream, using the given
     * supplier functions for both a {@link Pattern} and an {@link Item}
     * 
     * @param file            the input stream to read modifiers from
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return the List of {@code Modifier} objects compiled from the input yaml
     *         stream
     */
    public static List<Modifier> loadModifiers(InputStream file, Function<ResourceLocation, Item> itemSupplier)
    {
        return YamlLoader.loadYamls(file, 
                map -> ModifierLoader.compileModifier(map, itemSupplier));
    }

    /**
     * Helper method to compile the map from parsing a modifier yaml
     * 
     * @param yaml The parsed yaml
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return A {@link Modifier} constructed from the values in the yaml
     */
    @SuppressWarnings("unchecked")
    private static Modifier compileModifier(Map<String, Object> yaml, Function<ResourceLocation, Item> itemSupplier)
    {
        String name = (String) yaml.get(NAME);
        List<Item> items = ((List<String>) yaml.get(ITEMS)).stream().map(ResourceLocation::new).map(itemSupplier::apply).collect(Collectors.toList());
        return new Modifier(name, items);
    }
}
