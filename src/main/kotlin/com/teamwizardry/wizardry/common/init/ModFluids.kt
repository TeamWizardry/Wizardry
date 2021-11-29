package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.common.block.fluid.mana.ManaFluid
import com.teamwizardry.wizardry.common.block.fluid.nacre.NacreFluid
import com.teamwizardry.wizardry.getID
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ModFluids {
    lateinit var STILL_MANA: FlowableFluid
    lateinit var FLOWING_MANA: FlowableFluid
    lateinit var STILL_NACRE: FlowableFluid
    lateinit var FLOWING_NACRE: FlowableFluid

    fun init() {
        STILL_MANA = Registry.register(Registry.FLUID, getID("mana"), ManaFluid.Still())
        FLOWING_MANA = Registry.register(Registry.FLUID, getID("flowing_mana"), ManaFluid.Flowing())
        ModItems.manaBucket = BucketItem(STILL_MANA, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ModItems.wizardry))

        STILL_NACRE = Registry.register(Registry.FLUID, getID("nacre"), NacreFluid.Still())
        FLOWING_NACRE = Registry.register(Registry.FLUID, getID("flowing_nacre"), NacreFluid.Flowing())
        ModItems.nacreBucket = BucketItem(STILL_NACRE, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ModItems.wizardry))
    }

    @Environment(EnvType.CLIENT)
    fun initClient() {
        setUpFluidRendering(STILL_MANA, FLOWING_MANA, getID("mana"))
        setUpFluidRendering(STILL_NACRE, FLOWING_NACRE, getID("nacre"))
    }

    @Environment(EnvType.CLIENT)
    private fun setUpFluidRendering(still: Fluid, flowing: Fluid, texture: Identifier) {
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), still, flowing)

        val stillSprite = Identifier(texture.namespace, "fluid/${texture.path}_still")
        val flowingSprite = Identifier(texture.namespace, "fluid/${texture.path}_flow")

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register {_, registry -> registry.register(stillSprite); registry.register(flowingSprite)}

        val fluid = Registry.FLUID.getId(still)
        val listener = Identifier(fluid.namespace, "${fluid.path}_reload_listener")

        var fluidSprites: Array<Sprite?> = arrayOf(null, null)

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(object : SimpleSynchronousResourceReloadListener {
            override fun getFabricId(): Identifier { return listener }

            override fun reload(manager: ResourceManager) {
                val atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                fluidSprites = arrayOf(atlas.apply(stillSprite), atlas.apply(flowingSprite))
            }
        })

        val renderHandler = FluidRenderHandler {_, _, _ -> fluidSprites}

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler)
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler)
    }
}