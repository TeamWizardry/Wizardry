package com.teamwizardry.wizardry.common.spell.loading

import com.google.gson.JsonObject
import com.teamwizardry.wizardry.common.spell.component.Modifier
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ModifierLoader: FileLoader<Modifier>() {
    private const val NAME = "name"
    private const val ITEMS = "items"

    override fun compileJson(json: JsonObject): Modifier {
        val name = json[NAME].asString
        val items = json[ITEMS].asJsonArray.map{it.asString}.map(::Identifier).map(Registry.ITEM::get)
        return Modifier(name, items)
    }

    @Suppress("UNCHECKED_CAST")
    override fun compileYaml(yaml: Map<String, Any>): Modifier {
        val name = yaml[NAME] as String
        val items = (yaml[ITEMS] as List<String>).map{Identifier(it)}.map{Registry.ITEM[it]}.toList()
        return Modifier(name, items)
    }
}