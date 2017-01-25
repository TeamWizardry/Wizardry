package com.teamwizardry.wizardry.common.module.effects;

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
public class ModuleEffectBlink extends Module implements ITargettable {

    public ModuleEffectBlink() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.CHORUS_FRUIT_POPPED);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EFFECT;
    }

    @NotNull
    @Override
    public String getID() {
        return "effect_blink";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Blink";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will teleport the target to the position of the spell";
    }

    @Override
    public double getManaToConsume() {
        return 50;
    }

    @Override
    public double getBurnoutToFill() {
        return 20;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Vec3d target) {
        return caster != null && target != null && caster.attemptTeleport(target.xCoord, target.yCoord, target.zCoord);
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Entity target) {
        return caster != null && target != null && caster.attemptTeleport(target.posX, target.posY, target.posZ);
    }

    @NotNull
    @Override
    public ModuleEffectBlink copy() {
        ModuleEffectBlink module = new ModuleEffectBlink();
        module.deserializeNBT(serializeNBT());
        return module;
    }
}
