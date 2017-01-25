package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ModuleEventCollideEntity extends Module implements ITargettable {

    public ModuleEventCollideEntity() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.ROTTEN_FLESH);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EVENT;
    }

    @NotNull
    @Override
    public String getID() {
        return "on_collide_entity";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "On Collide Entity";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered when the spell collides with an entity";
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleEventCollideEntity clone = new ModuleEventCollideEntity();
        return clone;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Vec3d target) {
        return false;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Entity target) {
        return nextModule != null && nextModule instanceof ITargettable && ((ITargettable) nextModule).run(world, caster, target);
    }
}
