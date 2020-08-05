package com.teamwizardry.wizardry.common.spell.loading;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.spell.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.Element;

import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles loading Elements from yaml resources. Relies heavily on a cohesive
 * structure:
 * 
 * <pre>
 * elementOne: modid:item
 * elementTwo: modid:item
 * elementThree: modid:item
 * ...
 * </pre>
 */
public class ElementLoader extends YamlLoader
{
    private static final String folder =  Wizardry.MODID + "/element";
    
    /**
     * Unconstructable
     */
    private ElementLoader() {}
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/element/} and any subfolders
     * 
     * @see #loadModules(InputStream, Function, Function)
     */
    public static void loadElements(IReloadableResourceManager resourceManager)
    {
        YamlLoader.loadYamls(resourceManager, folder,
                map -> ElementLoader.compileModule(map, ForgeRegistries.ITEMS::getValue))
                  .forEach(list -> list.forEach(ComponentRegistry::addElement));
    }
    
    /**
     * Creates an {@link Element} list list from an input stream, using the given
     * supplier function for an {@link Item}
     * 
     * @param file            the input stream to read modules from
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return the List of {@code List<Element>} objects compiled from the input yaml
     *         stream
     */
    public static List<List<Element>> loadModules(InputStream file, Function<ResourceLocation, Item> itemSupplier)
    {
        return YamlLoader.loadYamls(file,
                map -> ElementLoader.compileModule(map, itemSupplier));
    }

    /**
     * Helper method to compile the map from parsing a module yaml
     * 
     * @param yaml The parsed yaml
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return A {@code List<Element>} constructed from the values in the yaml
     */
    private static List<Element> compileModule(Map<String, Object> yaml, Function<ResourceLocation, Item> itemSupplier)
    {
        List<Element> elements = new LinkedList<>();
        yaml.forEach((name, item) -> elements.add(new Element(name, itemSupplier.apply(new ResourceLocation((String) item)))));
        return elements;
    }
}
