package com.teamwizardry.wizardry.common.spell;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.Modifier;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles loading Modules from yaml resources. Relies heavily on a cohesive
 * structure:
 * 
 * <pre>
 * name: modifierName
 * item: modid:item
 * attributes:
 *   mana:
 *     add: double (default 0)
 *     baseMultiply: double (default 0)
 *     multiply: double (default 1)
 *   burnout:
 *      ... (repeat for all relevant attributes)
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
    private static final String ITEM = "item";
    private static final String ATTRIBUTES = "attributes";
    private static final String ADDITION = "add";
    private static final String MULTIPLY_BASE = "baseMultiply";
    private static final String MULTIPLY_TOTAL = "multiply";
    
    private static final String folder =  Wizardry.MODID + "/modifier";
    
    /**
     * Unconstructable
     */
    private ModifierLoader() {}
    
    /**
     * Reads all .yaml files under {@code data/<domain>/wizardry/module/} and any subfolders
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
     * @param file            the input stream to read modules from
     * @param patternSupplier the function used to convert a {@code modid:name}
     *                        string into a {@code Pattern}
     * @param itemSupplier    the function used to convert a {@code modid:name}
     *                        string into a {@code Item}
     * @return the List of {@code Module} objects compiled from the input yaml
     *         stream
     */
    public static List<Modifier> loadModifiers(InputStream file, Function<ResourceLocation, Item> itemSupplier)
    {
        return YamlLoader.loadYamls(file, 
                map -> ModifierLoader.compileModifier(map, itemSupplier));
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
    private static Modifier compileModifier(Map<String, Object> yaml, Function<ResourceLocation, Item> itemSupplier)
    {
        String name = (String) yaml.get(NAME);
        Item item = itemSupplier.apply(new ResourceLocation((String) yaml.get(ITEM)));
        Map<String, Map<String, Double>> attributeMap = (Map<String, Map<String, Double>>) yaml.get(ATTRIBUTES);
        Map<String, List<AttributeModifier>> attributeModifiers = new HashMap<>();
        attributeMap.entrySet().forEach(attribute -> {
            List<AttributeModifier> modifierList = attributeModifiers.computeIfAbsent(attribute.getKey(), key -> new LinkedList<>());
            modifierList.add(new AttributeModifier(Operation.ADDITION,
                    attribute.getValue().getOrDefault(ADDITION, 0.)));
            modifierList.add(new AttributeModifier(Operation.MULTIPLY_BASE,
                    attribute.getValue().getOrDefault(MULTIPLY_BASE, 0.)));
            modifierList.add(new AttributeModifier(Operation.MULTIPLY_TOTAL,
                    attribute.getValue().getOrDefault(MULTIPLY_TOTAL, 0.)));
        });
        
        return new Modifier(name, item, attributeModifiers);
    }
}
