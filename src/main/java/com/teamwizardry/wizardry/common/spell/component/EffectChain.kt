package com.teamwizardry.wizardry.common.spell.component

class EffectChain(effect: ModuleEffect?) : SpellChain(effect) {
    override fun toInstance(caster: Interactor): EffectInstance {
        return super.toInstance(caster) as EffectInstance
    }
}