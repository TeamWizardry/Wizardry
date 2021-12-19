package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.capability.mana.ManaCapability
import com.teamwizardry.wizardry.capability.spell.SpellCapability
import com.teamwizardry.wizardry.common.block.IManaNode
import com.teamwizardry.wizardry.common.block.entity.craftingplate.BlockCraftingPlateEntity
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer
import net.fabricmc.fabric.api.tag.TagFactory
import net.minecraft.fluid.Fluid
import net.minecraft.tag.Tag

object ModCapabilities : EntityComponentInitializer, BlockComponentInitializer, ItemComponentInitializer {
    val MANA: ComponentKey<ManaCapability> = ComponentRegistryV3.INSTANCE.getOrCreate(Wizardry.getID("mana"), ManaCapability::class.java)
    val SPELL: ComponentKey<SpellCapability> = ComponentRegistryV3.INSTANCE.getOrCreate(Wizardry.getID("spell"), SpellCapability::class.java)

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(MANA, { ManaCapability() }, RespawnCopyStrategy.ALWAYS_COPY)
    }

    override fun registerBlockComponentFactories(registry: BlockComponentFactoryRegistry) {
        registry.registerFor(BlockManaBatteryEntity::class.java, MANA) {ManaCapability()}
        registry.registerFor(BlockCraftingPlateEntity::class.java, MANA) {ManaCapability()}
    }

    override fun registerItemComponentFactories(registry: ItemComponentFactoryRegistry) {
        registry.register(ModItems.pearl, SPELL) {SpellCapability(it)}
        registry.register(ModItems.staff, SPELL) {SpellCapability(it)}
    }
}