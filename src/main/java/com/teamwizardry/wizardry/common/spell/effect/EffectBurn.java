package com.teamwizardry.wizardry.common.spell.effect;

import static com.teamwizardry.wizardry.common.spell.component.Attributes.DURATION;
import static com.teamwizardry.wizardry.common.spell.component.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.common.spell.component.Interactor.InteractorType.ENTITY;

import java.awt.Color;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.init.ModSounds;
import com.teamwizardry.wizardry.common.spell.component.Instance;
import com.teamwizardry.wizardry.common.spell.component.Interactor;
import com.teamwizardry.wizardry.common.spell.component.PatternEffect;
import com.teamwizardry.wizardry.common.utils.RandUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

public class EffectBurn extends PatternEffect {

    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if (entity.getType() != ENTITY)
            return;

        entity.getEntity().setFireTicks((int) instance.getAttributeValue(DURATION));
        ModSounds.playSound(world, instance.getCaster(), entity, ModSounds.FIRE, 0.1f);
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        if (block.getType() != BLOCK)
            return;

//        BlockPos pos = block.getBlockPos();
//        BlockPos off = pos.offset(block.getDir().getOpposite());
//        if (AbstractFireBlock.canLightBlock(world, pos, block.getDir())) {
//            BlockState posFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos);
//            world.setBlockState(pos, posFire);
//        } else if (FlintAndSteelItem.canSetFire(world.getBlockState(off), world, off)) {
//            BlockState offFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, off);
//            world.setBlockState(off, offFire);
//        }
        ModSounds.playSound(world, instance.getCaster(), block, ModSounds.FIRE, 0.1f);
    }

    private static final Color[] colors = new Color[]{Color.RED, Color.ORANGE, Color.DARK_GRAY};

    @Override
    public Color[] getColors() {
        return colors;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void runClient(World world, Instance instance, Interactor target) {

        for (int i = 0; i < 100; i++)
            Wizardry.PROXY.spawnParticle(
                    new GlitterBox.GlitterBoxFactory()
                            .setOrigin(target.getPos()
                                    .add(RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15)))
                            .setTarget(RandUtil.nextDouble(-0.5, 0.5),
                                    RandUtil.nextDouble(-0.5, 0.5),
                                    RandUtil.nextDouble(-0.5, 0.5))
                            .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
                            .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
                            .setInitialColor(getRandomColor())
                            .setGoalColor(getRandomColor())
                            .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
                            .setGoalSize(0)
                            .setInitialAlpha(RandUtil.nextFloat(0.5f, 1))
                            .createGlitterBox(RandUtil.nextInt(5, 25)));

    }
}
