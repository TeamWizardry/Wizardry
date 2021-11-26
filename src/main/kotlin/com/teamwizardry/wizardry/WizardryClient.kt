package com.teamwizardry.wizardry

import com.teamwizardry.wizardry.common.init.ModBlocks
import net.fabricmc.api.ClientModInitializer

object WizardryClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlocks.initClient()
    }
}