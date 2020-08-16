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
    private final Map<ISpellComponent, Map<Modifier, Integer>> componentModifiers = new HashMap<>();
    
    public ShapeChain compileSpell(List<ItemStack> items)
    {
        processItems(items);
        ShapeChain spell = compile();
        clear();
        return spell;
    }
    
    private void processItems(List<ItemStack> items)
    {
    }
    
    private ShapeChain compile()
    {
        // TODO: Handle target merging
        mergeTargets();
        
        return null;
    }
    
    private void mergeTargets()
    {
        // TODO: fill in
    }
    
    private void clear()
    {
        this.effectChains.clear();
        this.componentModifiers.clear();
        this.shapeChain.clear();
    }
    
    private SpellCompiler() {}
    
    public static SpellCompiler get()
    {
        return new SpellCompiler();
    }
}
