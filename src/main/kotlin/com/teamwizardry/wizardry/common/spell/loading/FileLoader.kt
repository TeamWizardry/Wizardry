package com.teamwizardry.wizardry.common.spell.loading

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.StreamSupport

abstract class FileLoader<T> {
    protected abstract fun compileYaml(yaml: Map<String, Any>): T
    protected abstract fun compileJson(json: JsonObject): T

    private val yaml = Load(LoadSettings.builder().setLabel("Wizardry Yaml Loader").build())

    fun loadJson(file: InputStream): T {
        return compileJson(JsonParser().parse(InputStreamReader(file)).asJsonObject)
    }

    @Suppress("UNCHECKED_CAST")
    fun loadYaml(file: InputStream): List<T> {
        return StreamSupport.stream(yaml.loadAllFromInputStream(file).spliterator(), false)
            .map { compileYaml(it as Map<String, Object>) }
            .toList()
    }
}