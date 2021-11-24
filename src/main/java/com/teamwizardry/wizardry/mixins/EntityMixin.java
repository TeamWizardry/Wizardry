//package com.teamwizardry.wizardry.mixins;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import com.teamwizardry.wizardry.common.init.ModTags;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityType;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.tag.Tag;
//import net.minecraft.util.math.BlockPos.Mutable;
//import net.minecraft.util.math.Box;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//
//@Mixin(Entity.class)
//public abstract class EntityMixin extends Entity {
//
//    protected EntityMixin(EntityType<?> type, World world) {
//        super(type, world);
//    }
//
//    @Shadow
//    public float fallDistance;
//
//    @Shadow
//    abstract Entity getRidingEntity();
//
//    @Shadow
//    public void extinguish() {
//    }
//
////    @Shadow
////    public abstract AxisAlignedBB getBoundingBox();
//
//    @Shadow
//    public World world;
//
//    @Shadow
//    public abstract boolean isPushedByWater();
//
//    @Shadow
//    public abstract void setMotion(Vec3d motionIn);
//
//    @Shadow
//    public abstract Vec3d getMotion();
//
//    @Shadow
//    protected double submergedHeight;
//
//    @Inject(method = "updateAquatics", at = @At(value = "RETURN"))
//    public void onUpdateAquatics(CallbackInfo ci) {
//        handleManaMovement(ModTags.MANA);
//    }
//
//    public void handleManaMovement(Tag<Fluid> fluidTag) {
//        Box box = this.getBoundingBox().contract(0.001D);
//        int bbMinX = MathHelper.floor(box.minX);
//        int bbMaxX = MathHelper.ceil(box.maxX);
//        int bbMinY = MathHelper.floor(box.minY);
//        int bbMaxY = MathHelper.ceil(box.maxY);
//        int bbMinZ = MathHelper.floor(box.minZ);
//        int bbMaxZ = MathHelper.ceil(box.maxZ);
//        if (this.world.isRegionLoaded(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ)) {
//            if (this.isPushedByWater()) {
//                Mutable.stream(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ)
//                       .filter(pos -> this.world.getFluidState(pos).isIn(fluidTag))
//                       .findFirst().ifPresent(pos -> this.setMotion(this.getMotion().add(0, 0.075, 0)));
//            }
//        }
//    }
//}