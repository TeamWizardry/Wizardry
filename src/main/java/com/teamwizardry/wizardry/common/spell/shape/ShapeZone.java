package com.teamwizardry.wizardry.common.spell.shape;

import static com.teamwizardry.wizardry.api.spell.Attributes.INTENSITY;
import static com.teamwizardry.wizardry.api.spell.Attributes.RANGE;

import java.util.LinkedList;
import java.util.List;

import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShapeZone extends PatternShape
{
    @Override
    public void run(World world, Instance instance, Interactor target)
    {
        Vec3d center = target.getPos();
        double range = instance.getAttributeValue(RANGE);
        double procFraction = MathHelper.clamp(instance.getAttributeValue(INTENSITY), 0, 1);
        AxisAlignedBB region = new AxisAlignedBB(new BlockPos(center)).grow(range-1);
        double rangeSq = range*range;
        
        // Run on entities
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, region, entity -> entity.getPositionVec().squareDistanceTo(center) <= rangeSq);
        double numEntityProcs = entities.size() * procFraction;
        while (entities.size() > numEntityProcs)
            entities.remove((int) (Math.random() * entities.size()));
        for (LivingEntity entity : entities)
            super.run(world, instance, new Interactor(entity));
        
        // Run on blocks
        List<BlockPos> blocks = new LinkedList<>();
        for (int x = (int) Math.floor(region.minX); x < Math.ceil(region.maxX); x++)
            for (int y = (int) Math.floor(region.minY); y < Math.ceil(region.maxY); y++)
                for (int z = (int) Math.floor(region.minZ); z < Math.ceil(region.maxZ); z++)
                    if (center.squareDistanceTo(x, y, z) <= rangeSq)
                        blocks.add(new BlockPos(x, y, z));
        double numBlockProcs = blocks.size() * procFraction;
        while (blocks.size() > numBlockProcs)
            blocks.remove((int) (Math.random() * blocks.size()));
        for (BlockPos pos : blocks)
        {
            Vec3d direction = new Vec3d(pos.getX()+0.5 - center.x, pos.getY()+0.5 - center.y, pos.getZ()+0.5 - center.z);
            super.run(world, instance, new Interactor(pos, Direction.getFacingFromVector(direction.x, direction.y, direction.z)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {
        super.runClient(world, instance, target);
    }
}
