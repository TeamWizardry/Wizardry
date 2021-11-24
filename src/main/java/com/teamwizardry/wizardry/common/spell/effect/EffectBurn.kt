package com.teamwizardry.wizardry.common.spell.effect

import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.PatternEffect
import net.fabricmc.api.Environment
import java.awt.Color

class EffectBurn : PatternEffect() {
    fun affectEntity(world: World?, entity: Interactor, instance: Instance) {
        if (entity.getType() != InteractorType.ENTITY) return
        entity.getEntity().setFireTicks(instance.getAttributeValue(DURATION).toInt())
        playSound(world, instance.caster, entity, ModSounds.FIRE, 0.1f)
    }

    fun affectBlock(world: World?, block: Interactor, instance: Instance) {
        if (block.getType() != InteractorType.BLOCK) return

//        BlockPos pos = block.getBlockPos();
//        BlockPos off = pos.offset(block.getDir().getOpposite());
//        if (AbstractFireBlock.canLightBlock(world, pos, block.getDir())) {
//            BlockState posFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos);
//            world.setBlockState(pos, posFire);
//        } else if (FlintAndSteelItem.canSetFire(world.getBlockState(off), world, off)) {
//            BlockState offFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, off);
//            world.setBlockState(off, offFire);
//        }
        playSound(world, instance.caster, block, ModSounds.FIRE, 0.1f)
    }

    val colors: Array<Color>
        get() = Companion.colors

    @Environment(EnvType.CLIENT)
    override fun runClient(world: World?, instance: Instance?, target: Interactor) {
        for (i in 0..99) Wizardry.Companion.PROXY.spawnParticle(
            GlitterBoxFactory()
                .setOrigin(
                    target.getPos()
                        .add(
                            RandUtil.nextDouble(-0.15, 0.15),
                            RandUtil.nextDouble(-0.15, 0.15),
                            RandUtil.nextDouble(-0.15, 0.15)
                        )
                )
                .setTarget(
                    RandUtil.nextDouble(-0.5, 0.5),
                    RandUtil.nextDouble(-0.5, 0.5),
                    RandUtil.nextDouble(-0.5, 0.5)
                )
                .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
                .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
                .setInitialColor(getRandomColor())
                .setGoalColor(getRandomColor())
                .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
                .setGoalSize(0f)
                .setInitialAlpha(RandUtil.nextFloat(0.5f, 1f))
                .createGlitterBox(RandUtil.nextInt(5, 25))
        )
    }

    companion object {
        private val colors = arrayOf(Color.RED, Color.ORANGE, Color.DARK_GRAY)
    }
}