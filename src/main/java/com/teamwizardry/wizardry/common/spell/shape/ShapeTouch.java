package com.teamwizardry.wizardry.common.spell.shape;

import java.util.UUID;

import com.teamwizardry.wizardry.api.spell.PatternShape;
import com.teamwizardry.wizardry.api.spell.TargetType;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class ShapeTouch extends PatternShape
{
    @Override
    public void run(World world, CompoundNBT castData, TargetType targetType)
    {
        Entity sourceEntity = null;
        BlockPos sourcePos = null;
        Direction sourceDir = null;
        if (castData.contains(SOURCE, NBT.TAG_INT))
            sourceEntity = world.getEntityByID(castData.getInt(SOURCE));
        else if (castData.contains(CASTER, NBT.TAG_COMPOUND))
        {
            CompoundNBT sourceBlock = castData.getCompound(SOURCE);
            sourcePos = BlockPos.fromLong(sourceBlock.getLong(POS));
            sourceDir = Direction.byIndex(sourceBlock.getInt(DIR));
        }
        
        Vec3d castDir = null;
        Vec3d castPos = null;
        if (sourceEntity != null)
        {
            castDir = sourceEntity.getLookVec();
            castPos = sourceEntity.getEyePosition(1);
        }
        else if (sourcePos != null)
        {
            castDir = new Vec3d(sourceDir.getDirectionVec());
            Vec3d offset;
            switch (sourceDir)
            {
                case DOWN:
                    offset = new Vec3d(0.5, 0, 0.5);
                    break;
                case UP:
                    offset = new Vec3d(0.5, 1, 0.5);
                    break;
                case NORTH:
                    offset = new Vec3d(0.5, 0.5, 0);
                    break;
                case SOUTH:
                    offset = new Vec3d(0.5, 0.5, 1);
                    break;
                case WEST:
                    offset = new Vec3d(0, 0.5, 0.5);
                    break;
                case EAST:
                    offset = new Vec3d(1, 0.5, 0.5);
                    break;
                default:
                    offset = Vec3d.ZERO;
            }
            castPos = new Vec3d(sourcePos).add(offset);
        }
        
        UUID casterEntity;
        BlockPos casterPos;
        if (castData.hasUniqueId(CASTER))
            casterEntity = castData.getUniqueId(CASTER);
        else if (castData.contains(CASTER, NBT.TAG_COMPOUND))
        {
            CompoundNBT casterBlock = castData.getCompound(CASTER);
            casterPos = BlockPos.fromLong(casterBlock.getLong(POS));
        }
    }

    @Override
    public void affectEntity(Entity entity)
    {
        
    }

    @Override
    public void affectBlock(BlockPos pos)
    {
        
    }

}
