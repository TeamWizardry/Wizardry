package com.teamwizardry.wizardry.capability.mana

import com.teamwizardry.librarianlib.scribe.Save
import com.teamwizardry.wizardry.configs.ServerConfigs
import net.minecraft.nbt.NbtCompound

class ManaCapability(@Save override var mana: Double = 0.0, @Save override var maxMana: Double = ServerConfigs.crudeHaloMaxMana): IManaCapability {

    override fun readFromNbt(tag: NbtCompound) {
    }

    override fun writeToNbt(tag: NbtCompound) {
    }
}