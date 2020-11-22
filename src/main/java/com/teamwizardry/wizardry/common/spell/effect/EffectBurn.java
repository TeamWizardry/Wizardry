package com.teamwizardry.wizardry.common.spell.effect;

import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.Attributes.DURATION;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.ENTITY;

public class EffectBurn extends PatternEffect {

    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if (entity.getType() != ENTITY)
            return;

        entity.getEntity().setFire((int) instance.getAttributeValue(DURATION));
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        if (block.getType() != BLOCK)
            return;

        BlockPos pos = block.getBlockPos();
        BlockPos off = pos.offset(block.getDir().getOpposite());
        if (FlintAndSteelItem.canSetFire(world.getBlockState(pos), world, pos)) {
            BlockState posFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos);
            world.setBlockState(pos, posFire);
        } else if (FlintAndSteelItem.canSetFire(world.getBlockState(off), world, off)) {
            BlockState offFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, off);
            world.setBlockState(off, offFire);
        }
    }

    private static final Color[] colors = new Color[]{Color.RED, Color.ORANGE, Color.DARK_GRAY};

    @Override
    public Color[] getColors() {
        return colors;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {

        //for (int i = 0; i < 100; i++)
        //    Wizardry.PROXY.spawnParticle(
        //            new GlitterBox.GlitterBoxFactory()
        //                    .setOrigin(target.getPos()
        //                            .add(RandUtil.nextDouble(-0.15, 0.15),
        //                                    RandUtil.nextDouble(-0.15, 0.15),
        //                                    RandUtil.nextDouble(-0.15, 0.15)))
        //                    .setTarget(RandUtil.nextDouble(-0.5, 0.5),
        //                            RandUtil.nextDouble(-0.5, 0.5),
        //                            RandUtil.nextDouble(-0.5, 0.5))
        //                    .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
        //                    .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
        //                    .setInitialColor(getRandomColor())
        //                    .setGoalColor(getRandomColor())
        //                    .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
        //                    .setGoalSize(0)
        //                    .setInitialAlpha(RandUtil.nextFloat(0.5f, 1))
        //                    .createGlitterBox(RandUtil.nextInt(5, 25)));

    }
}