package com.teamwizardry.wizardry

import com.teamwizardry.wizardry.common.init.*
import com.teamwizardry.wizardry.proxy.IProxy
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

const val MODID = "wizardry"
fun getId(path: String): Identifier { return Identifier(MODID, path) }

fun makeLogger(cls: Class<*>): Logger { return LogManager.getLogger(cls) }

val LOGGER = makeLogger(Wizardry::class.java)
lateinit var PROXY: IProxy

object Wizardry : ModInitializer {
    override fun onInitialize() {
        ModTags.init()
        ModFluids.init()
        ModItems.init()
        ModBlocks.init()
        ModSounds.init()
        ModPatterns.init()
    }
}