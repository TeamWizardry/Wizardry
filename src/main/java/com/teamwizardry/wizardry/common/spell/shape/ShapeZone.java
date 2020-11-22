package com.teamwizardry.wizardry.common.spell.shape;

import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import com.teamwizardry.wizardry.api.utils.ColorUtils;
import com.teamwizardry.wizardry.api.utils.MathUtils;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.client.particle.KeyFramedGlitterBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.teamwizardry.wizardry.api.spell.Attributes.INTENSITY;
import static com.teamwizardry.wizardry.api.spell.Attributes.RANGE;

public class ShapeZone extends PatternShape {
    @Override
    public void run(World world, Instance instance, Interactor target) {
        Vec3d center = target.getPos();
        double range = instance.getAttributeValue(RANGE);
        double procFraction = MathHelper.clamp(instance.getAttributeValue(INTENSITY), 0, 1);
        AxisAlignedBB region = new AxisAlignedBB(new BlockPos(center)).grow(range - 1);
        double rangeSq = range * range;

        final CompoundNBT points = new CompoundNBT();

        // Run on entities
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                region,
                entity -> entity.getPositionVec().squareDistanceTo(center) <= rangeSq);
        double numEntityProcs = entities.size() * procFraction;
        while (entities.size() > numEntityProcs)
            entities.remove((int) (Math.random() * entities.size()));
        for (LivingEntity entity : entities) {

            Interactor interactor = new Interactor(entity);
            Vec3d point = interactor.getPos();
            CompoundNBT pointNBT = new CompoundNBT();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            points.put(UUID.randomUUID().toString(), pointNBT);

            super.run(world, instance, interactor);
        }

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
        for (BlockPos pos : blocks) {
            Vec3d direction = new Vec3d(pos.getX() + 0.5 - center.x,
                    pos.getY() + 0.5 - center.y,
                    pos.getZ() + 0.5 - center.z);

            Interactor interactor = new Interactor(pos,
                    Direction.getFacingFromVector(direction.x, direction.y, direction.z));
            Vec3d point = interactor.getPos();
            CompoundNBT pointNBT = new CompoundNBT();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            points.put(UUID.randomUUID().toString(), pointNBT);

            super.run(world, instance, interactor);
        }

        instance.getExtraData().put("points", points);
        sendRenderPacket(world, instance, target);
    }

    @Override
    public boolean disableAutomaticRenderPacket() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {

        double range = instance.getAttributeValue(RANGE);
        Color[] colors = ColorUtils.mergeColorSets(instance.getEffectColors());

        Vec3d v1 = instance.getCaster().getClientPos();
        Vec3d v2 = target.getPos();

        for (int i = 0; i < 60; i++) {
            double a = i / 60.0;
            Vec2d dot = MathUtils.genCirclePerimeterDot((float) range, (float) (360f * a * Math.PI / 180.0f));
            Vec3d circleDotPos = target.getPos().add(dot.getX(), 0, dot.getY());
            Wizardry.PROXY.spawnParticle(
                    new GlitterBox.GlitterBoxFactory()
                            .setOrigin(target.getPos())
                            .setTarget(circleDotPos)
                            .setIsPhysics(false)
                            //.setGravity(RandUtil.nextFloat(-0.001f, -0.003f))
                            .setInitialColor(colors[0])
                            .setGoalColor(colors[1])
                            .setInitialSize(RandUtil.nextFloat(0.05f, 0.2f))
                            .setGoalSize(0)
                            .createGlitterBox(RandUtil.nextInt(10, 20)));
        }

        final CompoundNBT nbt = instance.getExtraData();
        if (nbt.contains("points")) {
            final CompoundNBT points = nbt.getCompound("points");
            for (String pointKey : points.keySet()) {
                CompoundNBT pointTag = points.getCompound(pointKey);
                Vec3d point = new Vec3d(pointTag.getDouble("x"), pointTag.getDouble("y"), pointTag.getDouble("z"));
                for (int i = 0; i < 5; i++) {
                    Vec3d to = point.add(0, RandUtil.nextInt(-4, 4), 0);
                    Wizardry.PROXY.spawnKeyedParticle(
                            new KeyFramedGlitterBox(20)
                                    .pos(Easing.easeOutQuart, point)
                                    .pos(Easing.linear, to)
                                    .color(Easing.linear, colors[0])
                                    .color(Easing.linear, colors[1])
                                    .size(Easing.linear, 1)
                                    .size(Easing.linear, 1)
                                    .alpha(Easing.linear, 1)
                                    .alpha(Easing.linear, 1)
                    );
                }
            }
        }
    }
}