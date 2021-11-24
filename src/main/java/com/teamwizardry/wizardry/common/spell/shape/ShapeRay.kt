package com.teamwizardry.wizardry.common.spell.shape;

import static com.teamwizardry.wizardry.common.spell.component.Attributes.RANGE;

import java.awt.Color;

import com.teamwizardry.librarianlib.etcetera.Raycaster;
import com.teamwizardry.librarianlib.etcetera.Raycaster.BlockMode;
import com.teamwizardry.librarianlib.etcetera.Raycaster.FluidMode;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.init.ModSounds;
import com.teamwizardry.wizardry.common.spell.component.Instance;
import com.teamwizardry.wizardry.common.spell.component.Interactor;
import com.teamwizardry.wizardry.common.spell.component.PatternShape;
import com.teamwizardry.wizardry.common.utils.ColorUtils;
import com.teamwizardry.wizardry.common.utils.RandUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShapeRay extends PatternShape {
    private static final Raycaster ray = new Raycaster();
    
    @Override
    public void run(World world, Instance instance, Interactor target) {
        Vec3d start = target.getPos();
        Vec3d end = start.add(target.getLook().multiply(instance.getAttributeValue(RANGE)));
        Entity sourceEntity = target.getEntity();
        ray.cast(world,
                BlockMode.VISUAL,
                FluidMode.ANY,
                entity -> entity instanceof LivingEntity && !entity.equals(sourceEntity), // TODO - where'd the better equality check move to?
                start.x,
                start.y,
                start.z,
                end.x,
                end.y,
                end.z);

        Interactor newTarget = null;
        switch (ray.getHitType()) {
            case NONE:
            case BLOCK:
            case FLUID:
                Vec3d dir = end.subtract(start);
                Vec3d hit = new Vec3d(ray.getHitX(), ray.getHitY(), ray.getHitZ());
                newTarget = new Interactor(new BlockPos(hit), Direction.getFacing(dir.x, dir.y, dir.z));
                break;
            case ENTITY:
                newTarget = new Interactor((LivingEntity) ray.getEntity());
                break;
        }
        ray.reset();

        ModSounds.playSound(world, instance.getCaster(), target, ModSounds.SUBTLE_MAGIC_BOOK_GLINT);
        super.run(world, instance, newTarget);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void runClient(World world, Instance instance, Interactor target) {

        Color[] colors = ColorUtils.mergeColorSets(instance.getEffectColors());
        Vec3d v1 = instance.getCaster().getClientPos();
        Vec3d v2 = target.getPos();

        for (int i = 0; i < 30; i++) {
            double a = i / 30.0;
            Wizardry.PROXY.spawnParticle(
                    new GlitterBox.GlitterBoxFactory()
                            .setOrigin((v1.getX() * a) + (v2.getX() * (1 - a)) + RandUtil.nextDouble(-0.035, 0.035),
                                    (v1.getY() * a) + (v2.getY() * (1 - a)) + RandUtil.nextDouble(-0.035, 0.035),
                                    (v1.getZ() * a) + (v2.getZ() * (1 - a)) + RandUtil.nextDouble(-0.035, 0.035))
                            .setGravity(RandUtil.nextFloat(-0.001f, -0.003f))
                            .setInitialColor(colors[0])
                            .setGoalColor(colors[1])
                            .setInitialSize(RandUtil.nextFloat(0.05f, 0.2f))
                            .setGoalSize(0)
                            .createGlitterBox(RandUtil.nextInt(5, 10)));
        }
    }
}
