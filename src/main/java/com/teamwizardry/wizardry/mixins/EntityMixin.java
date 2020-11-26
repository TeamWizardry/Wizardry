package com.teamwizardry.wizardry.mixins;

import com.teamwizardry.wizardry.common.lib.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Shadow
    public float fallDistance;

    @Shadow
    abstract Entity getRidingEntity();

    @Shadow
    public void extinguish() {
    }

    @Shadow
    public abstract AxisAlignedBB getBoundingBox();

    @Shadow
    public World world;

    @Shadow
    public abstract boolean isPushedByWater();

    @Shadow
    public abstract void setMotion(Vec3d motionIn);

    @Shadow
    public abstract Vec3d getMotion();

    @Shadow
    protected double submergedHeight;

    @Inject(method = "updateAquatics", at = @At(value = "RETURN"))
    public void onUpdateAquatics(CallbackInfo ci) {
        handleManaMovement(ModTags.Fluids.MANA);
    }

    public void handleManaMovement(Tag<Fluid> fluidTag) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
        int bbMinX = MathHelper.floor(axisalignedbb.minX);
        int bbMaxX = MathHelper.ceil(axisalignedbb.maxX);
        int bbMinY = MathHelper.floor(axisalignedbb.minY);
        int bbMaxY = MathHelper.ceil(axisalignedbb.maxY);
        int bbMinZ = MathHelper.floor(axisalignedbb.minZ);
        int bbMaxZ = MathHelper.ceil(axisalignedbb.maxZ);
        if (this.world.isAreaLoaded(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ)) {
            if (this.isPushedByWater()) {
                try (BlockPos.PooledMutable pos = BlockPos.PooledMutable.retain()) {
                    primary:
                    for (int x = bbMinX; x < bbMaxX; ++x) {
                        for (int y = bbMinY; y < bbMaxY; ++y) {
                            for (int z = bbMinZ; z < bbMaxZ; ++z) {
                                pos.setPos(x, y, z);
                                IFluidState fluidState = this.world.getFluidState(pos);
                                if (fluidState.isTagged(fluidTag)) {
                                    this.setMotion(this.getMotion().add(0, 0.1, 0));
                                    break primary;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}