package com.teamwizardry.wizardry.common.spell.component;

import static com.teamwizardry.wizardry.common.spell.component.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.common.spell.component.Interactor.InteractorType.ENTITY;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.wizardry.common.block.IManaNode;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Interactor {
    public enum InteractorType {
        ENTITY,
        BLOCK
    }

    private final InteractorType type;

    private final LivingEntity entity;
    private final BlockPos block;
    private final Direction dir;

    /**
     * Create an Interactor attached to a given {@link BlockPos}.
     * Spells cast on this block should take the {@link Direction}
     * into account, but this is by no means required.
     */
    public Interactor(BlockPos pos, Direction dir) {
        this.type = BLOCK;
        this.block = pos;
        this.dir = dir;
        this.entity = null;
    }

    /**
     * Create an Interactor attached to a given {@link LivingEntity}.
     * Spells cast on this entity are not guaranteed to have consistent
     * locations or directions, take this into account when dealing with
     * non-instantaneous effects.
     */
    public Interactor(LivingEntity entity)
    {
        this.type = ENTITY;
        this.entity = entity;
        this.block = null;
        this.dir = null;
    }

    /**
     * Gives the location the Interactor is currently seeing.
     * Returns the current eye position of the linked {@link LivingEntity}
     * or the center of the linked {@link BlockPos}.
     */
    public Vec3d getPos() {
        switch (type) {
            case ENTITY:
                return entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
            case BLOCK:
                return Vec3d.ofCenter(block);
        }
        throw new InconceivableException("No other hittable types");
    }

    /**
     * This is marked as client only because if the caster is a player, we want the origin to be
     * their physical hand.
     * Never use this outside of rendering purposes only.
     */
    public Vec3d getClientPos() {
        Vec3d pos = getPos();
        if (type == ENTITY && entity instanceof PlayerEntity) {
            if (pos == null) return null;
            float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - entity.bodyYaw));
            float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - entity.bodyYaw));
            return new Vec3d(offX, 0, offZ).add(pos);
        }

        return pos;
    }

    /**
     * Tells the "direction" this Interactor is facing. For {@link LivingEntity} Interactors
     * this is their look vector, for {@link BlockPos} Interactors it is the {@link Vec3d} form
     * of their {@link Direction}.
     */
    public Vec3d getLook() {
        switch (type) {
            case ENTITY:
                return entity.getRotationVector();
            case BLOCK:
                return Vec3d.of(dir.getVector());
        }
        throw new InconceivableException("No other hittable types");
    }
    
    public LivingEntity getEntity() { return entity; }
    
    public BlockPos getBlockPos() { return block; }
    
    public Direction getDir() { return this.dir; }
    
    public InteractorType getType() { return this.type; }
    
    /**
     * Drains mana from and adds burnout to the Interactor's target.
     * @return Returns {@code true} if enough mana was drained to cast the spell.
     * Returns {@code false} otherwise, although mana and burnout are still modified.
     */
    public boolean consumeCost(World world, double mana, double burnout)
    {
        switch (this.type)
        {
            case BLOCK:
                Block block = world.getBlockState(this.block).getBlock();
                if (block instanceof IManaNode)
                    return ((IManaNode) block).removeMana(world, this.block, mana) <= 0;
                return false;
            case ENTITY:
//                IManaCapability cap = this.entity.getCapability(MANA_CAPABILITY).orElse(null);
//                if (cap == null) return false;
//                cap.setBurnout(Math.min(cap.getBurnout() + burnout, cap.getMaxBurnout()));
//                if (cap.getMana() < mana)
//                {
//                    cap.setMana(0);
//                    return false;
//                }
//                cap.setMana(cap.getMana() - mana);
                return true;
        }
        throw new InconceivableException("No other hittable types");
    }
    
    public NbtCompound toNBT()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("type", type.toString());
        switch (type)
        {
            case ENTITY:
                nbt.putInt("entity", entity.getId());
                break;
            case BLOCK:
                NbtCompound pos = new NbtCompound();
                pos.putInt("x", block.getX());
                pos.putInt("y", block.getY());
                pos.putInt("z", block.getZ());
                nbt.put("block", pos);
                nbt.putInt("dir", dir.getId());
                break;
        }
        return nbt;
    }
    
    public static Interactor fromNBT(World world, NbtCompound nbt)
    {
        InteractorType type = InteractorType.valueOf(nbt.getString("type"));
        switch (type)
        {
            case ENTITY:
                Entity entity = world.getEntityById(nbt.getInt("entity"));
                if (entity instanceof LivingEntity)
                    return new Interactor((LivingEntity) entity);
                return null;
            case BLOCK:
                NbtCompound pos = nbt.getCompound("block");
                return new Interactor(new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z")), Direction.byId(nbt.getInt("dir")));
        }
        throw new InconceivableException("No other hittable types");
    }
}

