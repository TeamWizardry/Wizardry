package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.ItemStack;

public class SpellCompiler
{
    private final LinkedList<ModuleShape> shapeChain = new LinkedList<>();
    private final Map<ModuleShape, LinkedList<ModuleEffect>> effectChains = new HashMap<>();
    private final Map<ModuleShape, ModuleEffect> shapeOverrides = new HashMap<>();
    private final Map<ISpellComponent, Map<Modifier, Integer>> effectModifiers = new HashMap<>();
    private final Map<Module, LinkedList<?>> moduleEvents = new HashMap<>();
    
    public void compileSpell(List<ItemStack> items)
    {
        processItems(items);
        compile();
    }
    
    private void processItems(List<ItemStack> items)
    {
        for (ItemStack stack : items)
        {
            ISpellComponent component = ComponentRegistry.getComponentForItem(stack.getItem());
            int count = stack.getCount();
            if (component instanceof ModuleShape)
                handleShape((ModuleShape) component, count);
            else if (component instanceof ModuleEffect)
                handleEffect((ModuleEffect) component, count);
            else if (component instanceof Modifier)
                handleModifier((Modifier) component, count);
//            else if (component instanceof Event)
//                handleModifier((Event) component, count);
        }
    }
    
    private void handleShape(ModuleShape shape, int count)
    {
        for (int i = 0; i < count; i++)
            shapeChain.add(shape);
    }
    
    private void handleEffect(ModuleEffect effect, int count)
    {
        if (shapeChain.isEmpty())
            return;
        ModuleShape lastShape = shapeChain.getLast();
        if (effect.getPattern().overrides(lastShape.getPattern()))
        {
            if (!shapeOverrides.containsKey(lastShape)) // Only one override is allowed - all overrides afterwards are ignored
            {
                effectChains.computeIfAbsent(lastShape, m -> new LinkedList<>()).add(effect);
                shapeOverrides.put(lastShape, effect);
            }
        }
        else
        {
            List<ModuleEffect> effects = effectChains.computeIfAbsent(lastShape, m -> new LinkedList<>());
            for (int i = 0; i < count; i++)
                effects.add(effect);
        }
    }
    
    private void handleModifier(Modifier modifier, int count)
    {
        if (shapeChain.isEmpty())
            return;
        ISpellComponent lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // Modifiers affecting last Effect - otherwise affecting last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
        for (int i = 0; i < count; i++)
            effectModifiers.computeIfAbsent(lastComponent, m -> new HashMap<>()).merge(modifier, count, Integer::sum);
    }
 
    private void handleEvent(/*Event event, */int count)
    {
        if (shapeChain.isEmpty())
            return;
        ISpellComponent lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // If the last object was an Effect, affect that - otherwise last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
//        List<?> events = moduleEvents.computeIfAbsent(lastComponent, m -> new LinkedList<>());
//        for (int i = 0; i < count; i++)
//            events.add(event);
            
    }
    
    private void compile()
    {
        /*
         * TODO:
         */
    }
    
    private SpellCompiler() {}
    
    public static SpellCompiler get()
    {
        return new SpellCompiler();
    }
}
