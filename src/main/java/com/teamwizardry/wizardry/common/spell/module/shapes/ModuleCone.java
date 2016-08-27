package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.*;

import javax.annotation.Nullable;
import java.util.List;

public class ModuleCone extends Module implements IContinuousCast {
    public ModuleCone(ItemStack stack) {
        super(stack);
        attributes.addAttribute(Attribute.DISTANCE);
        attributes.addAttribute(Attribute.SCATTER);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription() {
        return "Casts the spell on all entities within a frontal cone.";
    }

    @Override
    public String getDisplayName() {
        return "Cone";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
        compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0.1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        double radius = spell.getDouble(DISTANCE);
        double scatter = 360 / 2 * MathHelper.clamp_double(spell.getDouble(SCATTER), 0, 1);
        Vec3d look = caster.getLook(1);
        if (!(caster instanceof SpellEntity)) {
            BlockPos pos = caster.getPosition();
            AxisAlignedBB axis = new AxisAlignedBB(pos.subtract(new Vec3i(radius, 0, radius)), pos.add(new Vec3i(radius, 1, radius)));
            List<Entity> entities = caster.worldObj.getEntitiesInAABBexcluding(caster, axis, Predicates.and(new Predicate<Entity>() {
                public boolean apply(@Nullable Entity apply) {
                    return apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem);
                }
            }, EntitySelectors.NOT_SPECTATING));
            for (Entity entity : entities) {
                if (entity.getDistanceSqToEntity(caster) <= radius * radius) {
                    Vec3d leftVec = look.rotateYaw(-(float) scatter / 2).rotatePitch(-caster.rotationPitch);
                    Vec3d rightVec = look.rotateYaw((float) scatter / 2).rotatePitch(-caster.rotationPitch);
                    Vec3d posVec = entity.getPositionVector().subtract(caster.getPositionVector()).normalize();
                    if (leftVec.xCoord == -rightVec.xCoord && leftVec.zCoord == -rightVec.zCoord)
                        if (betweenVectors(posVec, leftVec, look) || betweenVectors(posVec, look, rightVec))
                            stack.castEffects(entity);
                    if (betweenVectors(posVec, leftVec, rightVec))
                        stack.castEffects(caster);
                }
            }
            return true;
        } else {
            BlockPos pos = caster.getPosition();
            for (int i = -(int) radius; i <= radius; i++) {
                for (int j = -(int) radius; j <= radius; j++) {
                    if (i * i + j * j <= radius * radius && !caster.worldObj.isAirBlock(pos.add(i, 0, j))) {
                        double xCoord = look.xCoord * i;
                        double zCoord = look.zCoord * j;
                        double lookSq = look.xCoord * look.xCoord + look.zCoord * look.zCoord;
                        double posSq = i * i + j * j;
                        double cos = (xCoord + zCoord) / Math.sqrt(lookSq * posSq);
                        double angle = Math.acos(Math.abs(cos));
                        if (angle <= scatter) {
                            SpellEntity entity = new SpellEntity(caster.worldObj, pos.getX() + i, pos.getY(), pos.getZ() + j);
                            stack.castEffects(entity);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean betweenVectors(Vec3d test, Vec3d left, Vec3d right) {
        double testX = test.xCoord;
        double testZ = test.zCoord;
        double leftX = left.xCoord;
        double leftZ = left.zCoord;
        double rightX = right.xCoord;
        double rightZ = right.zCoord;

        // Math taken from: http://www.blackpawn.com/texts/pointinpoly/
        // P = A + u * (C - A) + v * (B - A)
        // 0 <= (u, v) <= 1
        // entityPos = u * leftVec + v * rightVec
        // dotnm = vn * vm
        double dot00 = leftX * leftX + leftZ * leftZ;
        double dot01 = leftX * rightX + leftZ * rightZ;
        double dot02 = leftX * testX + leftZ * testZ;
        double dot11 = rightX * rightX + rightZ * rightZ;
        double dot12 = rightX * testX + rightZ * testZ;
        double inv = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * inv;
        double v = (dot00 * dot12 - dot01 * dot02) * inv;
        return u >= 0 && v >= 0 && u <= 1 && v <= 1;
    }
}
