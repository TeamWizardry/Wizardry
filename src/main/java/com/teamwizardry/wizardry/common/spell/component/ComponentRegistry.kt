package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.wizardry.configs.ServerConfigs
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.Logger

object ComponentRegistry {
    private val LOGGER: Logger = Wizardry.Companion.INSTANCE.makeLogger(ComponentRegistry::class.java)
    private val modules: MutableMap<String?, Module?> = HashMap()
    private val modifiers: MutableMap<String?, Modifier> = HashMap()
    private val spellComponents: MutableMap<List<Item?>?, ISpellComponent?> = HashMap()
    private var entityTarget: TargetComponent? = null
        private set
    private var blockTarget: TargetComponent? = null
        private set

    fun addModule(module: Module?) {
        tryRegister(module, modules)
    }

    fun addModifier(modifier: Modifier) {
        tryRegister(modifier, modifiers)
    }

    fun getComponentForItems(items: List<Item?>?): ISpellComponent? {
        for (spells in spellComponents.keys) if (listStartsWith(items, spells)) return spellComponents[spells]
        return null
    }

    private fun listStartsWith(list: List<Item?>?, other: List<Item?>?): Boolean {
        if (other!!.size > list!!.size) return false
        val listIter = list.listIterator()
        val otherIter = other.listIterator()
        while (listIter.hasNext() && otherIter.hasNext()) if (listIter.next() != otherIter.next()) return false
        return true
    }

    private fun <Component : ISpellComponent?> tryRegister(
        component: Component,
        map: MutableMap<String?, in Component>
    ): Boolean {
        val items = component.getItems()
        for (keys in spellComponents.keys) {
            if (listStartsWith(keys, items)) {
                LOGGER.warn(
                    "Spell component registration failed for {} {}, recipe hidden by {}",
                    component.javaClass.getSimpleName(), component.getName(), spellComponents[keys].getName()
                )
                return false
            }
        }
        map[component.getName()] = component
        spellComponents[component.getItems()] = component
        return true
    }

    fun loadTargets() {
        if (entityTarget != null) spellComponents.remove(entityTarget.getItems())
        if (blockTarget != null) spellComponents.remove(blockTarget.getItems())
        entityTarget = TargetComponent("entityTarget", Registry.ITEM[Identifier(ServerConfigs.entityTargetItem)])
        blockTarget = TargetComponent("blockTarget", Registry.ITEM[Identifier(ServerConfigs.blockTargetItem)])
        spellComponents[entityTarget.getItems()] = entityTarget
        spellComponents[blockTarget.getItems()] = blockTarget
    }

    fun getModules(): Map<String?, Module?> {
        return modules
    }
}