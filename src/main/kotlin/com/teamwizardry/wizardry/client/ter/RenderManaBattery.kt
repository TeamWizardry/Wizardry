package com.teamwizardry.wizardry.client.ter

import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.block.entity.manabattery.BlockManaBatteryEntity
import com.teamwizardry.wizardry.Wizardry.Companion.getID
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
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Quaternion

class RenderManaBattery(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BlockManaBatteryEntity> {

    private fun getModel(name: String): BakedModel {
        val bmm: BakedModelManager = MinecraftClient.getInstance().bakedModelManager
        val mm: Map<Identifier, BakedModel> = (bmm as AccessorModelManager).models
        val missing: BakedModel = bmm.missingModel
        val location: Identifier = Wizardry.getID(name)
        return mm[location] ?: missing
    }

    override fun render(entity: BlockManaBatteryEntity, tickDelta: Float, ms: MatrixStack, buffers: VertexConsumerProvider, light: Int, overlay: Int) {

        if (entity.world == null) return

        val buffer: VertexConsumer = buffers.getBuffer(RenderLayer.getTranslucentMovingBlock())
        val renderer = MinecraftClient.getInstance().blockRenderManager.modelRenderer
        val axis = Direction.UP.unitVector

        val time = entity.world!!.time + tickDelta

        val bobbing = kotlin.math.sin(time / 30.0) / 10.0

        val manaCrystal: BakedModel = getModel("block/mana_crystal")
        val innerRing: BakedModel = getModel("block/mana_crystal_ring")
        val outerRing: BakedModel = getModel("block/mana_crystal_ring_outer")

        ms.push()

        ms.translate(0.0, 1 + bobbing, 0.0)

        renderer.render(ms.peek(), buffer, entity.cachedState, manaCrystal, 1F, 1F, 1F, light, overlay)

        ms.translate(0.0, - bobbing, 0.0)

        ms.translate(0.5, 0.0, 0.5)
        ms.multiply(Quaternion(axis, time * 10, true))
        ms.translate(-0.5, 0.0, -0.5)

        renderer.render(ms.peek(), buffer, entity.cachedState, innerRing, 1f, 1f, 1f, light, overlay)

        ms.translate(0.5, 0.0, 0.5)
        ms.multiply(Quaternion(axis, -time * 10, true))
        ms.translate(-0.5, 0.0, -0.5)

        ms.translate(0.5, 0.0, 0.5)
        ms.multiply(Quaternion(axis, -2*time, true))
        ms.translate(-0.5, 0.0, -0.5)

        renderer.render(ms.peek(), buffer, entity.cachedState, outerRing, 1f, 1f, 1f, light, overlay)

        ms.pop()
    }
}
