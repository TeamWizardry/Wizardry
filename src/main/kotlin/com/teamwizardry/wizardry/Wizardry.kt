package com.teamwizardry.wizardry

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.glitter.ParticleSystemManager
import com.teamwizardry.wizardry.client.particle.ModParticles
import com.teamwizardry.wizardry.common.init.*
import com.teamwizardry.wizardry.proxy.ClientProxy
import com.teamwizardry.wizardry.proxy.IProxy
import com.teamwizardry.wizardry.proxy.ServerProxy
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import java.util.function.Consumer

class Wizardry {
    companion object {
        const val MODID = "wizardry"
        val logManager = ModLogManager(MODID, "Wizardry")

        var PROXY: IProxy = ServerProxy()
            private set

        fun getID(path: String): Identifier { return Identifier(MODID, path) }
    }

    object CommonInitializer: ModInitializer {
        override fun onInitialize() {
            ModTags.init()
            ModFluids.init()
            ModItems.init()
            ModBlocks.init()
            ModSounds.init()
            ModPatterns.init()

            ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ModPatterns.ModuleReloadListener)
        }
    }

    object ClientInitializer: ClientModInitializer {
        override fun onInitializeClient() {
            PROXY = ClientProxy()
            ModFluids.initClient()
            ModBlocks.initClient()
            ModItems.initClient()

            ParticleSystemManager.add(ModParticles.physicsGlitter)
            ModelLoadingRegistry.INSTANCE.registerModelProvider(::registerModels)
        }

        private fun registerModels(rm: ResourceManager, consumer: Consumer<Identifier>) {
            consumer.accept(getID("block/mana_battery"))
            consumer.accept(getID("block/mana_crystal"))
            consumer.accept(getID("block/mana_crystal_ring"))
            consumer.accept(getID("block/mana_crystal_ring_outer"))
        }
    }
}