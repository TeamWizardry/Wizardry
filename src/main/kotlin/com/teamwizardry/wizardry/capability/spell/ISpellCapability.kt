package com.teamwizardry.wizardry.capability.spell

import com.teamwizardry.wizardry.common.init.ModCapabilities
import com.teamwizardry.wizardry.common.spell.component.ShapeChain
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.item.ItemStack

interface ISpellCapability : AutoSyncedComponent {
    var spell: ShapeChain?
}