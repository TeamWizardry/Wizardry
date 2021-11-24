package com.teamwizardry.wizardry

import com.teamwizardry.wizardry.common.init.*
import com.teamwizardry.wizardry.proxy.IProxy
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Wizardry : ModInitializer {
    override fun onInitialize() {
        INSTANCE = this
        ModTags.init()
        ModFluids.init()
        ModItems.init()
        ModBlocks.init()
        ModSounds.init()
        ModPatterns.init()
    }

    fun makeLogger(cls: Class<*>?): Logger {
        return LogManager.getLogger(cls)
    }

    companion object {
        const val MODID = "wizardry"
        val LOGGER = LogManager.getLogger(Wizardry::class.java)!!
        var PROXY: IProxy? = null
        var INSTANCE: Wizardry? = null
        fun getId(path: String?): Identifier {
            return Identifier(MODID, path)
        }
    }
}