package com.teamwizardry.wizardry.common.spell.effect;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.spell.Attributes;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.item.HoeItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class EffectHeatSponge extends PatternEffect {
    // Time in ticks added to the effect per duration
    private static final int DURATION_MULTIPLIER = 20 * 5;
    private static final boolean EXINGUISH_MULTIPLE = true;

    private static final Color[] colors = new Color[]{Color.WHITE, Color.GRAY, Color.BLUE};

    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if(entity.getType() != Interactor.InteractorType.ENTITY) return;

        entity.getEntity().extinguish();

        int duration = (int)instance.getAttributeValue(Attributes.DURATION) * DURATION_MULTIPLIER;
        entity.getEntity().addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, duration, 0));
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        if (block.getType() != Interactor.InteractorType.BLOCK) return;

        BlockPos blockPos = block.getBlockPos();
        BlockPos[] positions = new BlockPos[]{blockPos, blockPos.up(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        boolean hasExtinguished = false;

        for(BlockPos targetPosition : positions) {
            BlockState state = world.getBlockState(targetPosition);

            if(state.getBlock() == Blocks.FIRE) {
                world.removeBlock(targetPosition, false);
                if(!EXINGUISH_MULTIPLE) return;  // this is intentional. Only one fire will be extinguished.
                else hasExtinguished = true;
            }
        }

        if(hasExtinguished) return;

        IFluidState fState = world.getFluidState(blockPos);

        if (fState.isSource() && fState.getFluid().isEquivalentTo(Fluids.LAVA)) {
            world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
        }
    }

    // Rendering
    @Override
    public Color[] getColors() {
        return colors;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
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
