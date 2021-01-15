package com.teamwizardry.wizardry.common.spell.component;

import java.util.LinkedList;
import java.util.List;

import com.teamwizardry.librarianlib.prism.Save;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.ShapeInstance;

public class ShapeChain extends SpellChain
{
    @Save private ShapeChain next;
    @Save private List<EffectChain> effects;
    
    public ShapeChain(ModuleShape shape)
    {
        super(shape);
        effects = new LinkedList<>();
    }
    
    public ShapeChain setNext(ShapeChain nextShape) { this.next = nextShape; return this; }
    public ShapeChain addEffect(EffectChain chain) { this.effects.add(chain); return this; }
    
    @Override
    public ShapeInstance toInstance(Interactor caster)
    {
        ShapeInstance instance = (ShapeInstance) super.toInstance(caster);
        if (next != null)
            instance.setNext(next.toInstance(caster));
        effects.stream().map(effect -> effect.toInstance(caster)).forEach(instance::addEffect);
        return instance;
    }

//    @Override
//    public CompoundNBT serializeNBT()
//    {
//        CompoundNBT nbt = super.serializeNBT();
//        if (next != null)
//            nbt.put(NEXT, next.serializeNBT());
//        
//        CompoundNBT effects = new CompoundNBT();
//        for (int i = 0; i < effects.size(); i++)
//            effects.put(Integer.toString(i), this.effects.get(i).serializeNBT());
//        nbt.put(EFFECTS, effects);
//        
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundNBT nbt)
//    {
//        super.deserializeNBT(nbt);
//        if (nbt.contains(NEXT))
//        {
//            this.next = new ShapeChain(null);
//            this.next.deserializeNBT(nbt.getCompound(NEXT));
//        }
//        
//        CompoundNBT effects = nbt.getCompound(EFFECTS);
//        effects.keySet().forEach(index -> {
//            EffectChain effect = new EffectChain(null);
//            effect.deserializeNBT(effects.getCompound(index));
//            this.effects.add(effect);
//        });
//    }
}
