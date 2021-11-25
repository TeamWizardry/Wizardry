package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.librarianlib.scribe.Save
import net.minecraft.nbt.NbtCompound
import java.util.*
import java.util.function.Consumer

class ShapeChain(shape: ModuleShape?) : SpellChain(shape) {
    @Save
    private var next: ShapeChain? = null

    @Save
    private val effects: MutableList<EffectChain>
    fun setNext(nextShape: ShapeChain?): ShapeChain {
        next = nextShape
        return this
    }

    fun addEffect(chain: EffectChain): ShapeChain {
        effects.add(chain)
        return this
    }

    override fun toInstance(caster: Interactor): ShapeInstance {
        val instance: ShapeInstance = super.toInstance(caster) as ShapeInstance
        if (next != null) instance.setNext(next!!.toInstance(caster))
        effects.stream().map {effect: EffectChain -> effect.toInstance(caster)}.forEach(instance::addEffect)
        return instance
    }

    override fun serializeNBT(): NbtCompound {
        val nbt: NbtCompound = super.serializeNBT()
        if (next != null) nbt.put(NEXT, next!!.serializeNBT())
        val effects = NbtCompound()
        for (i in 0 until effects.size) effects.put(i.toString(), this.effects[i].serializeNBT())
        nbt.put(EFFECTS, effects)
        return nbt
    }

    override fun deserializeNBT(nbt: NbtCompound) {
        super.deserializeNBT(nbt)
        if (nbt.contains(NEXT)) {
            next = ShapeChain(null)
            next!!.deserializeNBT(nbt.getCompound(NEXT))
        }
        val effects: NbtCompound = nbt.getCompound(EFFECTS)
        effects.keys.forEach(Consumer {index: String? ->
            val effect = EffectChain(null)
            effect.deserializeNBT(effects.getCompound(index))
            this.effects.add(effect)
        })
    }

    init {
        effects = LinkedList<EffectChain>()
    }
}