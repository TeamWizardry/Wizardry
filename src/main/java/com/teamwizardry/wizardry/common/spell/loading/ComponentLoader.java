//package com.teamwizardry.wizardry.common.spell.loading;
//
//import java.io.InputStream;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//import com.teamwizardry.wizardry.Wizardry;
//import com.teamwizardry.wizardry.common.spell.ComponentRegistry;
//import com.teamwizardry.wizardry.common.spell.BaseComponent;
//
//import net.minecraft.item.Item;
//import net.minecraft.resources.IReloadableResourceManager;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.registries.ForgeRegistries;
//
///**
// * Handles loading BaseComponents from yaml resources. Relies heavily on a cohesive
// * structure:
// * 
// * <pre>
// * elementOne: modid:item
// * elementTwo: modid:item
// * elementThree: modid:item
// * ...
// * </pre>
// */
//public class ComponentLoader extends YamlLoader
//{
//    private static final String folder = Wizardry.MODID + "/component";
//    
//    private static final String FORMS = "form";
//    private static final String ACTIONS = "action";
//    private static final String ELEMENTS = "element";
//    
//    /**
//     * Unconstructable
//     */
//    private ComponentLoader() {}
//    
//    /**
//     * Reads all .yaml files under {@code data/<domain>/wizardry/component/} and any subfolders
//     * 
//     * @see #loadModules(InputStream, Function, Function)
//     */
//    public static void loadComponents(IReloadableResourceManager resourceManager)
//    {
//        YamlLoader.loadYamls(resourceManager, folder,
//                map -> ComponentLoader.compileModule(map, ForgeRegistries.ITEMS::getValue))
//                  .forEach(list -> list.forEach(ComponentRegistry::addElement));
//    }
//    
//    /**
//     * Creates an {@link BaseComponent} list list from an input stream, using the given
//     * supplier function for an {@link Item}
//     * 
//     * @param file            the input stream to read modules from
//     * @param itemSupplier    the function used to convert a {@code modid:name}
//     *                        string into a {@code Item}
//     * @return the List of {@code List<BaseComponent>} objects compiled from the input yaml
//     *         stream
//     */
//    public static List<List<BaseComponent>> loadModules(InputStream file, Function<ResourceLocation, Item> itemSupplier)
//    {
//        return YamlLoader.loadYamls(file,
//                map -> ComponentLoader.compileModule(map, itemSupplier));
//    }
//
//    /**
//     * Helper method to compile the map from parsing a module yaml
//     * 
//     * @param yaml The parsed yaml
//     * @param itemSupplier    the function used to convert a {@code modid:name}
//     *                        string into a {@code Item}
//     * @return A {@code List<BaseComponent>} constructed from the values in the yaml
//     */
//    private static List<BaseComponent> compileModule(Map<String, Object> yaml, Function<ResourceLocation, Item> itemSupplier)
//    {
//        List<BaseComponent> forms = new LinkedList<>();
//        List<BaseComponent> actions = new LinkedList<>();
//        List<BaseComponent> elements = new LinkedList<>();
//        Wizardry.LOGGER.warn(yaml.get(FORMS).getClass());
//        Wizardry.LOGGER.warn(yaml.get(ACTIONS).getClass());
//        Wizardry.LOGGER.warn(yaml.get(ELEMENTS).getClass());
//        return elements;
//    }
//}
