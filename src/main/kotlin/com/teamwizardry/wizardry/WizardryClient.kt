package com.teamwizardry.wizardry

import com.teamwizardry.wizardry.common.init.ModBlocks
import com.teamwizardry.wizardry.proxy.ClientProxy
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.function.Consumer

object WizardryClient : ClientModInitializer {
    override fun onInitializeClient() {
        PROXY = ClientProxy()
        ModBlocks.initClient()

        ModelLoadingRegistry.INSTANCE.registerModelProvider(::registerModels);
    }

    fun registerModels(rm: ResourceManager, consumer: Consumer<Identifier>) {
        consumer.accept(getID("block/mana_battery"))
        consumer.accept(getID("block/mana_crystal"))
        consumer.accept(getID("block/mana_crystal_ring"))
        consumer.accept(getID("block/mana_crystal_ring_outer"))
    }
}