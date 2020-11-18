package com.teamwizardry.wizardry.common.spell.shape;

import static com.teamwizardry.wizardry.api.spell.Attributes.RANGE;

import java.util.Map;

import com.teamwizardry.librarianlib.etcetera.Raycaster;
import com.teamwizardry.librarianlib.etcetera.Raycaster.BlockMode;
import com.teamwizardry.librarianlib.etcetera.Raycaster.FluidMode;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShapeRay extends PatternShape
{
    @Override
    public void run(World world, Interactor caster, Interactor source, Interactor target, Map<String, Double> attributeValues, double manaCost, double burnoutCost)
    {
        Vec3d start = source.getPos();
        Vec3d end = source.getPos().add(source.getLook().scale(attributeValues.get(RANGE)));
        Raycaster ray = new Raycaster();
        Entity sourceEntity = source.getEntity();
        ray.cast(world, BlockMode.VISUAL, FluidMode.ANY, entity -> entity instanceof LivingEntity && !entity.isEntityEqual(sourceEntity), start.x, start.y, start.z, end.x, end.y, end.z);
        Vec3d hit = new Vec3d(ray.getHitX(), ray.getHitY(), ray.getHitZ());
        switch (ray.getHitType())
        {
            case NONE:
            case BLOCK:
            case FLUID:
                affectBlock(world, new BlockPos(hit));
                break;
            case ENTITY:
                affectEntity(world, (LivingEntity) ray.getEntity());
                break;
        }
    }

    @Override
    public void affectEntity(World world, LivingEntity entity)
    {
        
    }

    @Override
    public void affectBlock(World world, BlockPos pos)
    {
        
    }

}
