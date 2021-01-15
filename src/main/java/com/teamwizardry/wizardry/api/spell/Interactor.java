package com.teamwizardry.wizardry.api.spell;

import static com.teamwizardry.wizardry.api.capability.mana.ManaCapability.MANA_CAPABILITY;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.ENTITY;

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException;
import com.teamwizardry.wizardry.api.block.IManaNode;
import com.teamwizardry.wizardry.api.capability.mana.IManaCapability;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
                return entity.getPositionVector().add(0, entity.getEyeHeight(entity.getPose()), 0);
            case BLOCK:
                return new Vec3d(block).add(0.5, 0.5, 0.5);
        }
        throw new InconceivableException("No other hittable types");
    }

    /**
     * This is marked as client only because if the caster is a player, we want the origin to be
     * their physical hand.
     * Never use this outside of rendering purposes only.
     */
    @OnlyIn(Dist.CLIENT)
    public Vec3d getClientPos() {
        Vec3d pos = getPos();
        if (type == ENTITY && entity instanceof PlayerEntity) {
            if (pos == null) return null;
            float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - entity.rotationYaw));
            float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - entity.rotationYaw));
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
                return entity.getLookVec();
            case BLOCK:
                return new Vec3d(dir.getDirectionVec());
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
                IManaCapability cap = this.entity.getCapability(MANA_CAPABILITY).orElse(null);
                if (cap == null) return false;
                cap.setBurnout(Math.min(cap.getBurnout() + burnout, cap.getMaxBurnout()));
                if (cap.getMana() < mana)
                {
                    cap.setMana(0);
                    return false;
                }
                cap.setMana(cap.getMana() - mana);
                return true;
        }
        throw new InconceivableException("No other hittable types");
    }
    
    public CompoundNBT toNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("type", type.toString());
        switch (type)
        {
            case ENTITY:
                nbt.putInt("entity", entity.getEntityId());
                break;
            case BLOCK:
                CompoundNBT pos = new CompoundNBT();
                pos.putInt("x", block.getX());
                pos.putInt("y", block.getY());
                pos.putInt("z", block.getZ());
                nbt.put("block", pos);
                nbt.putInt("dir", dir.getIndex());
                break;
        }
        return nbt;
    }
    
    public static Interactor fromNBT(World world, CompoundNBT nbt)
    {
        InteractorType type = InteractorType.valueOf(nbt.getString("type"));
        switch (type)
        {
            case ENTITY:
                Entity entity = world.getEntityByID(nbt.getInt("entity"));
                if (entity instanceof LivingEntity)
                    return new Interactor((LivingEntity) entity);
                return null;
            case BLOCK:
                CompoundNBT pos = nbt.getCompound("block");
                return new Interactor(new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z")), Direction.byIndex(nbt.getInt("dir")));
        }
        throw new InconceivableException("No other hittable types");
    }
}

