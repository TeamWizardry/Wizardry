package com.teamwizardry.wizardry.common.item

import com.teamwizardry.wizardry.common.utils.WNBT
import net.minecraft.client.MinecraftClient
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

interface INacreProduct : ItemColorProvider {
    fun colorableOnUpdate(stack: ItemStack, world: World) {
        if (!world.isClient) {
            val tag: NbtCompound = stack.getOrCreateNbt()
            if (!tag.contains(RAND)) tag.putFloat(RAND, world.getTime() / 140f % 140f)
            if (!tag.contains(PURITY)) {
                tag.putInt(PURITY, NACRE_PURITY_CONVERSION)
                tag.putFloat(PURITY_OVERRIDE, 1f)
            }
            if (!tag.contains(COMPLETE) || !tag.getBoolean(COMPLETE)) {
                tag.putBoolean(COMPLETE, true)
            }
        }
    }

    fun colorableOnEntityItemUpdate(entityItem: ItemEntity) {
        if (entityItem.world.isClient) return
        val stack: ItemStack = entityItem.getStack()
        val tag: NbtCompound = stack.getOrCreateNbt()
        if (!tag.contains(RAND)) tag.putFloat(RAND, entityItem.world.random.nextFloat())

//		BlockState state = entityItem.world.getBlockState(entityItem.getBlockPos());

        // TODO: ModFluids.NACRE
        /*
		if (state.getBlock() == ModFluids.NACRE.getActualBlock() && !WNBT.getBoolean(stack, COMPLETE, false)) {
			int purity = WNBT.getInt(stack, PURITY, 0);
			purity = Math.min(purity + 1, NACRE_PURITY_CONVERSION * 2);
			WNBT.setInt(stack, PURITY, purity);
		} else if (WNBT.getInt(stack, PURITY, 0) > 0)
			WNBT.setBoolean(stack, COMPLETE, true);
		 */
    }

    fun getQuality(stack: ItemStack): Float {
        val tag: NbtCompound = stack.getOrCreateNbt()
        val override: Float = if (tag.contains(PURITY_OVERRIDE)) tag.getFloat(PURITY_OVERRIDE) else 0
        if (override > 0) return override
        val timeConstant = NACRE_PURITY_CONVERSION.toFloat()
        val purity: Int = WNBT.getInt(stack, PURITY, NACRE_PURITY_CONVERSION)
        return if (purity > NACRE_PURITY_CONVERSION + 1) max(
            0f,
            2f - purity / timeConstant
        ) else if (purity < NACRE_PURITY_CONVERSION - 1) max(
            0f,
            purity / timeConstant
        ) else 1f
    }

    override fun getColor(stack: ItemStack, tintIndex: Int): Int {
        if (tintIndex != 0) return 0xFFFFFF
        val rand: Float = WNBT.getFloat(stack, RAND, -1)
        var hue = 0f
        if (MinecraftClient.getInstance().world != null) {
            hue = if (rand < 0) MinecraftClient.getInstance().world!!.time / 140f % 140f else rand
        }
        val pow = min(1f, max(0f, getQuality(stack)))
        val saturation = curveConst * (1 - Math.E.pow(-pow.toDouble()).toFloat())
        return Color.HSBtoRGB(hue, saturation, 1f)
    }

    interface INacreDecayProduct : INacreProduct {
        override fun getColor(stack: ItemStack, tintIndex: Int): Int {
            if (tintIndex != 0) return 0xFFFFFF
            val lastCast: Long = WNBT.getLong(stack, LAST_CAST, -1)
            val decayCooldown: Int = WNBT.getInt(stack, LAST_COOLDOWN, -1)
            var tick: Long = 0
            if (MinecraftClient.getInstance().world != null) {
                tick = MinecraftClient.getInstance().world!!.time
            }
            val timeSinceCooldown = tick - lastCast
            val decayStage = if (decayCooldown > 0) timeSinceCooldown.toFloat() / decayCooldown else 1f
            val rand: Float = WNBT.getFloat(stack, RAND, -1)
            val hue = if (rand < 0) tick / 140f % 140f else rand
            val pow = min(1f, max(0f, getQuality(stack)))
            val decaySaturation: Double =
                if (lastCast == -1L || decayCooldown <= 0 || decayStage >= 1f) 1f else if (decayStage < decayCurveDelimiter) Math.E.pow(
                    (-15 * decayStage).toDouble()
                ) else Math.E.pow((3 * decayStage - 3).toDouble())
            val saturation = curveConst * (1 - Math.E.pow(-pow.toDouble()).toFloat()) * decaySaturation.toFloat()
            return Color.HSBtoRGB(hue, saturation, 1f)
        }

        companion object {
            const val decayCurveDelimiter = 1 / 6.0
        }
    }

    companion object {
        const val COMPLETE = "complete"
        const val LAST_CAST = "last_cast"
        const val LAST_COOLDOWN = "last_cooldown"
        const val NACRE_PURITY_CONVERSION = 30 * 20
        const val PURITY = "purity"
        const val PURITY_OVERRIDE = "purity_override"
        const val RAND = "rand"
        const val curveConst = 0.75f / (1.0f - 1 / Math.E.toFloat())
    }
}