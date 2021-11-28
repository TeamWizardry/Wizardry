package com.teamwizardry.wizardry.client.ter

import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity
import com.teamwizardry.wizardry.getID
import com.teamwizardry.wizardry.mixins.AccessorModelManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedModelManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier


class RenderManaBattery(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BlockManaBatteryEntity> {

    private fun getModel(name: String): BakedModel {
        val bmm: BakedModelManager = MinecraftClient.getInstance().bakedModelManager
        val mm: Map<Identifier, BakedModel> = (bmm as AccessorModelManager).models
        val missing: BakedModel = bmm.missingModel
        val location: Identifier = getID(name)
        return mm[location] ?: missing
    }

    override fun render(
        entity: BlockManaBatteryEntity,
        tickDelta: Float,
        ms: MatrixStack,
        buffers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        ms.push()

        ms.translate(0.0, 0.5, 0.0)

        val buffer: VertexConsumer = buffers.getBuffer(RenderLayer.getTranslucentNoCrumbling())
        val bakedModel: BakedModel = getModel("block/mana_crystal")
        MinecraftClient.getInstance().blockRenderManager.modelRenderer.render(
            ms.peek(),
            buffer,
            entity.cachedState,
            bakedModel,
            0.3F,
            0.3F,
            0.3F,
            light,
            overlay
        )
        ms.translate(0.0, -0.5, 0.0)

        ms.pop()
    }
}
