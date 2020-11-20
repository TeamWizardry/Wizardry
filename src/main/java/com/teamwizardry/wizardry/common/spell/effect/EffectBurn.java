package com.teamwizardry.wizardry.common.spell.effect;

import static com.teamwizardry.wizardry.api.spell.Attributes.DURATION;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.ENTITY;

import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EffectBurn extends PatternEffect
{
    @Override
    public void affectEntity(World world, Interactor entity, Instance instance)
    {
        if (entity.getType() != ENTITY)
            return;
        
        entity.getEntity().setFire((int)instance.getAttributeValue(DURATION));
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance)
    {
        if (block.getType() != BLOCK)
            return;
        
        BlockPos pos = block.getBlockPos();
        BlockPos off = pos.offset(block.getDir().getOpposite());
        if (FlintAndSteelItem.canSetFire(world.getBlockState(pos), world, pos))
        {
            BlockState posFire = ((FireBlock)Blocks.FIRE).getStateForPlacement(world, pos);
            world.setBlockState(pos, posFire);
        }
        else if (FlintAndSteelItem.canSetFire(world.getBlockState(off), world, off))
        {
            BlockState offFire = ((FireBlock)Blocks.FIRE).getStateForPlacement(world, off);
            world.setBlockState(off, offFire);
        }
    }
}
