package com.teamwizardry.wizardry.capability.mana

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent

interface IManaCapability : AutoSyncedComponent {
    var mana: Double
    var maxMana: Double
}