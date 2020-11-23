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
import com.teamwizardry.wizardry.client.particle.KeyFramedGlitterBox;
import com.teamwizardry.wizardry.common.core.Sequence;
import com.teamwizardry.wizardry.common.core.SequenceEventLoop;
import com.teamwizardry.wizardry.common.init.ModSounds;
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
import java.util.List;
import java.util.*;

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

        final CompoundNBT pointsTag = new CompoundNBT();
        final List<Interactor> interactors = new ArrayList<>();

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
            interactors.add(interactor);
            CompoundNBT pointNBT = new CompoundNBT();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            pointsTag.put(UUID.randomUUID().toString(), pointNBT);
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
            interactors.add(interactor);
            CompoundNBT pointNBT = new CompoundNBT();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            pointsTag.put(UUID.randomUUID().toString(), pointNBT);
        }

        instance.getExtraData().putBoolean("exploded", false);
        sendRenderPacket(world, instance, target);
        ModSounds.playSound(world, instance.getCaster(), target, ModSounds.HIGH_PITCHED_SOLO_BLEEP, 0.5f, 2f);

        SequenceEventLoop.createSequence(new Sequence(world, 10)
                .event(0f, (seq) -> {
                    instance.getExtraData().put("points", pointsTag);
                    instance.getExtraData().putBoolean("exploded", true);
                    sendRenderPacket(world, instance, target);
                }).event(1f, (seq) -> {
                    Sequence sequence = new Sequence(world, interactors.size());
                    Collections.shuffle(interactors);
                    for (int i = 0, interactorsSize = interactors.size(); i < interactorsSize; i++) {
                        Interactor interactor = interactors.get(i);
                        if (interactor.getPos().squareDistanceTo(center) <= range * range) {
                            sequence.event((i / (float) interactorsSize),
                                    (seq1) -> super.run(world, instance, interactor));
                        }
                    }

                    SequenceEventLoop.createSequence(sequence);

                }));

    }

    @Override
    public boolean disableAutomaticRenderPacket() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {

        final CompoundNBT nbt = instance.getExtraData();

        double range = instance.getAttributeValue(RANGE);
        Color[] colors = ColorUtils.mergeColorSets(instance.getEffectColors());

        if (nbt.contains("exploded") && !nbt.getBoolean("exploded")) {
            for (int i = 0; i < 60; i++) {
                double a = i / 60.0;
                Vec2d dot = MathUtils.genCirclePerimeterDot((float) range,
                        (float) (360f * RandUtil.nextFloat(-10, 10) * a * Math.PI / 180.0f));
                Vec3d circleDotPos = target.getPos().add(dot.getX(), 0, dot.getY());
                Wizardry.PROXY.spawnKeyedParticle(
                        new KeyFramedGlitterBox(RandUtil.nextInt(40, 50))
                                .pos(target.getPos(), Easing.easeOutQuart)
                                .pos(circleDotPos)
                                .pos(circleDotPos)
                                .pos(circleDotPos)
                                .pos(circleDotPos)
                                .color(colors[0])
                                .color(colors[1])
                                .size(0, Easing.easeOutQuart)
                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
                                .size(0)
                                .alpha(1)
                                .alpha(1)
                );
            }
        }

        if (nbt.contains("points")) {
            final CompoundNBT points = nbt.getCompound("points");
            for (String pointKey : points.keySet()) {
                CompoundNBT pointTag = points.getCompound(pointKey);
                Vec3d point = new Vec3d(pointTag.getDouble("x"), pointTag.getDouble("y"), pointTag.getDouble("z"));
                double yDist = Math.abs(target.getPos().y - point.getY());
                for (int i = 0; i < 5; i++) {
                    Vec3d to = point.add(0, RandUtil.nextDouble(-yDist, yDist), 0);
                    Wizardry.PROXY.spawnKeyedParticle(
                            new KeyFramedGlitterBox(20)
                                    .pos(point)
                                    .pos(point, Easing.easeOutQuart)
                                    .pos(to)
                                    .color(colors[0])
                                    .color(colors[1])
                                    .size(0, Easing.easeOutQuart)
                                    .size(0.3, Easing.linear)
                                    .size(0.1, Easing.linear)
                                    .size(0)
                                    .alpha(1)
                                    .alpha(1)
                    );
                }
            }
        }
    }
}
