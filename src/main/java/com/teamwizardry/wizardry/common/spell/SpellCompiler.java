package com.teamwizardry.wizardry.common.spell;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.wizardry.api.spell.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.BlockTarget;
import com.teamwizardry.wizardry.api.spell.EntityTarget;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.ItemStack;

public class SpellCompiler
{
    private final LinkedList<ModuleShape> shapeChain = new LinkedList<>();
    private final Map<ModuleShape, LinkedList<ModuleEffect>> effectChains = new HashMap<>();
    private final Map<ModuleShape, ModuleShape> shapeOverrides = new HashMap<>();
    private final Map<Module, String> moduleElements = new HashMap<>();
    private final Map<ISpellComponent, Map<Modifier, Integer>> componentModifiers = new HashMap<>();
    private final Map<Module, LinkedList<EntityTarget>> moduleEntityTargets = new HashMap<>();
    private final Map<Module, LinkedList<BlockTarget>> moduleBlockTargets = new HashMap<>();
    
    public ShapeChain compileSpell(List<ItemStack> items)
    {
        processItems(items);
        ShapeChain spell = compile();
        clear();
        return spell;
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
            else if (component instanceof Element)
                handleElement((Element) component, count);
            else if (component instanceof Modifier)
                handleModifier((Modifier) component, count);
            else if (component instanceof EntityTarget)
                handleEntityTarget((EntityTarget) component, count);
            else if (component instanceof BlockTarget)
                handleBlockTarget((BlockTarget) component, count);
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
        if (lastShape.isOverriddenBy(effect))
        {
            if (!shapeOverrides.containsKey(lastShape)) // Only one override is allowed - all overrides afterwards are ignored
                shapeOverrides.put(lastShape, lastShape.getOverride(effect));
        }
        else
        {
            List<ModuleEffect> effects = effectChains.computeIfAbsent(lastShape, m -> new LinkedList<>());
            for (int i = 0; i < count; i++)
                effects.add(effect);
        }
    }
    
    private void handleElement(Element element, int count)
    {
        if (shapeChain.isEmpty())
            return;
        Module lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // Elements shift the last Effect, otherwise shifting last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
        moduleElements.put(lastComponent, element.getName());
    }
    
    private void handleModifier(Modifier modifier, int count)
    {
        if (shapeChain.isEmpty())
            return;
        Module lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // Modifiers affecting last Effect - otherwise affecting last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
        for (int i = 0; i < count; i++)
            componentModifiers.computeIfAbsent(lastComponent, m -> new HashMap<>()).merge(modifier, count, Integer::sum);
    }
 
    private void handleEntityTarget(EntityTarget target, int count)
    {
        if (shapeChain.isEmpty())
            return;
        Module lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // If the last object was an Effect, affect that - otherwise last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
        List<EntityTarget> targets = moduleEntityTargets.computeIfAbsent(lastComponent, m -> new LinkedList<>());
        for (int i = 0; i < count; i++)
            targets.add(target);
    }
    
    private void handleBlockTarget(BlockTarget target, int count)
    {
        if (shapeChain.isEmpty())
            return;
        Module lastComponent = shapeChain.getLast();
        if (effectChains.containsKey(lastComponent)) // If the last object was an Effect, affect that - otherwise last Shape
            lastComponent = effectChains.get(lastComponent).getLast();
        
        List<BlockTarget> targets = moduleBlockTargets.computeIfAbsent(lastComponent, m -> new LinkedList<>());
        for (int i = 0; i < count; i++)
            targets.add(target);
    }
    
    private ShapeChain compile()
    {
        // TODO: Handle target merging
        mergeTargets();
        
        List<ShapeChain> shapes = new LinkedList<>();
        for (ModuleShape shape : shapeChain)
        {
            List<ModuleEffect> effectChain = effectChains.get(shape);
            List<EffectChain> effects = new LinkedList<>();
            for (ModuleEffect effect : effectChain)
            {
                Map<String, List<AttributeModifier>> modifiers = mergeModifiers(componentModifiers.getOrDefault(effect, Collections.<Modifier, Integer>emptyMap()));
                effects.add(new EffectChain(effect, modifiers));
            }
            
            Map<String, List<AttributeModifier>> modifiers = mergeModifiers(componentModifiers.getOrDefault(shape, Collections.<Modifier, Integer>emptyMap()));
            
            String element = moduleElements.getOrDefault(shape, "");
            
            if (shapeOverrides.containsKey(shape))
                shape = shapeOverrides.get(shape);
            
            if (!element.isEmpty())
                shape = (ModuleShape) shape.getElementalVariant(element);
            
            shapes.add(new ShapeChain(shape, modifiers).setEffects(effects.toArray(new EffectChain[effects.size()])));
        }
        return shapes.stream().reduce((parent, child) -> parent.setNext(child)).get();
    }
    
    private void mergeTargets()
    {
        // TODO: fill in
    }
    
    private Map<String, List<AttributeModifier>> mergeModifiers(Map<Modifier, Integer> modifiers)
    {
        Map<String, List<AttributeModifier>> mergedModifiers = new HashMap<>();
        for (Entry<Modifier, Integer> entry : modifiers.entrySet())
        {
            Modifier modifier = entry.getKey();
            int count = entry.getValue();
            for (String attribute : modifier.getAffectedAttributes())
            {
                List<AttributeModifier> modList = mergedModifiers.computeIfAbsent(attribute, a -> new LinkedList<AttributeModifier>());
                for (int i = 0; i < count; i++)
                    modList.addAll(modifier.getAttributeModifiers(attribute));
            };
        };
        return mergedModifiers;
    }
    
    private void clear()
    {
        this.effectChains.clear();
        this.componentModifiers.clear();
        this.moduleBlockTargets.clear();
        this.moduleEntityTargets.clear();
        this.shapeChain.clear();
        this.shapeOverrides.clear();
    }
    
    private SpellCompiler() {}
    
    public static SpellCompiler get()
    {
        return new SpellCompiler();
    }
}
