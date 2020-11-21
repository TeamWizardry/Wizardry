package com.teamwizardry.wizardry.common.spell.shape;

import com.teamwizardry.librarianlib.etcetera.Raycaster;
import com.teamwizardry.librarianlib.etcetera.Raycaster.BlockMode;
import com.teamwizardry.librarianlib.etcetera.Raycaster.FluidMode;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.teamwizardry.wizardry.api.spell.Attributes.RANGE;

public class ShapeRay extends PatternShape
{
    @Override
    public void run(World world, Instance instance, Interactor target)
    {
        Vec3d start = target.getPos();
        Vec3d end = start.add(target.getLook().scale(instance.getAttributeValue(RANGE)));
        Raycaster ray = new Raycaster();
        Entity sourceEntity = target.getEntity();
        ray.cast(world, BlockMode.VISUAL, FluidMode.ANY, entity -> entity instanceof LivingEntity && !entity.isEntityEqual(sourceEntity), start.x, start.y, start.z, end.x, end.y, end.z);
        
        Interactor newTarget = null;
        switch (ray.getHitType())
        {
            case NONE:
            case BLOCK:
            case FLUID:
                Vec3d dir = end.subtract(start);
                Vec3d hit = new Vec3d(ray.getHitX(), ray.getHitY(), ray.getHitZ());
                newTarget = new Interactor(new BlockPos(hit), Direction.getFacingFromVector(dir.x, dir.y, dir.z));
                break;
            case ENTITY:
                newTarget = new Interactor((LivingEntity) ray.getEntity());
                break;
        }

        super.run(world, instance, newTarget);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {
        super.runClient(world, instance, target);
    }
}
